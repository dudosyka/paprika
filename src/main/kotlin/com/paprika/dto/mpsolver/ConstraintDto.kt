package com.paprika.dto.mpsolver

import com.paprika.database.models.dish.DishModel
import org.jetbrains.exposed.sql.Expression

data class ConstraintDto (
    var name: String = "Constraint",
    var modelKey: Expression<*> = DishModel.name,
    var bool: Boolean = false,
    var top: Double = 0.0,
    var bottom: Double = 0.0
)