package com.paprika.database.dao.dish

import com.paprika.database.models.dish.DietModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class DietDao(id : EntityID<Int>) : BaseIntEntity(id, DietModel) {
    companion object : BaseIntEntityClass<DietDao>(DietModel)

    val name by DietModel.name
}