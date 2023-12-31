package com.paprika.database.dao.cache

import com.paprika.database.dao.dish.*
import com.paprika.database.models.cache.EatingCacheDishesModel
import com.paprika.database.models.cache.EatingCacheModel
import com.paprika.dto.EatingOutputDto
import com.paprika.dto.MacronutrientsDto
import com.paprika.dto.ParametersDto
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable

class EatingCacheDao(id : EntityID<Int>): BaseIntEntity(id, EatingCacheModel) {
    companion object : BaseIntEntityClass<EatingCacheDao>(EatingCacheModel)

    var calories by EatingCacheModel.calories
    var protein by EatingCacheModel.protein
    var fat by EatingCacheModel.fat
    var carbohydrates by EatingCacheModel.carbohydrates
    var cellulose by EatingCacheModel.cellulose
    var diet by DietDao optionalReferencedOn EatingCacheModel.diet

    var size by EatingCacheModel.size
    var difficulty by EatingCacheModel.difficulty
    var type by DishTypeDao optionalReferencedOn EatingCacheModel.type
    var dishCount by EatingCacheModel.dishCount
    var dishes by DishDao via EatingCacheDishesModel

    var useTimesFromLastScrap by EatingCacheModel.useTimesFromLastScrap
    var useTimesFromCreation by EatingCacheModel.useTimesFromCreation
    var onRemove by EatingCacheModel.onRemove

    fun toDto(name: String, dishes: SizedIterable<DishDao>, params: ParametersDto?): EatingOutputDto =
        EatingOutputDto(
            name,
            dishes.toDto(),
            params = dishes.countMicronutrients(),
            idealParams = if (params != null) MacronutrientsDto(
                calories = params.calories,
                protein = params.maxProtein,
                fat = params.maxFat,
                carbohydrates = params.maxCarbohydrates,
                cellulose = params.maxCellulose
            ) else null
        )
}