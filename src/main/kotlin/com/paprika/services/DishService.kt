package com.paprika.services

import com.paprika.database.dao.dish.DishDao
import com.paprika.database.models.dish.DishModel
import com.paprika.dto.EatingOptionsDto
import com.paprika.utils.kodein.KodeinService
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI

class DishService(override val di: DI): KodeinService(di) {
    private fun difficultyCond(difficulty: Int): Op<Boolean> {
        return when (difficulty) {
            1 -> DishModel.timeToCook lessEq 5
            2 -> DishModel.timeToCook lessEq 15
            3 -> DishModel.timeToCook lessEq 30
            4 -> DishModel.timeToCook lessEq 45
            5 -> DishModel.timeToCook lessEq 60
            else -> DishModel.timeToCook lessEq 1000
        }
    }

    private fun dietCond(diet: Int): Op<Boolean> {
        return if (diet == 0)
            DishModel.diet neq 0
        else
            DishModel.diet eq diet
    }

    private fun typeCond(type: Int): Op<Boolean> {
        return if (type == 0)
            DishModel.type neq 0
        else
            DishModel.type eq type
    }

    fun getDishesByEatingParams(eatingOptionsDto: EatingOptionsDto, diet: Int): List<DishDao> = transaction {
        DishDao.find {
            dietCond(diet) and
            difficultyCond(eatingOptionsDto.difficulty) and
            typeCond(eatingOptionsDto.type)
        }.toList()
    }
}