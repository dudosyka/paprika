package com.paprika.database.models.cache

import com.paprika.utils.database.BaseIntIdTable

object DailyDietEatingsCacheModel: BaseIntIdTable() {
    val dailyDiet = reference("daily_diet", DailyDietCacheModel)
    val eating = reference("eating", EatingCacheModel)
}