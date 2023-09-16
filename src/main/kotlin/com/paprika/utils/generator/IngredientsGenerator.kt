package com.paprika.utils.generator

import com.paprika.database.dao.ingredient.IngredientDao
import com.paprika.database.models.ingredient.IngredientModel
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll

class IngredientsGenerator(count: Int, clearOld: Boolean = true): Generator<IngredientDao>(count, clearOld, 1) {
    override var generated: List<IngredientDao> = listOf()
    init {
        if (clearOld)
            IngredientModel.deleteAll()
        generated = IngredientModel.batchInsert(sequence) {
            this[IngredientModel.id] = it
            this[IngredientModel.name] = "Ingredient${it}"
            this[IngredientModel.measure] = it
        }.map {
            IngredientDao.wrapRow(it)
        }
    }

    fun getItems(): List<IngredientDao> {
        return generated
    }
}