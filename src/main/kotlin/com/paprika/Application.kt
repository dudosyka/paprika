package com.paprika

import com.paprika.controllers.PaprikaController
import com.paprika.controllers.UploadController
import com.paprika.plugins.*
import com.paprika.services.*
import com.paprika.services.telegram.TelegramResponseDispatcher
import com.paprika.services.telegram.updates.TelegramUpdatesListener
import com.paprika.utils.kodein.bindEagerSingleton
import com.paprika.utils.kodein.bindSingleton
import com.paprika.utils.kodein.kodeinApplication
import io.ktor.server.application.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun main(): Unit = EngineMain.main(arrayOf())

fun Application.module() {
//    configureMonitoring()
    responseFilter()
    configureSerialization()
    configureRouting()
    kodeinApplication {
        //Services DI
        bindSingleton { DishService(it) }
        bindSingleton { PaprikaService(it) }
        bindSingleton { DataManagerService(it) }
        bindSingleton { CacheService(it) }
        bindSingleton { BotService(it) }
        bindSingleton { TelegramResponseDispatcher(it) }
        bindEagerSingleton {
            val updatesListener = TelegramUpdatesListener(it)

            CoroutineScope(Job()).launch {
                configureTelegram(updatesListener)
            }

            updatesListener
        }

        //Controllers DI
        bindSingleton { PaprikaController(it) }
        bindSingleton { UploadController(it) }
    }
//    DatabaseConnector(
//        DietModel, DishTypeModel, DishModel, DishIngredientModel,
//        IngredientModel,
//        EatingCacheModel, EatingCacheDishesModel
//    ) {
////        DishGenerator(52000, false, 2001)
//    }
}
