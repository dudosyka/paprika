package com.paprika.database.dao.dish

import com.paprika.database.models.dish.DishTypeModel
import com.paprika.dto.DishTypeOutputDto
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import com.paprika.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class DishTypeDao(id : EntityID<Int>) : BaseIntEntity(id, DishTypeModel) {
    companion object : BaseIntEntityClass<DishTypeDao>(DishTypeModel)

    val name by DishTypeModel.name

    fun toDto() =
        DishTypeOutputDto(idValue, name)
}