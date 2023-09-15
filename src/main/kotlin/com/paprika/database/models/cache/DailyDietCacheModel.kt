package com.paprika.database.models.cache

import com.paprika.database.models.dish.DietModel
import com.paprika.utils.database.BaseIntIdTable

object DailyDietCacheModel: BaseIntIdTable() {
    //Diet options
    val calories = double("calories")
    val protein = double("protein")
    val fat = double("fat")
    val carbohydrates = double("carbohydrates")
    val cellulose = double("cellulose")
    val diet = reference("diet", DietModel)
    val eatingsCount = integer("eating_count")

    //System fields
    val useTimeFromLastScrap = integer("use_time_from_last_scrap")
    val useTimeFromCreation = integer("use_time_from_creation")
    val onRemove = bool("on_remove")
}