package com.paprika.services

import com.paprika.database.dao.dish.toDto
import com.paprika.database.models.dish.DishModel
import com.paprika.dto.*
import com.paprika.utils.kodein.KodeinService
import org.kodein.di.DI
import org.kodein.di.instance

class PaprikaService(di: DI) : KodeinService(di) {
    private val dishService: DishService by instance()

    private fun getParams(paprikaInputDto: PaprikaInputDto, eatingsCoef: Double = 1.0): ParametersInputDto {
        return if (paprikaInputDto.calories != null)
            ParametersInputDto(
                calories = paprikaInputDto.calories * eatingsCoef,

                minProtein = 0.0,
                maxProtein = paprikaInputDto.calories / 4 * eatingsCoef,

                minFat = 0.0,
                maxFat = paprikaInputDto.calories / 9 * eatingsCoef,

                minCarbohydrates = 0.0,
                maxCarbohydrates = paprikaInputDto.calories / 4 * eatingsCoef,

                minCellulose = 25.0 * eatingsCoef,
                maxCellulose = 50.0 * eatingsCoef,
            )
        else
            ParametersInputDto(
                calories = paprikaInputDto.idealMicronutrients!!.calories * eatingsCoef,

                minProtein = paprikaInputDto.idealMicronutrients.minProtein * eatingsCoef,
                maxProtein = paprikaInputDto.idealMicronutrients.maxProtein * eatingsCoef,

                minFat = paprikaInputDto.idealMicronutrients.minFat * eatingsCoef,
                maxFat = paprikaInputDto.idealMicronutrients.maxFat * eatingsCoef,

                minCarbohydrates = paprikaInputDto.idealMicronutrients.minCarbohydrates * eatingsCoef,
                maxCarbohydrates = paprikaInputDto.idealMicronutrients.maxCarbohydrates * eatingsCoef,

                minCellulose = paprikaInputDto.idealMicronutrients.minCellulose * eatingsCoef,
                maxCellulose = paprikaInputDto.idealMicronutrients.maxCellulose * eatingsCoef,
            )
    }

    private fun solveEating(paprikaInputDto: PaprikaInputDto, index: Int): EatingOutputDto {
        val params = getParams(paprikaInputDto, paprikaInputDto.eatings[index].size)

        val solver = MPSolverService.initSolver {
            answersCount(paprikaInputDto.eatings[index].dishCount)
            onDirection(MPSolverService.SolveDirection.MINIMIZE)

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

            onData(dishService.getDishesByEatingParams(paprikaInputDto.eatings[index], paprikaInputDto.diet))
            withObjective(DishModel.timeToCook)
        }

        val result = solver.solve()
        if (result.isEmpty())
            throw Exception("Can`t solve")

        val micronutrients = result.map {
            MicronutrientsDto(
                calories = it.calories,
                protein = it.protein,
                fat = it.fat,
                carbohydrates = it.carbohydrates,
                cellulose = it.cellulose
            )
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
        return EatingOutputDto(
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
    }

    fun calculateMenu(paprikaInputDto: PaprikaInputDto): PaprikaOutputDto {
        val eatings = List(paprikaInputDto.eatings.size) { index ->  solveEating(paprikaInputDto, index) }
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
            idealParams = getParams(paprikaInputDto)
        )
    }
}