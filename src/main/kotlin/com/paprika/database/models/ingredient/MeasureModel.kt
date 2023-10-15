package com.paprika.database.models.ingredient

import com.paprika.utils.database.BaseIntIdTable

object MeasureModel: BaseIntIdTable() {
    val name = text("name")
}