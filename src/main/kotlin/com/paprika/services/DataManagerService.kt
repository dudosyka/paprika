package com.paprika.services

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.paprika.database.models.dish.DishIngredientModel
import com.paprika.database.models.dish.DishModel
import com.paprika.database.models.ingredient.IngredientMeasureModel
import com.paprika.database.models.ingredient.IngredientModel
import com.paprika.utils.database.BaseIntIdTable
import com.paprika.utils.kodein.KodeinService
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI

class DataManagerService(di: DI) : KodeinService(di) {
    private fun upload(data: ByteArray, model: BaseIntIdTable, modelKeys: Map<String, Column<*>>, doubleKeys: List<String> = listOf(), intKeys: List<String> = listOf(), boolKeys: List<String> = listOf()) = transaction {
        val pureData: List<Map<String, Any>> = csvReader().readAllWithHeader(String(data))

        if (modelKeys.keys.contains("id"))
            model.deleteWhere {
                (modelKeys["id"] as Column<Int>) inList (pureData.map { it["id"].toString().toInt() })
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

    fun uploadMeasures(data: ByteArray) {
        this.upload(
            data,
            model = IngredientMeasureModel,
            modelKeys = mapOf(
                "id" to IngredientMeasureModel.id,
            ),
            intKeys = listOf("id"),
            boolKeys = listOf("isDimensional")
        )
    }

    fun uploadIngredients(data: ByteArray) {
        this.upload(
            data,
            model = IngredientModel,
            modelKeys = mapOf(
                "id" to IngredientModel.id,
                "name" to IngredientModel.name,
            ),
            intKeys = listOf("id", "measure")
        )
    }

    fun uploadDishes(dishData: ByteArray, dishToIngredientData: ByteArray) {
        this.upload(
            dishData,
            model = DishModel,
            modelKeys = mapOf(
                "id" to DishModel.id,
                "name" to DishModel.name,
                "calories" to DishModel.calories,
                "protein" to DishModel.protein,
                "fat" to DishModel.fat,
                "carbohydrates" to DishModel.carbohydrates,
                "cellulose" to DishModel.cellulose,
                "weight" to DishModel.weight,
                "timeToCook" to DishModel.timeToCook,
                "diet" to DishModel.diet,
                "type" to DishModel.type,
            ),
            doubleKeys = listOf(
                "calories", "protein", "fat", "carbohydrates", "cellulose", "weight"
            ),
            intKeys = listOf(
                "id", "timeToCook", "diet", "type"
            )
        )

        this.upload(
            dishToIngredientData,
            model = DishIngredientModel,
            modelKeys = mapOf(
                "dish" to DishIngredientModel.dish,
                "ingredient" to DishIngredientModel.ingredient,
                "count" to DishIngredientModel.measureCount
            ),
            intKeys = listOf(
                "dish", "ingredient"
            ),
            doubleKeys = listOf(
                "count"
            )
        )
    }
}