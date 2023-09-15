package com.paprika.database.models.cache

import com.paprika.database.models.dish.DishModel
import com.paprika.utils.database.BaseIntIdTable

object EatingCacheDishesModel: BaseIntIdTable() {
    val dish = reference("dish", DishModel)
    val eatingCache = reference("eating_cache", EatingCacheModel)
}