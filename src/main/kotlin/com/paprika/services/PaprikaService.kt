package com.paprika.services

import com.paprika.database.dao.dish.countMicronutrients
import com.paprika.database.dao.dish.toDto
import com.paprika.database.models.dish.DishModel
import com.paprika.database.models.user.UserSavedDietModel
import com.paprika.dto.*
import com.paprika.dto.user.AuthorizedUser
import com.paprika.exceptions.CantSolveException
import com.paprika.utils.kodein.KodeinService
import com.paprika.utils.params.ParamsManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.kodein.di.DI
import org.kodein.di.instance
import java.time.LocalDateTime

class PaprikaService(di: DI) : KodeinService(di) {
    private val dishService: DishService by instance()
    private val cacheService: CacheService by instance()
    /*
        That param is used to auto calculate and validate params
        0.25 means that the delta of generated (or provided) params must be in range of [ calories * 0.75, calories * 1.25 ]
        In the other words it means amount of acceptable calculation error
     */
    private val solverDelta = 0.25

    /*

        Solve eating method works recursively, due to optimization we need to separate big amounts of Dishes into small batches
        (here we use 750 items in a row) this approach helps to maximize the algorithm work time.

        So to solve the eating which have amount of available dishes grater than 750
        we need recursion to invoke the same solving with different batches

        To provide to the method information about what batch should it process in current call we use offset argument by default it zero
        which means "take the first batch".

        When the offset 0 besides start solving we also must check cache and if in the database we have already had the eating that user need,
        and it is we simply return it.

     */
    private fun solveEating(
        paprikaInputDto: PaprikaInputDto,
        index: Int,
        maxima: Int = 0,
        offset: Long = 1
    ): Pair<EatingOutputDto, Int?> {
        println("Algorithm input: $paprikaInputDto")
        var dishesCount = 0
        if (offset.toInt() == 1) {
            val cache = cacheService.findEating(paprikaInputDto, index)
            if (cache != null)
                return cache

            println("Cache returns nothing :( start solving")
            dishesCount = dishService.getDishesIdByEatingParams(paprikaInputDto.eatings[index], paprikaInputDto).count()
            println(dishesCount)
        }

        val dishes = dishService.getDishesByEatingParams(paprikaInputDto.eatings[index], paprikaInputDto, offset)
        val eatingOptions = paprikaInputDto.eatings[index]
        val processedData = ParamsManager.process {
            withSize(eatingOptions.size)
            fromPaprikaInput(paprikaInputDto, solverDelta)
        }
        println("Processed Data ${processedData.calculatedFromParams}")
        val params = processedData.params

        if (dishesCount == 0 && maxima == 0)
            throw CantSolveException()

        //Here we are init the solver
        val solver = MPSolverService.initSolver {
            answersCount(paprikaInputDto.eatings[index].dishCount ?: 0)

            setConstraint {
                name = "Calories"
                bottom = params.calories * (1.0 - solverDelta)
                top = params.calories * (1.0 + solverDelta)
                modelKey = DishModel.calories
            }
            //We set constraints to params only if they are provided
            if (processedData.calculatedFromParams) {
                setConstraint {
                    name = "Protein"
                    bottom = params.minProtein
                    top = params.maxProtein
                    modelKey = DishModel.protein
                }
                setConstraint {
                    name = "Fat"
                    bottom = params.minFat
                    top = params.maxFat
                    modelKey = DishModel.fat
                }
                setConstraint {
                    name = "Carbohydrates"
                    bottom = params.minCarbohydrates
                    top = params.maxCarbohydrates
                    modelKey = DishModel.carbohydrates
                }
            }
//          Here was the cellulose constraint
//            setConstraint {
//                name = "Cellulose"
//                bottom = params.minCellulose
//                top = params.maxCellulose
//                modelKey = DishModel.cellulose
//            }

            // Provide the data that will be used for calculating
            onData(dishes)
            // Provide the objective, algorithm will be trying to optimize the calculation based of that variable
            withObjective(DishModel.timeToCook)
            // We set direction to minimize so in that case algorithm will be trying to found the dish with less time to cook
            onDirection(MPSolverService.SolveDirection.MINIMIZE)
        }

        val result = solver.solve()
        if (result.isEmpty()) {
            if ((maxima != 0 || dishesCount == 0) && maxima < offset * 750)
                throw CantSolveException()
            else {
                val nextMaxima = if (maxima == 0)
                    dishesCount
                else
                    maxima

                return solveEating(paprikaInputDto, index, nextMaxima, offset + 1)
            }
        }

        val micronutrients = result.countMicronutrients()

        return Pair(EatingOutputDto(
            name = eatingOptions.name,
            idealParams = MacronutrientsDto(
                calories = params.calories,
                protein = params.maxProtein,
                fat = params.maxFat,
                carbohydrates = params.maxCarbohydrates,
                cellulose = params.maxCellulose
            ),
            dishes = result.toDto(),
            params = micronutrients
        ), null)
    }

    /*

        That method is used to solve the whole daily diet,
        it simply calls the "solve eating" method for each eating in the requested diet and then processed it.

        After the eating solving we save it in cache (in the database) for future solvings
     */
    fun calculateMenu(authorizedUser: AuthorizedUser, paprikaInputDto: PaprikaInputDto): PaprikaOutputDto {
        transaction {
            UserSavedDietModel.update({ UserSavedDietModel.user eq authorizedUser.id }) {
                it[UserSavedDietModel.updatedAt] = LocalDateTime.now()
            }
        }
        val eatings = List(paprikaInputDto.eatings.size) {
            index ->  run {
                val eatingOutputDto = solveEating(paprikaInputDto, index)
//                eatingOutputDto.first.dishes = eatingOutputDto.first.dishes.appendIngredients()

                val cacheId = if (eatingOutputDto.second == null)
                    cacheService.saveEating(eatingOutputDto.first, paprikaInputDto, index)
                else
                    eatingOutputDto.second

                cacheService.saveUserDiet(authorizedUser.id, paprikaInputDto.eatings[index].name, cacheId!!)

                eatingOutputDto.first
            }
        }

        val params = eatings.mapIndexed { index, item ->
            item.params
        }.reduce {
            a, b -> run {
                MacronutrientsDto(
                    calories = a.calories + b.calories,
                    protein = a.protein + b.protein,
                    fat = a.fat + b.fat,
                    carbohydrates = a.carbohydrates + b.carbohydrates,
                    cellulose = a.cellulose + b.cellulose
                )
            }
        }

        return PaprikaOutputDto(
            diet = paprikaInputDto.diet,
            eatings = eatings,
            params = params,
            idealParams = ParamsManager.process {
                fromPaprikaInput(paprikaInputDto, solverDelta)
            }.params
        )
    }
}