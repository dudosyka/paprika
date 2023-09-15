package com.paprika.database.dao.cache

import com.paprika.database.dao.dish.DietDao
import com.paprika.database.dao.dish.DishDao
import com.paprika.database.dao.dish.DishTypeDao
import com.paprika.database.models.cache.EatingCacheDishesModel
import com.paprika.database.models.cache.EatingCacheModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class EatingCacheDao(id : EntityID<Int>): BaseIntEntity(id, EatingCacheModel) {
    companion object : BaseIntEntityClass<EatingCacheDao>(EatingCacheModel)

    val calories by EatingCacheModel.calories
    val protein by EatingCacheModel.protein
    val fat by EatingCacheModel.fat
    val carbohydrates by EatingCacheModel.carbohydrates
    val cellulose by EatingCacheModel.cellulose
    val diet by DietDao referencedOn EatingCacheModel.diet

    val size by EatingCacheModel.size
    val time by EatingCacheModel.time
    val difficulty by EatingCacheModel.difficulty
    val type by DishTypeDao referencedOn EatingCacheModel.type
    val dishCount by EatingCacheModel.dishCount
    val dishes by DishDao via EatingCacheDishesModel

    val useTimesFromLastScrap by EatingCacheModel.useTimesFromLastScrap
    val useTimesFromCreation by EatingCacheModel.useTimesFromCreation
    val onRemove by EatingCacheModel.onRemove
}