package com.paprika.database.models.cache

import com.paprika.database.models.dish.DietModel
import com.paprika.utils.database.BaseIntIdTable

object DailyDietCacheModel: BaseIntIdTable() {
    //Diet options
    val calories = integer("calories")
    val protein = integer("protein")
    val fat = integer("fat")
    val carbohydrates = integer("carbohydrates")
    val cellulose = integer("cellulose")
    val diet = reference("diet", DietModel)
    val eatingsCount = integer("eating_count")

    //System fields
    val useTimeFromLastScrap = integer("use_time_from_last_scrap")
    val useTimeFromCreation = integer("use_time_from_creation")
    val onRemove = bool("on_remove")
}