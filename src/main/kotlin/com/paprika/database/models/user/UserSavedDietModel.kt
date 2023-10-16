package com.paprika.database.models.user

import com.paprika.database.models.cache.EatingCacheModel
import com.paprika.utils.database.BaseIntIdTable

object UserSavedDietModel: BaseIntIdTable() {
    val name = text("name")
    val user = reference("user", UserModel)
    val cache = reference("cache", EatingCacheModel)
}