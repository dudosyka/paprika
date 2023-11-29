package com.paprika.database.dao.ingredient

import com.paprika.database.models.ingredient.MeasureModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MeasureDao(id: EntityID<Int>): BaseIntEntity(id, MeasureModel) {
    companion object: BaseIntEntityClass<MeasureDao>(MeasureModel)

    val name by MeasureModel.name
    val nameFive by MeasureModel.nameFive
    val nameFractional by MeasureModel.nameFractional
    val nameTwo by MeasureModel.nameTwo
    val isDimensionless by MeasureModel.isDimensionless
}