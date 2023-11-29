package com.paprika.database.models.ingredient

import com.paprika.utils.database.BaseIntIdTable

object MeasureModel: BaseIntIdTable() {
    val name = text("name")
    val nameFive = text("name_five")
    val nameFractional = text("name_fractional")
    val nameTwo = text("name_two")
    val isDimensionless = bool("is_dimensionless")
}