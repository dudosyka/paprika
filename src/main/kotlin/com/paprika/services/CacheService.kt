package com.paprika.services

import com.paprika.database.dao.cache.EatingCacheDao
import com.paprika.database.dao.dish.DietDao
import com.paprika.database.dao.dish.DishTypeDao
import com.paprika.database.dao.user.UserDao
import com.paprika.database.models.cache.EatingCacheDishesModel
import com.paprika.database.models.cache.EatingCacheModel
import com.paprika.database.models.user.UserModel
import com.paprika.database.models.user.UserSavedDietModel
import com.paprika.dto.*
import com.paprika.utils.database.idValue
import com.paprika.utils.kodein.KodeinService
import com.paprika.utils.params.ParamsManager
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import java.lang.Exception

class CacheService(di: DI) : KodeinService(di) {
    private fun createMinMaxCond(min: Double, max: Double, field: Column<Double>): Op<Boolean> {
        return ((field lessEq max) and (field greaterEq min))
    }

    private fun dietCond(diet: Int): Op<Boolean> {
        return if (diet == 0)
            EatingCacheModel.diet.isNull()
        else
            EatingCacheModel.diet eq diet
    }

    private fun typeCond(type: Int): Op<Boolean> {
        return if (type == 0)
            EatingCacheModel.type.isNull()
        else
            EatingCacheModel.type eq type
    }

    private fun dishCountCond(dishCount: Int?): Op<Boolean> {
        return if (dishCount != null && dishCount != 0)
            (EatingCacheModel.dishCount eq dishCount)
        else
            (EatingCacheModel.dishCount lessEq 10000)
    }

    fun saveEating(eatingOutputDto: EatingOutputDto, paprikaInputDto: PaprikaInputDto, index: Int): Int = transaction {
        val eatingInput = paprikaInputDto.eatings[index]
        val micronutrients = eatingOutputDto.idealParams ?: throw Exception()
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

        cache.idValue
    }

    fun saveUserDiet(userId: Int, eatingName: String, cacheId: Int) = transaction {
        UserSavedDietModel.deleteWhere {
            (user eq userId) and updatedAt.isNotNull()
        }
        UserSavedDietModel.insert {
            it[user] = userId
            it[cache] = cacheId
            it[name] = eatingName
        }
    }

    fun findEating(paprikaInputDto: PaprikaInputDto, index: Int): Pair<EatingOutputDto, Int>? = transaction {
        val eatingOptions = paprikaInputDto.eatings[index]
        val params = ParamsManager.process {
            withSize(eatingOptions.size)
            fromPaprikaInput(paprikaInputDto)
        }.params

        println(params)
        println(paprikaInputDto)
        println(eatingOptions)

        val cache = EatingCacheDao.find {
            EatingCacheModel.excludeDishesFromList(paprikaInputDto.excludeDishes).apply { println("Excluded $this") } and
            createMinMaxCond(params.minCellulose, params.maxCellulose, EatingCacheModel.cellulose).apply { println("Cellulose $this") } and
            createMinMaxCond(params.minProtein, params.maxProtein, EatingCacheModel.protein).apply { println("Protein $this") } and
            createMinMaxCond(params.minFat, params.maxFat, EatingCacheModel.fat).apply { println("Fat $this") } and
            createMinMaxCond(
                params.minCarbohydrates,
                params.maxCarbohydrates,
                EatingCacheModel.carbohydrates
            ).apply { println("Carbo $this") } and
            createMinMaxCond(params.calories * 0.99, params.calories * 1.01, EatingCacheModel.calories).apply { println("Calories $this") } and
            dietCond(paprikaInputDto.diet).apply { println("Diet $this") } and
            typeCond(eatingOptions.type).apply { println("Type $this") } and
            (EatingCacheModel.difficulty eq eatingOptions.difficulty).apply { println("Diff $this") } and
            dishCountCond(eatingOptions.dishCount).apply { println("Count $this") }
        }.toList().apply { println(this@apply) }.filter {
            it.dishes.all { dish -> !paprikaInputDto.excludeDishes.contains(dish.idValue) }
        }

        println(cache)

        if (cache.isNotEmpty()) {
            val result = cache.first()
            println(result.idValue)
            result.useTimesFromLastScrap++
            result.useTimesFromCreation++
            result.flush()
            Pair(result.toDto(eatingOptions.name, result.dishes, params), result.idValue)
        } else {
            null
        }
    }

    fun loadUserSaved(userId: Int): PaprikaOutputDto = transaction {
        val user = UserDao.find {
            UserModel.id eq userId
        }.first()

        val list: List<Column<*>> = run {
            val list = EatingCacheModel.columns.toMutableList()
            list.add(UserSavedDietModel.name)
            list
        }

        val eatingsOutputDtoList = UserSavedDietModel.leftJoin(EatingCacheModel).slice(list).select(UserSavedDietModel.user eq userId).map {
            val name = it[UserSavedDietModel.name]

            EatingCacheDao.wrapRow(it).run {
                toDto(name, dishes, ParametersDto.buildFromCache(it))
            }
        }
//        .map { eatingOutputDto -> run {
//            eatingOutputDto.dishes = eatingOutputDto.dishes.appendIngredients()
//            eatingOutputDto
//        } }

        val paprikaOutputDto = PaprikaOutputDto(
            diet = user.params.diet?.idValue ?: 0,
            eatings = eatingsOutputDtoList,
            idealParams = eatingsOutputDtoList.map { ParametersDto.buildFromMacronutrients(it.idealParams!!) }.reduce {
                f, s -> ParametersDto(
                    calories = f.calories + s.calories,
                    minCellulose = f.minCellulose + s.minCellulose,
                    maxCellulose = f.maxCellulose + s.maxCellulose,
                    minProtein = f.minProtein + s.minProtein,
                    maxProtein = f.maxProtein + s.maxProtein,
                    minFat = f.minFat + s.minFat,
                    maxFat = f.maxFat + s.maxFat,
                    minCarbohydrates = f.minCarbohydrates + s.minCarbohydrates,
                    maxCarbohydrates = f.maxCarbohydrates + s.maxCarbohydrates
                )
            },
            params = eatingsOutputDtoList.map { it.params }.reduce { f, s -> MacronutrientsDto(
                calories = f.calories + s.calories,
                carbohydrates = f.carbohydrates + s.carbohydrates,
                fat = f.fat + s.fat,
                protein = f.protein + s.protein,
                cellulose = f.cellulose + s.cellulose
            ) }
        )

        paprikaOutputDto
    }
}