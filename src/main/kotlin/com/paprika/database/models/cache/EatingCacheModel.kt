package com.paprika.database.models.cache

import com.paprika.database.models.dish.DietModel
import com.paprika.database.models.dish.DishTypeModel
import com.paprika.utils.database.BaseIntIdTable

object EatingCacheModel: BaseIntIdTable() {
    //Diet options
    val calories = integer("calories")
    val protein = integer("protein")
    val fat = integer("fat")
    val carbohydrates = integer("carbohydrates")
    val cellulose = integer("cellulose")
    val diet = reference("diet", DietModel)

    //Eating options
    val size = integer("size")
    val time = integer("time")
    val difficulty = integer("difficulty")
    val type = reference("type", DishTypeModel)
    val dishCount = integer("dish_count")

    //System fields
    val useTimesFromLastScrap = integer("use_times_from_last_scrap")
    val useTimesFromCreation = integer("use_times_from_creation")
    val onRemove = bool("on_remove")
}