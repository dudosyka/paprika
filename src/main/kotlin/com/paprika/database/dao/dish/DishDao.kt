package com.paprika.database.dao.dish

import com.paprika.database.models.dish.DishIngredientModel
import com.paprika.database.models.dish.DishModel
import com.paprika.database.models.dish.DishStepModel
import com.paprika.dto.DishDto
import com.paprika.dto.MacronutrientsDto
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import com.paprika.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.transactions.transaction

class DishDao(id : EntityID<Int>) : BaseIntEntity(id, DishModel) {
    companion object : BaseIntEntityClass<DishDao>(DishModel)

    val name by DishModel.name
    val description by DishModel.description
    val portionsCount by DishModel.portionsCount
    val logo by DishModel.imageUrl
    val calories by DishModel.calories
    val protein by DishModel.protein
    val fat by DishModel.fat
    val carbohydrates by DishModel.carbohydrates
    val cellulose by DishModel.cellulose
    val timeToCook by DishModel.timeToCook

    val diet by DietDao referencedOn DishModel.diet
    private val _dietId by DishModel.diet
    val dietId
        get() = _dietId.value
    val type by DishTypeDao referencedOn DishModel.type
    private val _typeId by DishModel.type
    val typeId
        get() = _typeId.value
    val ingredients by DishIngredientDao referrersOn DishIngredientModel.dish

    val steps by DishStepDao referrersOn DishStepModel.dish
    fun toDto(): DishDto = transaction {
        DishDto(
            idValue,
            name,
            logo,
            calories,
            protein,
            fat,
            carbohydrates,
            cellulose,
            timeToCook,
            diet.idValue,
            type.idValue,
        )
    }
}

fun List<DishDao>.toDto(): List<DishDto> = map { it.toDto() }
fun SizedIterable<DishDao>.toDto(): List<DishDto> = toList().toDto()
fun List<DishDao>.countMicronutrients(): MacronutrientsDto =
    map {
        MacronutrientsDto(
            calories = it.calories,
            protein = it.protein,
            fat = it.fat,
            carbohydrates = it.carbohydrates,
            cellulose = it.cellulose
        )
    }.reduce {
        a, b -> run {
            MacronutrientsDto(
                calories = a.calories + b.calories,
                protein = a.protein + b.protein,
                fat = a.fat + b.fat,
                carbohydrates = a.carbohydrates + b.carbohydrates,
                cellulose = a.cellulose + b.cellulose
            )
        }
    }
fun SizedIterable<DishDao>.countMicronutrients(): MacronutrientsDto = toList().countMicronutrients()