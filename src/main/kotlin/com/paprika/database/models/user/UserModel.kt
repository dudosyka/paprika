package com.paprika.database.models.user

import com.paprika.utils.database.BaseIntIdTable

object UserModel: BaseIntIdTable() {
    val telegramId = integer("telegram_id").uniqueIndex()
    val sex = integer("sex")
    val height = double("height")
    val weight = double("weight")
    val birthday = integer("birthday")
    val active = integer("active")
}