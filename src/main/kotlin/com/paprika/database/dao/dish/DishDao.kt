package com.paprika.database.dao.dish

import com.paprika.database.models.dish.DishModel
import com.paprika.dto.DishDto
import com.paprika.dto.MicronutrientsDto
import com.paprika.utils.database.BaseIntEntity
import com.paprika.utils.database.BaseIntEntityClass
import com.paprika.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.SizedIterable

class DishDao(id : EntityID<Int>) : BaseIntEntity(id, DishModel) {
    companion object : BaseIntEntityClass<DishDao>(DishModel)

    val name by DishModel.name
    val calories by DishModel.calories
    val protein by DishModel.protein
    val fat by DishModel.fat
    val carbohydrates by DishModel.carbohydrates
    val cellulose by DishModel.cellulose
    val weight by DishModel.weight
    val timeToCook by DishModel.timeToCook

    val diet by DietDao referencedOn DishModel.diet
    private val _dietId by DishModel.diet
    val dietId
        get() = _dietId.value
    val type by DishTypeDao referencedOn DishModel.type
    private val _typeId by DishModel.diet
    val typeId
        get() = _typeId.value

    fun toDto(): DishDto {
        return DishDto(
            idValue, name, calories, protein, fat, carbohydrates, cellulose, weight, timeToCook, dietId, typeId
        )
    }
}

fun List<DishDao>.toDto(): List<DishDto> = map { it.toDto() }
fun SizedIterable<DishDao>.toDto(): List<DishDto> = toList().toDto()
fun List<DishDao>.countMicronutrients(): MicronutrientsDto =
    map {
        MicronutrientsDto(
            calories = it.calories,
            protein = it.protein,
            fat = it.fat,
            carbohydrates = it.carbohydrates,
            cellulose = it.cellulose
        )
    }.reduce {
        a, b -> run {
            MicronutrientsDto(
                calories = a.calories + b.calories,
                protein = a.protein + b.protein,
                fat = a.fat + b.fat,
                carbohydrates = a.carbohydrates + b.carbohydrates,
                cellulose = a.cellulose + b.cellulose
            )
        }
    }
fun SizedIterable<DishDao>.countMicronutrients(): MicronutrientsDto = toList().countMicronutrients()