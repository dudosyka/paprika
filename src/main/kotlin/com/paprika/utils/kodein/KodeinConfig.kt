package com.paprika.utils.kodein

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.kodein.di.*
import org.kodein.type.jvmType

/**
 * Shortcut for binding eager singletons to the same type.
 */
inline fun <reified T : Any> DI.MainBuilder.bindEagerSingleton(crossinline callback: (DI) -> T) {
    bind<T>() with eagerSingleton { callback(this@eagerSingleton.di) }
}

/**
 * Shortcut for binding singletons to the same type.
 */
inline fun <reified T : Any> DI.MainBuilder.bindSingleton(crossinline callback: (DI) -> T) {
    bind<T>() with singleton { callback(this@singleton.di) }
}

@Suppress("KDocUnresolvedReference")
fun Application.kodeinApplication(
    kodeinMapper: DI.MainBuilder.(Application) -> Unit = {}
) {
    val application = this

    /**
     * Creates a [Kodein] instance, binding the [Application] instance.
     * Also calls the [kodeInMapper] to map the Controller dependencies.
     */
    val kodein = DI {
        bind<Application>() with instance(application)
        kodeinMapper(this, application)
    }

    /**
     * Detects all the registered [KodeinController] and registers its routes.
     */
    routing {
        for (bind in kodein.container.tree.bindings) {
            val bindClass = bind.key.type.jvmType as? Class<*>?
            if (bindClass != null && KodeinController::class.java.isAssignableFrom(bindClass)) {
                val res by kodein.Instance(bind.key.type)
                println("Registering '$res' routes...")
                (res as KodeinController).apply { registerRoutes() }
            }
        }
    }
}