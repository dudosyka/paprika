package com.paprika.services

import com.paprika.database.dao.dish.countMicronutrients
import com.paprika.database.dao.dish.toDto
import com.paprika.database.models.dish.DishIngredientModel
import com.paprika.database.models.dish.DishModel
import com.paprika.database.models.ingredient.IngredientMeasureModel
import com.paprika.database.models.ingredient.IngredientModel
import com.paprika.dto.*
import com.paprika.exceptions.CantSolveException
import com.paprika.utils.kodein.KodeinService
import com.paprika.utils.params.ParamsTransformer
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.instance

class PaprikaService(di: DI) : KodeinService(di) {
    private val dishService: DishService by instance()
    private val cacheService: CacheService by instance()

    private fun solveEating(paprikaInputDto: PaprikaInputDto, index: Int, maxima: Int = 0, offset: Long = 1): EatingOutputDto {
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
        val params = ParamsTransformer(paprikaInputDto, paprikaInputDto.eatings[index].size)

        if (dishesCount == 0)
            throw CantSolveException()


        val solver = MPSolverService.initSolver {
            answersCount(paprikaInputDto.eatings[index].dishCount)
            onDirection(MPSolverService.SolveDirection.MAXIMIZE)

            setConstraint {
                name = "Calories"
                bottom = params.calories * 0.99
                top = params.calories * 1.01
                modelKey = DishModel.calories
            }
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
            setConstraint {
                name = "Cellulose"
                bottom = params.minCellulose
                top = params.maxCellulose
                modelKey = DishModel.cellulose
            }

            onData(dishes)
            withObjective(DishModel.calories)
        }

        val result = solver.solve()
        if (result.isEmpty())
            if ((maxima != 0 || dishesCount == 0) && maxima < offset * 750)
                throw CantSolveException()
            else
                return solveEating(paprikaInputDto, index, dishesCount, offset + 1)

        val micronutrients = result.countMicronutrients()

        val output = EatingOutputDto(
            name = "Eating",
            idealMicronutrients = MicronutrientsDto(
                calories = params.calories,
                protein = params.maxProtein,
                fat = params.maxFat,
                carbohydrates = params.maxCarbohydrates,
                cellulose = params.maxCellulose
            ),
            dishes = result.toDto(),
            micronutrients = micronutrients
        )
        cacheService.saveEating(output, paprikaInputDto, index)

        return output
    }

    fun calculateMenu(paprikaInputDto: PaprikaInputDto): PaprikaOutputDto {
        var eatings = List(paprikaInputDto.eatings.size) { index ->  solveEating(paprikaInputDto, index) }
        eatings = eatings.map {
            it.dishes = it.dishes.map { dish -> transaction {
                dish.ingredients =
                    DishIngredientModel.innerJoin(IngredientModel).innerJoin(IngredientMeasureModel).select {
                        DishIngredientModel.dish eq dish.id
                    }.map {
                        IngredientDto(
                            id = it[IngredientModel.id].value,
                            name = it[IngredientModel.name],
                            measure = MeasureDto(
                                name = it[IngredientMeasureModel.name],
                                nameFiveItems = it[IngredientMeasureModel.nameFiveItems],
                                nameFractional = it[IngredientMeasureModel.nameFractional],
                                nameTwoItems = it[IngredientMeasureModel.nameTwoItems],
                                isDimensional = it[IngredientMeasureModel.isDimensional],
                            ),
                            measureCount = it[DishIngredientModel.measureCount],
                        )
                    }
                dish
            } }
            it
        }
        val params = eatings.map { it.micronutrients }.reduce {
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
            idealParams = ParamsTransformer(paprikaInputDto)
        )
    }
}