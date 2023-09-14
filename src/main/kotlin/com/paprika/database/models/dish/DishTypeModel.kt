package com.paprika.database.models.dish

import com.paprika.utils.database.BaseIntIdTable

object DishTypeModel : BaseIntIdTable() {
    val name = text("name")
}