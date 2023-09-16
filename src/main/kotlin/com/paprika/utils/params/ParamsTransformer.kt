package com.paprika.utils.params

import com.paprika.dto.PaprikaInputDto
import com.paprika.dto.ParametersInputDto

object ParamsTransformer {
    operator fun invoke(paprikaInputDto: PaprikaInputDto, eatingsCoef: Double = 1.0): ParametersInputDto {
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
}