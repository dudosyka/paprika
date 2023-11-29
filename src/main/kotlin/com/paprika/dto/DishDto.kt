package com.paprika.dto

import com.paprika.database.models.dish.DishIngredientModel
import com.paprika.database.models.ingredient.IngredientMeasureModel
import com.paprika.database.models.ingredient.IngredientModel
import com.paprika.database.models.ingredient.MeasureModel
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class DishDto (
    val id: Int,
    val name: String,
    val logo: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val carbohydrates: Double,
    val cellulose: Double,
    val timeToCook: Int,
    val dietId: Int,
    val typeId: Int,
    var ingredients: List<IngredientDto> = listOf()
)

fun List<DishDto>.appendIngredients() = map { dish -> transaction {
    dish.ingredients =
        DishIngredientModel.leftJoin(IngredientModel).leftJoin(IngredientMeasureModel).innerJoin(MeasureModel).select {
            DishIngredientModel.dish eq dish.id
        }.map {
            IngredientDto(
                id = it[IngredientModel.id].value,
                name = it[IngredientModel.name],
                imageUrl = it[IngredientModel.imageUrl],
                measureType = MeasureDto(
                    name = it[MeasureModel.name],
                    nameFiveItems = it[MeasureModel.nameFive],
                    nameFractional = it[MeasureModel.nameFractional],
                    nameTwoItems = it[MeasureModel.nameTwo],
                    isDimensionless = it[MeasureModel.isDimensionless]
                ),
                measureCount = it[DishIngredientModel.measureCount],
            )
        }
    dish
} }