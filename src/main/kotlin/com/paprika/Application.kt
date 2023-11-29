package com.paprika

import com.paprika.controllers.*
import com.paprika.database.models.cache.EatingCacheDishesModel
import com.paprika.database.models.cache.EatingCacheModel
import com.paprika.database.models.cusine.CusineModel
import com.paprika.database.models.dish.*
import com.paprika.database.models.ingredient.IngredientMeasureModel
import com.paprika.database.models.ingredient.IngredientModel
import com.paprika.database.models.ingredient.MeasureModel
import com.paprika.database.models.user.UserEatingsParamsModel
import com.paprika.database.models.user.UserModel
import com.paprika.database.models.user.UserParamsModel
import com.paprika.database.models.user.UserSavedDietModel
import com.paprika.plugins.*
import com.paprika.services.*
import com.paprika.services.telegram.TelegramResponseDispatcher
import com.paprika.services.telegram.updates.TelegramUpdatesListener
import com.paprika.utils.database.DatabaseConnector
import com.paprika.utils.kodein.bindEagerSingleton
import com.paprika.utils.kodein.bindSingleton
import com.paprika.utils.kodein.kodeinApplication
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun main(): Unit = EngineMain.main(arrayOf())

fun Application.module() {
    install(CORS) {
        anyHost()
        allowHeader("Authorization")
        allowNonSimpleContentTypes = true
        allowCredentials = true
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Put)
    }
//    configureMonitoring()
    responseFilter()
    configureSerialization()
    configureRouting()
    configureSecurity()
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
        bindSingleton { IngredientService(it) }
        bindSingleton { UserService(it) }
        bindSingleton { AuthService(it) }

        //Controllers DI
        bindSingleton { PaprikaController(it) }
        bindSingleton { UploadController(it) }
        bindSingleton { IngredientsController(it) }
        bindSingleton { UserController(it) }
        bindSingleton { DishController(it) }
    }
    DatabaseConnector(
        DietModel, DishTypeModel, DishModel, DishIngredientModel, CusineModel, DishStepModel,
        IngredientModel, MeasureModel, IngredientMeasureModel,
        EatingCacheModel, EatingCacheDishesModel,
        UserModel, UserParamsModel, UserEatingsParamsModel, UserSavedDietModel
    ) {
//        DishGenerator(52000, false, 1)
    }
}
