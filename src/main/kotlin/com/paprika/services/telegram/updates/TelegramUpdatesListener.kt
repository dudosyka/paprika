package com.paprika.services.telegram.updates

import com.paprika.services.BotService
import com.paprika.utils.telegram.TelegramTypes
import com.paprika.utils.kodein.KodeinService
import com.paprika.utils.telegram.TelegramApiDispatcher
import com.paprika.utils.telegram.dto.getupdates.GetUpdates
import org.kodein.di.DI
import org.kodein.di.instance

class TelegramUpdatesListener(di: DI): KodeinService(di) {
    private val botService: BotService by instance()
    private val telegramApiDispatcher = TelegramApiDispatcher()
    private var lastUpdateId: Long = 0
    private val allowedUpdates = listOf<String>("message", "callback_query", "inline_query")
    init {
        println("Initialized")
    }

    private fun processUpdates(updates: TelegramUpdatesListDto): List<TelegramTypes> {
        return updates.result.map {
            when {
                it.containsKey("message") -> {
                    TelegramTypes.Message(it["message"] as Map<*, *>)
                }
                it.containsKey("callback_query") -> {
                    TelegramTypes.CallbackQuery(it["callback_query"] as Map<*, *>)
                }
                else -> {
                    println(it)
                    TelegramTypes.EmptyType()
                }
            }
        }
    }

    suspend fun startPulling() {
        while (true) {
            val updates = telegramApiDispatcher.call<TelegramUpdatesListDto>(GetUpdates(GetUpdates.Body(offset = lastUpdateId, allowed_updates = allowedUpdates)))
            if (updates.ok && updates.result.isNotEmpty()) {
                updates.getLastUpdateId().run {
                    if (this != null) {
                        lastUpdateId = this + 1
                    }
                }
                processUpdates(updates).forEach {
                    when (it) {
                        is TelegramTypes.Message ->
                            botService.updatesActor.trySend(BotService.BotEvents.Message(it.chat, it.text))
                        is TelegramTypes.CallbackQuery ->
                            botService.updatesActor.trySend(BotService.BotEvents.Callback(it.message.chat, it.data ?: ""))
                    }
                }
            }
        }
    }
}