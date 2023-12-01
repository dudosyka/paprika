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
    private val solverDelta = 0.25

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


        val solver = MPSolverService.initSolver {
            answersCount(paprikaInputDto.eatings[index].dishCount ?: 0)
            onDirection(MPSolverService.SolveDirection.MINIMIZE)

            setConstraint {
                name = "Calories"
                bottom = params.calories * (1.0 - solverDelta)
                top = params.calories * (1.0 + solverDelta)
                modelKey = DishModel.calories
            }
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
//            setConstraint {
//                name = "Cellulose"
//                bottom = params.minCellulose
//                top = params.maxCellulose
//                modelKey = DishModel.cellulose
//            }

            onData(dishes)
            withObjective(DishModel.timeToCook)
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
            idealParams = MicronutrientsDto(
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
                MicronutrientsDto(
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