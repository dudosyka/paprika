package com.paprika.database.models.cache

import com.paprika.database.models.dish.DietModel
import com.paprika.database.models.dish.DishTypeModel
import com.paprika.utils.database.BaseIntIdTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.select

object EatingCacheModel: BaseIntIdTable() {
    //Diet options
    val calories = double("calories")
    val protein = double("protein")
    val fat = double("fat")
    val carbohydrates = double("carbohydrates")
    val cellulose = double("cellulose")
    val diet = optReference("diet", DietModel)

    //Eating options
    val size = double("size")
    val difficulty = integer("difficulty")
    val type = optReference("type", DishTypeModel)
    val dishCount = integer("dish_count")

    //System fields
    val useTimesFromLastScrap = integer("use_times_from_last_scrap")
    val useTimesFromCreation = integer("use_times_from_creation")
    val onRemove = bool("on_remove")

    fun excludeDishesFromList(list: List<Int>): Op<Boolean> {
        val ids = EatingCacheDishesModel.select {
            (EatingCacheDishesModel.dish notInList list)
        }.map { it[EatingCacheDishesModel.eatingCache].value }

        return EatingCacheModel.id inList ids
    }
}