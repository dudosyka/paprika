package com.paprika.services

import com.paprika.database.dao.cache.EatingCacheDao
import com.paprika.database.dao.dish.DietDao
import com.paprika.database.dao.dish.DishTypeDao
import com.paprika.database.dao.dish.countMicronutrients
import com.paprika.database.dao.dish.toDto
import com.paprika.database.models.cache.EatingCacheDishesModel
import com.paprika.database.models.cache.EatingCacheModel
import com.paprika.dto.EatingOutputDto
import com.paprika.dto.MicronutrientsDto
import com.paprika.dto.PaprikaInputDto
import com.paprika.utils.database.idValue
import com.paprika.utils.kodein.KodeinService
import com.paprika.utils.params.ParamsManager
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI

class CacheService(di: DI) : KodeinService(di) {
    private fun createMinMaxCond(min: Double, max: Double, field: Column<Double>): Op<Boolean> {
        return ((field lessEq max) and (field greaterEq min))
    }

    private fun dietCond(diet: Int): Op<Boolean> {
        return if (diet == 0)
            EatingCacheModel.diet eq null
        else
            EatingCacheModel.diet eq diet
    }

    private fun typeCond(type: Int): Op<Boolean> {
        return if (type == 0)
            EatingCacheModel.type eq null
        else
            EatingCacheModel.type eq type
    }

    private fun dishCountCond(dishCount: Int?): Op<Boolean> {
        return if (dishCount != null)
            (EatingCacheModel.dishCount eq dishCount)
        else
            Op.nullOp()
    }

    fun saveEating(eatingOutputDto: EatingOutputDto, paprikaInputDto: PaprikaInputDto, index: Int) = transaction {
        val eatingInput = paprikaInputDto.eatings[index]
        val micronutrients = eatingOutputDto.idealMicronutrients
        val cache = EatingCacheDao.new {
            calories = micronutrients.calories
            protein = micronutrients.protein
            fat = micronutrients.fat
            carbohydrates = micronutrients.carbohydrates
            cellulose = micronutrients.cellulose
            if (paprikaInputDto.diet != 0)
                diet = DietDao[paprikaInputDto.diet]

            size = eatingInput.size
            difficulty = eatingInput.difficulty
            if (eatingInput.type != 0)
                type = DishTypeDao[eatingInput.type]
            dishCount = eatingOutputDto.dishes.size

            useTimesFromCreation = 0
            useTimesFromLastScrap = 0
            onRemove = false
        }

        EatingCacheDishesModel.batchInsert(eatingOutputDto.dishes) {
            this[EatingCacheDishesModel.dish] = it.id
            this[EatingCacheDishesModel.eatingCache] = cache.idValue
        }
    }

    fun findEating(paprikaInputDto: PaprikaInputDto, index: Int): EatingOutputDto? = transaction {
        val eatingOptions = paprikaInputDto.eatings[index]
        val params = ParamsManager.process {
            withSize(eatingOptions.size)
            fromPaprikaInput(paprikaInputDto)
        }.params


        val cache = EatingCacheDao.find {
            EatingCacheModel.excludeDishesFromList(paprikaInputDto.excludeDishes) and
            createMinMaxCond(params.minCellulose, params.maxCellulose, EatingCacheModel.cellulose) and
            createMinMaxCond(params.minProtein, params.maxProtein, EatingCacheModel.protein) and
            createMinMaxCond(params.minFat, params.maxFat, EatingCacheModel.fat) and
            createMinMaxCond(
                params.minCarbohydrates,
                params.maxCarbohydrates,
                EatingCacheModel.carbohydrates
            ) and
            createMinMaxCond(params.calories * 0.99, params.calories * 1.01, EatingCacheModel.calories) and
            dietCond(paprikaInputDto.diet) and
            typeCond(eatingOptions.type) and
            (EatingCacheModel.difficulty eq eatingOptions.difficulty) and
            dishCountCond(eatingOptions.dishCount)

        }.toList().filter {
            it.dishes.all { dish -> !paprikaInputDto.excludeDishes.contains(dish.idValue) }
        }

        if (cache.isNotEmpty()) {
            val result = cache.first()
            result.useTimesFromLastScrap++
            result.useTimesFromCreation++
            result.flush()
            EatingOutputDto(
                eatingOptions.name,
                result.dishes.toDto(),
                micronutrients = result.dishes.countMicronutrients(),
                idealMicronutrients = MicronutrientsDto(
                    calories = params.calories,
                    protein = params.maxProtein,
                    fat = params.maxFat,
                    carbohydrates = params.maxCarbohydrates,
                    cellulose = params.maxCellulose
                )
            )
        } else {
            null
        }
    }
}