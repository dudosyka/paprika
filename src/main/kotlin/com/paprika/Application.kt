package com.paprika

import com.paprika.controllers.PaprikaMainController
import com.paprika.database.models.cache.EatingCacheDishesModel
import com.paprika.database.models.cache.EatingCacheModel
import com.paprika.database.models.dish.DietModel
import com.paprika.database.models.dish.DishIngredientModel
import com.paprika.database.models.dish.DishModel
import com.paprika.database.models.dish.DishTypeModel
import com.paprika.database.models.ingredient.IngredientModel
import com.paprika.plugins.*
import com.paprika.services.CacheService
import com.paprika.services.DishService
import com.paprika.services.PaprikaService
import com.paprika.utils.database.DatabaseConnector
import com.paprika.utils.generator.DishGenerator
import com.paprika.utils.kodein.bindSingleton
import com.paprika.utils.kodein.kodeinApplication
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(): Unit = EngineMain.main(arrayOf())

fun Application.module() {
//    configureMonitoring()
    responseFilter()
    configureSerialization()
    configureAdministration()
    configureRouting()
    kodeinApplication {
        bindSingleton { DishService(it) }
        bindSingleton { PaprikaService(it) }
        bindSingleton { CacheService(it) }
        bindSingleton { PaprikaMainController(it) }
    }
    DatabaseConnector(
        DietModel, DishTypeModel, DishModel, DishIngredientModel,
        IngredientModel,
        EatingCacheModel, EatingCacheDishesModel
    ) {
//        DishGenerator(52000, false, 2001)
    }
}
