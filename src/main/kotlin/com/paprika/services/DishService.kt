package com.paprika.services

import com.paprika.database.dao.dish.DishDao
import com.paprika.database.models.cache.EatingCacheModel
import com.paprika.database.models.dish.DishModel
import com.paprika.dto.EatingOptionsDto
import com.paprika.dto.PaprikaInputDto
import com.paprika.utils.kodein.KodeinService
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
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

    private fun createDishByParamsCond(eatingOptionsDto: EatingOptionsDto, paprikaInputDto: PaprikaInputDto): Op<Boolean> =
        DishModel.id notInList paprikaInputDto.excludeDishes and
        dietCond(paprikaInputDto.diet) and
        difficultyCond(eatingOptionsDto.difficulty) and
        typeCond(eatingOptionsDto.type)

    fun getDishesIdByEatingParams(eatingOptionsDto: EatingOptionsDto, paprikaInputDto: PaprikaInputDto): List<Int> = transaction {
        DishModel.slice(listOf(DishModel.id)).select { createDishByParamsCond(eatingOptionsDto, paprikaInputDto) }.map { it[DishModel.id].value }
    }

    fun getDishesByEatingParams(eatingOptionsDto: EatingOptionsDto, paprikaInputDto: PaprikaInputDto, offset: Long = 1): List<DishDao> = transaction {
        DishDao.find {
            createDishByParamsCond(eatingOptionsDto, paprikaInputDto)
        }.limit(750, offset = offset).toList()
    }
}