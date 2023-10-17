package com.paprika.services

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.paprika.database.models.dish.DishIngredientModel
import com.paprika.database.models.dish.DishModel
import com.paprika.database.models.ingredient.IngredientMeasureModel
import com.paprika.database.models.ingredient.IngredientModel
import com.paprika.database.models.ingredient.MeasureModel
import com.paprika.dto.upload.DatabaseStatisticOutputDto
import com.paprika.exceptions.BadRequestException
import com.paprika.utils.database.BaseIntIdTable
import com.paprika.utils.kodein.KodeinService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI

class DataManagerService(di: DI) : KodeinService(di) {
    private fun upload(data: ByteArray, model: BaseIntIdTable, modelKeys: Map<String, Column<*>>, doubleKeys: List<String> = listOf(), intKeys: List<String> = listOf(), boolKeys: List<String> = listOf(), rewrite: Boolean = false): List<ResultRow> = transaction {
        val pureData: List<Map<String, Any>> = csvReader().readAllWithHeader(String(data))


        if (modelKeys.keys.contains("id")) {
            if (rewrite) {
                model.deleteWhere {
                    (modelKeys["id"] as Column<Int>) inList (pureData.map { it["id"].toString().toInt() })
                }
            } else {
                val data = model.select {
                    (modelKeys["id"] as Column<Int>) inList (pureData.map { it["id"].toString().toInt() })
                }.map { it }
                if (data.isNotEmpty())
                    throw BadRequestException("Ids intersections")
            }
        }

        model.batchInsert(pureData) {
            modelKeys.forEach { modelKey ->
                if (doubleKeys.contains(modelKey.key))
                    this[modelKey.value as Column<Double>] = it[modelKey.key].toString().toDouble()
                else if (intKeys.contains(modelKey.key))
                    this[modelKey.value as Column<Int>] = it[modelKey.key].toString().toInt()
                else if (boolKeys.contains(modelKey.key))
                    this[modelKey.value as  Column<Boolean>] = it[modelKey.key].toString().toBoolean()
                else
                    this[modelKey.value as Column<String>] = it[modelKey.key].toString()
            }
        }
    }

    fun uploadMeasures(data: ByteArray, rewrite: Boolean) {
        this.upload(
            data,
            model = MeasureModel,
            modelKeys = mapOf(
                "id" to MeasureModel.id,
                "name" to MeasureModel.name,
            ),
            intKeys = listOf("id"),
            rewrite = rewrite
        )
    }

    fun uploadIngredients(ingredients: ByteArray, ingredientsMeasures: ByteArray, rewrite: Boolean) {
        this.upload(
            ingredients,
            model = IngredientModel,
            modelKeys = mapOf(
                "id" to IngredientModel.id,
                "name" to IngredientModel.name,
                "cellulose" to IngredientModel.cellulose
            ),
            rewrite = rewrite,
            intKeys = listOf("id", "measure")
        )

        this.upload(
            ingredientsMeasures,
            model = IngredientMeasureModel,
            modelKeys = mapOf(
                "ingredient" to IngredientMeasureModel.ingredient,
                "measure" to IngredientMeasureModel.measure,
                "topBound" to IngredientMeasureModel.topBound
            ),
            rewrite = rewrite,
        )
    }

    fun uploadDishes(dishData: ByteArray, dishToIngredientData: ByteArray, rewrite: Boolean) {
//        val dishIngredients = getDishIngredients(dishToIngredientData)

        this.upload(
            dishData,
            model = DishModel,
            modelKeys = mapOf(
                "id" to DishModel.id,
                "name" to DishModel.name,
                "logo" to DishModel.logo,
                "calories" to DishModel.calories,
                "protein" to DishModel.protein,
                "fat" to DishModel.fat,
                "carbohydrates" to DishModel.carbohydrates,
                "weight" to DishModel.weight,
                "timeToCook" to DishModel.timeToCook,
                "diet" to DishModel.diet,
                "type" to DishModel.type,
            ),
            doubleKeys = listOf(
                "calories", "protein", "fat", "carbohydrates", "weight"
            ),
            intKeys = listOf(
                "id", "timeToCook", "diet", "type"
            ),
            rewrite = rewrite,
        )

//        dishIngredients.forEach {
//            val ingredientsCellulose =
//        }
//        DishModel.update {  }

        this.upload(
            dishToIngredientData,
            model = DishIngredientModel,
            modelKeys = mapOf(
                "dish" to DishIngredientModel.dish,
                "ingredient" to DishIngredientModel.ingredient,
                "measure_count" to DishIngredientModel.measureCount
            ),
            intKeys = listOf(
                "dish", "ingredient"
            ),
            doubleKeys = listOf(
                "measure_count"
            ),
            rewrite = rewrite,
        )
    }

    fun getStatistic(): DatabaseStatisticOutputDto = transaction {
        val lastDishId = DishModel.selectAll().last()[DishModel.id].value
        val lastIngredientId = IngredientModel.selectAll().last()[IngredientModel.id].value
        val lastMeasureId = MeasureModel.selectAll().last()[MeasureModel.id].value
        DatabaseStatisticOutputDto(
            lastDishId, lastIngredientId, lastMeasureId
        )
    }
}