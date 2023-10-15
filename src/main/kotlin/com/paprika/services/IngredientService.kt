package com.paprika.services

import com.paprika.database.models.ingredient.IngredientMeasureModel
import com.paprika.database.models.ingredient.IngredientModel
import com.paprika.dto.IngredientsMeasureInputDto
import com.paprika.dto.IngredientsMeasureOutputDto
import com.paprika.utils.kodein.KodeinService
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI

class IngredientService(di: DI) : KodeinService(di) {
    fun getCorrectMeasures(input: IngredientsMeasureInputDto): IngredientsMeasureOutputDto = transaction {
        val outputMap = input.toMap()
        IngredientModel.leftJoin(IngredientMeasureModel).select {
            IngredientModel.id inList input.getIds()
        }.forEach {
            val ingredient = outputMap.getValue(it[IngredientModel.id].value)
            val topBound = it[IngredientMeasureModel.topBound]

            if (ingredient.count <= topBound && (topBound <= ingredient.bound || ingredient.measure == null)) {
                ingredient.bound = topBound
                ingredient.measure = it[IngredientMeasureModel.measure].value
            }
        }

        IngredientsMeasureOutputDto(outputMap)
    }
}