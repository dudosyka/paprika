package com.paprika.utils.generator

import com.paprika.database.dao.dish.DishDao
import com.paprika.database.models.dish.DietModel
import com.paprika.database.models.dish.DishModel
import com.paprika.database.models.dish.DishTypeModel
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteAll

class DishGenerator(count: Int, clearOld: Boolean = true, startIndex: Int = 1): Generator<DishDao>(count, clearOld, startIndex) {
    override var generated: List<DishDao> = listOf()

    init {
        if (clearOld) {
            DishModel.deleteAll()
            DietModel.deleteAll()
            DishTypeModel.deleteAll()
        }

        if (startIndex == 1) {
            DietModel.batchInsert(sequence.take(5)) {
                this[DietModel.id] = it
                this[DietModel.name] = "Diet${it}"
            }

            DishTypeModel.batchInsert(sequence.take(5)) {
                this[DishTypeModel.id] = it
                this[DishTypeModel.name] = "DishType${it}"
            }
        }

        generated = DishModel.batchInsert(sequence) {
            this[DishModel.id] = it
            this[DishModel.name] = "Dish $it"
            this[DishModel.logo] = "Logo $it"
            this[DishModel.calories] = getRandomValue<Double>(100, 500)
            this[DishModel.protein] = getRandomValue<Double>(1, 10)
            this[DishModel.fat] = getRandomValue<Double>(5, 35)
            this[DishModel.carbohydrates] = getRandomValue<Double>(1, 10)
            this[DishModel.cellulose] = getRandomValue<Double>(2, 20)
            this[DishModel.weight] = getRandomValue<Double>(100, 300)
            this[DishModel.timeToCook] = getRandomValue<Int>(10, 120)
            this[DishModel.diet] = getRandomValue<Int>(1, 5)
            this[DishModel.type] = getRandomValue<Int>(1, 5)
        }.map {
            DishDao.wrapRow(it)
        }
    }
}