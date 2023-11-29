package com.paprika.database.models.cusine

import com.paprika.utils.database.BaseIntIdTable

object CusineModel: BaseIntIdTable() {
    val cusine = text("cusine")
    val category = text("category")
}