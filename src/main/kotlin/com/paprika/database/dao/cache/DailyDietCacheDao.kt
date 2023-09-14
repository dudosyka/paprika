package com.paprika.database.dao.cache

import com.paprika.database.dao.dish.DietDao
import com.paprika.database.models.cache.DailyDietCacheModel
import com.paprika.database.models.cache.DailyDietEatingsCacheModel
import com.paprika.database.models.cache.EatingCacheModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DailyDietCacheDao(id : EntityID<Int>): BaseIntEntity(id, DailyDietCacheModel) {
    companion object : BaseIntEntityClass<DailyDietCacheDao>(DailyDietCacheModel)

    val calories by DailyDietCacheModel.calories
    val protein by DailyDietCacheModel.protein
    val fat by DailyDietCacheModel.fat
    val carbohydrates by DailyDietCacheModel.carbohydrates
    val cellulose by DailyDietCacheModel.cellulose
    val diet by DietDao referencedOn DailyDietCacheModel.diet
    val eatingsCount by DailyDietCacheModel.eatingsCount

    val eatings by EatingCacheDao via DailyDietEatingsCacheModel

    val useTimesFromLastScrap by EatingCacheModel.useTimesFromLastScrap
    val useTimesFromCreation by EatingCacheModel.useTimesFromCreation
    val onRemove by EatingCacheModel.onRemove
}