package com.paprika.utils.params

import com.paprika.dto.PaprikaInputDto
import com.paprika.dto.ParametersInputDto
import com.paprika.exceptions.CantSolveException

class ParamsManager internal constructor() {
    lateinit var params: ParametersInputDto
    private var calories: Double = 0.0
    private var eatingsCoef: Double = 1.0

    companion object {
        fun process(apply: ParamsManager.() -> Unit): ParamsManager {
            return ParamsManager().apply(apply)
        }
    }

    fun withSize(coef: Double) {
        this.eatingsCoef = coef
    }
    fun fromPaprikaInput(paprikaInputDto: PaprikaInputDto) {
        if (paprikaInputDto.calories != null)
            fromCalories(paprikaInputDto.calories)
        else if (paprikaInputDto.idealMicronutrients != null)
            fromParams(paprikaInputDto.idealMicronutrients)
        else
            throw CantSolveException("You must provide either macronutrients params or calories")
    }
    fun fromCalories(calories: Double?) {
        if (calories == null)
            return
        this.calories = calories
        val protein = createMinMaxValue(4)
        val fat = createMinMaxValue(9)
        val carbohydrates = createMinMaxValue(4)
        this.params = ParametersInputDto(
            calories = this.calories * eatingsCoef,

            minProtein = protein.first,
            maxProtein = protein.second,

            minFat = fat.first,
            maxFat = fat.second,

            minCarbohydrates = carbohydrates.first,
            maxCarbohydrates = carbohydrates.second,

            minCellulose = 25.0 * eatingsCoef,
            maxCellulose = 50.0 * eatingsCoef,
        )
    }
    fun fromParams(params: ParametersInputDto?) {
        if (params == null)
            return
        this.validateParams(params)
        this.params = ParametersInputDto(
            calories = params.calories * eatingsCoef,

            minProtein = params.minProtein,
            maxProtein = params.maxProtein,

            minFat = params.minFat,
            maxFat = params.maxFat,

            minCarbohydrates = params.minCarbohydrates,
            maxCarbohydrates = params.maxCarbohydrates,

            minCellulose = params.minCellulose,
            maxCellulose = params.maxCellulose,
        )
    }

    private fun transformToCalories(value: Double, multiplier: Int = 4): Double {
        return value * multiplier
    }

    private fun summariseParams(params: ParametersInputDto): Double {
        return transformToCalories(params.minProtein) + transformToCalories(params.minCarbohydrates) + transformToCalories(params.minFat, 9)
    }

    private fun validateParams(params: ParametersInputDto) {
        if (params.calories < summariseParams(params))
            throw CantSolveException("Bad macronutrients params were provided!")
    }

    private fun createMinMaxValue(divider: Int = 4, delta: Double = 1.0): Pair<Double, Double> {
        val value = calories / divider * eatingsCoef
        return Pair(
            value * (1 - delta),
            value * (1 + delta)
        )
    }

    operator fun invoke(paprikaInputDto: PaprikaInputDto, eatingsCoef: Double = 1.0): ParametersInputDto {
        return if (paprikaInputDto.calories != null) {
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
        } else
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
}