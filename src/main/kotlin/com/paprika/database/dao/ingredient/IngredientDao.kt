package com.paprika.database.dao.ingredient

import com.paprika.database.models.ingredient.IngredientModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class IngredientDao(id : EntityID<Int>): BaseIntEntity(id, IngredientModel) {
    companion object : BaseIntEntityClass<IngredientDao>(IngredientModel)

    val name by IngredientModel.name
    val cellulose by IngredientModel.cellulose
    val imageUrl by IngredientModel.imageUrl
    val relativeUrl by IngredientModel.relativeUrl
}