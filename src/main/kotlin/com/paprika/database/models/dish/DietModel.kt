package com.paprika.database.models.dish

import com.paprika.utils.database.BaseIntIdTable

object DietModel: BaseIntIdTable() {
    val name = text("name")
}