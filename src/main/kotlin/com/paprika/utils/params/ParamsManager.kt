package com.paprika.utils.params

import com.paprika.dto.PaprikaInputDto
import com.paprika.dto.ParametersDto
import com.paprika.exceptions.CantSolveException

class ParamsManager internal constructor() {
    lateinit var params: ParametersDto
    private var calories: Double = 0.0
    private var eatingsCoef: Double = 1.0
    var calculatedFromParams: Boolean = false
    private val calculateDelta = 0.1

    companion object {
        fun process(apply: ParamsManager.() -> Unit): ParamsManager {
            return ParamsManager().apply(apply)
        }
    }

    fun withSize(coef: Double) {
        this.eatingsCoef = coef
    }
    fun fromPaprikaInput(paprikaInputDto: PaprikaInputDto, delta: Double = 1.0) {
        if (paprikaInputDto.calories != null)
            fromCalories(paprikaInputDto.calories, delta)
        else if (paprikaInputDto.params != null)
            fromParams(paprikaInputDto.params, delta)
        else
            throw CantSolveException("You must provide either macronutrients params or calories")
    }
    fun fromCalories(calories: Double?, delta: Double = 1.0) {
        if (calories == null)
            return
        this.calories = calories
        val protein = createMinMaxValue(4, delta)
        val fat = createMinMaxValue(9, delta)
        val carbohydrates = createMinMaxValue(4, delta)
        this.params = ParametersDto(
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
    fun fromParams(params: ParametersDto?, delta: Double = 0.0) {
        calculatedFromParams = true
        if (params == null)
            return
        this.validateParams(params)
        this.params = ParametersDto(
            calories = params.calories * eatingsCoef,

            minProtein = params.minProtein * eatingsCoef * (1.0 - delta),
            maxProtein = params.maxProtein * eatingsCoef * (1.0 + delta),

            minFat = params.minFat * eatingsCoef * (1.0 - delta),
            maxFat = params.maxFat * eatingsCoef * (1.0 - delta),

            minCarbohydrates = params.minCarbohydrates * eatingsCoef * (1.0 - delta),
            maxCarbohydrates = params.maxCarbohydrates * eatingsCoef * (1.0 - delta),

            minCellulose = params.minCellulose * eatingsCoef * (1.0 - delta),
            maxCellulose = params.maxCellulose * eatingsCoef * (1.0 - delta),
        )
    }

    private fun transformToCalories(value: Double, multiplier: Int = 4): Double {
        return value * multiplier
    }

    private fun summariseMinParams(params: ParametersDto): Double {
        return transformToCalories(params.minProtein) + transformToCalories(params.minCarbohydrates) + transformToCalories(params.minFat, 9)
    }

    private fun summariseMaxParams(params: ParametersDto): Double {
        return transformToCalories(params.maxProtein) + transformToCalories(params.maxCarbohydrates) + transformToCalories(params.maxFat, 9)
    }

    fun validateParams(params: ParametersDto) {
        if (params.calories * (1.0 + calculateDelta) < summariseMinParams(params) || summariseMaxParams(params) > params.calories * (1.0 + calculateDelta))
            throw CantSolveException("Bad macronutrients params were provided!")
    }

    private fun createMinMaxValue(divider: Int = 4, delta: Double = 1.0): Pair<Double, Double> {
        val value = (calories / 3) / divider * eatingsCoef
        return Pair(
            value * (1 - delta),
            value * (1 + delta)
        )
    }

    operator fun invoke(paprikaInputDto: PaprikaInputDto, eatingsCoef: Double = 1.0): ParametersDto {
        return if (paprikaInputDto.calories != null) {
            ParametersDto(
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
            ParametersDto(
                calories = paprikaInputDto.params!!.calories * eatingsCoef,

                minProtein = paprikaInputDto.params.minProtein * eatingsCoef,
                maxProtein = paprikaInputDto.params.maxProtein * eatingsCoef,

                minFat = paprikaInputDto.params.minFat * eatingsCoef,
                maxFat = paprikaInputDto.params.maxFat * eatingsCoef,

                minCarbohydrates = paprikaInputDto.params.minCarbohydrates * eatingsCoef,
                maxCarbohydrates = paprikaInputDto.params.maxCarbohydrates * eatingsCoef,

                minCellulose = paprikaInputDto.params.minCellulose * eatingsCoef,
                maxCellulose = paprikaInputDto.params.maxCellulose * eatingsCoef,
            )
    }
}