package com.paprika.database.models.dish

import com.paprika.utils.database.BaseIntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object DishStepModel: BaseIntIdTable() {
    val relative_id = integer("relative_id")
    val description = text("description")
    val imageUrl = text("image_url")
    val dish = reference("dish", DishModel, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
}