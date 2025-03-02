package com.paprika.services

import com.paprika.database.dao.dish.DishDao
import com.paprika.database.models.dish.DishModel
import com.paprika.database.models.dish.DishStepModel
import com.paprika.dto.*
import com.paprika.utils.database.idValue
import com.paprika.utils.kodein.KodeinService
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notInList
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
//        dietCond(paprikaInputDto.diet) and
        difficultyCond(eatingOptionsDto.difficulty)
//        typeCond(eatingOptionsDto.type)

    fun getDishesIdByEatingParams(eatingOptionsDto: EatingOptionsDto, paprikaInputDto: PaprikaInputDto): List<Int> = transaction {
        DishModel.slice(listOf(DishModel.id)).select { createDishByParamsCond(eatingOptionsDto, paprikaInputDto) }.map { it[DishModel.id].value }
    }

    fun getDishesByEatingParams(eatingOptionsDto: EatingOptionsDto, paprikaInputDto: PaprikaInputDto, offset: Long = 1): List<DishDao> = transaction {
        DishDao.find {
            createDishByParamsCond(eatingOptionsDto, paprikaInputDto)
        }.limit(750, offset = offset).toList()
    }

    fun getDishesRecipe(dishesIds: List<Int>): List<DishRecipeOutput> = transaction {
        DishDao.find {
            DishModel.id inList dishesIds
        }.map {
            dish -> run {
                DishRecipeOutput(
                    id = dish.idValue,
                    ingredients = dish.ingredients.map {
                        val ingredientDao = it.ingredient
                        val measureDao = it.measure
                        IngredientDto(
                            id = ingredientDao.idValue,
                            name = ingredientDao.name,
                            imageUrl = ingredientDao.imageUrl,
                            measureType = MeasureDto(
                                name = measureDao.name,
                                nameFiveItems = measureDao.nameFive,
                                nameFractional = measureDao.nameFractional,
                                nameTwoItems = measureDao.nameTwo,
                                isDimensionless = measureDao.isDimensionless
                            ),
                            measureCount = it.measureCount
                        )
                    },
                    steps = dish.steps.map {
                        DishRecipeStepDto(
                            text = it.description,
                            imageUrl = it.imageUrl,
                            relativeId = it.relativeId
                        )
                    }
                )
            }
        }
    }

    fun removeSimple(id: Int?): Any = transaction {
        DishModel.deleteWhere {
            DishModel.id eq id
        }
        ""
    }

    fun createSimple(dish: SIMPLEDishCreate): Any = transaction {
        DishModel.insert {
            it[name] = dish.name
            it[description] = dish.description
            it[portionsCount] = dish.portions
            it[imageUrl] = dish.image
            it[calories] = dish.calories
            it[protein] = dish.protein
            it[fat] = dish.fats
            it[carbohydrates] = dish.carbo
            it[cellulose] = 0.0
            it[timeToCook] = dish.time
            it[diet] = dish.diet
            it[type] = dish.type
        }
        ""
    }

    fun getDishesSimple(): Any = transaction {
        DishModel
            .selectAll()
            .map {
                SIMPLEDishOutput(
                    it[DishModel.id].value,
                    it[DishModel.protein],
                    it[DishModel.fat],
                    it[DishModel.carbohydrates],
                    it[DishModel.calories],
                    it[DishModel.imageUrl],
                    it[DishModel.description],
                    it[DishModel.name],
                    it[DishModel.portionsCount],
                    it[DishModel.timeToCook],
                    it[DishModel.diet].value,
                    it[DishModel.type].value,
                )
            }
    }


}