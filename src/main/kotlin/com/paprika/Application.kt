package com.paprika

import com.paprika.controllers.PaprikaMainController
import com.paprika.database.models.cache.DailyDietCacheModel
import com.paprika.database.models.cache.DailyDietEatingsCacheModel
import com.paprika.database.models.cache.EatingCacheDishesModel
import com.paprika.database.models.cache.EatingCacheModel
import com.paprika.database.models.dish.DietModel
import com.paprika.database.models.dish.DishModel
import com.paprika.database.models.dish.DishTypeModel
import com.paprika.database.models.ingredient.IngredientMeasureModel
import com.paprika.database.models.ingredient.IngredientModel
import com.paprika.plugins.*
import com.paprika.services.DishService
import com.paprika.services.PaprikaService
import com.paprika.utils.database.DatabaseConnector
import com.paprika.utils.kodein.bindSingleton
import com.paprika.utils.kodein.kodeinApplication
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(): Unit = EngineMain.main(arrayOf())
//{
//    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
//        .start(wait = true)
//}

fun Application.module() {
//    configureMonitoring()
    configureSerialization()
    configureAdministration()
    configureRouting()
    kodeinApplication {
        bindSingleton { DishService(it) }
        bindSingleton { PaprikaService(it) }
        bindSingleton { PaprikaMainController(it) }
    }
    DatabaseConnector(
        DietModel, DishTypeModel, DishModel,
        IngredientModel, IngredientMeasureModel,
        EatingCacheModel, EatingCacheDishesModel,
        DailyDietCacheModel, DailyDietEatingsCacheModel
    ) {
//        DishGenerator(2000)
    }
}
