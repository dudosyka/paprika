package com.paprika.services

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.paprika.database.models.cusine.CusineModel
import com.paprika.database.models.dish.DishIngredientModel
import com.paprika.database.models.dish.DishModel
import com.paprika.database.models.dish.DishStepModel
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
                else if (intKeys.contains(modelKey.key)) {
                    val value = when (modelKey.key) {
                        "calories" -> it[modelKey.key].toString().toInt() / 1000
                        "diet" -> 1
                        "type" -> 1
                        else -> it[modelKey.key].toString().toInt()
                    }

                    this[modelKey.value as Column<Int>] = value
                }
                else if (boolKeys.contains(modelKey.key))
                    this[modelKey.value as  Column<Boolean>] = (it[modelKey.key].toString() == "True")
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
                "measureUnit_id" to MeasureModel.id,
                "measureUnit_isDimensionless" to MeasureModel.isDimensionless,
                "measureUnit_name" to MeasureModel.name,
                "measureUnit_nameFive" to MeasureModel.nameFive,
                "measureUnit_nameFractional" to MeasureModel.nameFractional,
                "measureUnit_nameTwo" to MeasureModel.nameTwo,
            ),
            intKeys = listOf("measureUnit_id"),
            boolKeys = listOf("measureUnit_isDimensionless"),
            rewrite = rewrite
        )
    }

    fun uploadIngredients(ingredients: ByteArray, rewrite: Boolean) {
        upload(
            ingredients,
            model = IngredientModel,
            modelKeys = mapOf(
                "ingr_id" to IngredientModel.id,
                "catalogPhoto" to IngredientModel.imageUrl,
                "name" to IngredientModel.name,
                "relativeUrl" to IngredientModel.relativeUrl
            ),
            rewrite = rewrite,
            intKeys = listOf("ingr_id")
        )
    }

    fun uploadDishes(dishData: ByteArray, dishToIngredientData: ByteArray, dishSteps: ByteArray, dishCategories: ByteArray, rewrite: Boolean) {

        upload(
            dishCategories,
            model = CusineModel,
            modelKeys = mapOf(
                "id" to CusineModel.id,
                "cuisine" to CusineModel.cusine,
                "recipeCategory" to CusineModel.category
            ),
            intKeys = listOf("id")
        )

        upload(
            dishData,
            model = DishModel,
            modelKeys = mapOf(
                "recipe_id" to DishModel.id,
                "cookingTime" to DishModel.timeToCook,
                "description" to DishModel.description,
                "name" to DishModel.name,
                "openGraphImageUrl" to DishModel.imageUrl,

                "portionsCount" to DishModel.portionsCount,
                "carbohydrates" to DishModel.carbohydrates,
                "fats" to DishModel.fat,
                "proteins" to DishModel.protein,
                "calories" to DishModel.calories,

                "diet" to DishModel.diet,
                "type" to DishModel.type,
            ),
            doubleKeys = listOf(
                "carbohydrates", "fats", "proteins"
            ),
            intKeys = listOf(
                "recipe_id", "portionsCount", "cookingTime", "diet", "type", "calories"
            ),
            rewrite = rewrite,
        )

        upload(
            dishToIngredientData,
            model = DishIngredientModel,
            modelKeys = mapOf(
                "recipe_id" to DishIngredientModel.dish,
                "measureUnit_id" to DishIngredientModel.measure,
                "ingr_id" to DishIngredientModel.ingredient,
                "amount" to DishIngredientModel.measureCount
            ),
            intKeys = listOf(
                "recipe_id", "ingr_id", "measureUnit_id"
            ),
            doubleKeys = listOf(
                "amount"
            ),
            rewrite = rewrite,
        )

        upload(
            dishSteps,
            model = DishStepModel,
            modelKeys = mapOf(
                "id" to DishStepModel.id,
                "step_id" to DishStepModel.relative_id,
                "recipe_id" to DishStepModel.dish,
                "imageUrl" to DishStepModel.imageUrl,
                "description" to DishStepModel.description
            ),
            intKeys = listOf(
                "id", "step_id", "recipe_id"
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