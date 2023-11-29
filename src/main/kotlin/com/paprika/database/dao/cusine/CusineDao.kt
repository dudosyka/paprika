package com.paprika.database.dao.cusine

import com.paprika.database.models.cusine.CusineModel
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CusineDao(id: EntityID<Int>): BaseIntEntity(id, CusineModel) {
    companion object : BaseIntEntityClass<CusineDao>(CusineModel)

    val cusine by CusineModel.cusine
    val category by CusineModel.category
}