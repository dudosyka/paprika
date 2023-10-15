package com.paprika.services

import com.paprika.conf.AppConf
import com.paprika.services.telegram.TelegramOnDispatch
import com.paprika.services.telegram.TelegramResponseDispatcher
import com.paprika.utils.telegram.TelegramTypes
import com.paprika.utils.kodein.KodeinService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import org.kodein.di.DI
import org.kodein.di.instance

class BotService(di: DI): KodeinService(di) {
    private val telegramResponseDispatcher: TelegramResponseDispatcher by instance()

    open class BotEvents(
        open val chat: TelegramTypes.Chat?
    ) {
//        class EmptyType(chat: TelegramTypes.Chat? = null): BotEvents(chat)
        class Message(
            override val chat: TelegramTypes.Chat,
            val text: String
        ): BotEvents(chat)

        class Callback(
            override val chat: TelegramTypes.Chat,
            val data: String
        ): BotEvents(chat)
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    val updatesActor = CoroutineScope(Job()).actor<BotEvents>(capacity = Channel.BUFFERED) {
        for (update in this) {
            when (update) {
                is BotEvents.Message -> telegramResponseDispatcher.onDispatch.send(
                    TelegramOnDispatch.Message(
                        chat = update.chat,
                        text = "Hi! Click the btn below to open the paprika application",
                        replyMarkup = TelegramTypes.InlineKeyboardMarkup(
                            inline_keyboard = listOf(listOf(
//                                TelegramTypes.InlineKeyboardButton(
//                                    text = "New btn link",
//                                    url = "https://google.com"
//                                ),
//                                TelegramTypes.InlineKeyboardButton(
//                                    text = "New btn callback",
//                                    callback_data = "Data from callback"
//                                ),
                                TelegramTypes.InlineKeyboardButton(
                                    text = "Open web app",
                                    web_app = TelegramTypes.WebappInfo(
                                        url = AppConf.botWebappUrl
                                    )
                                )
                            ))
                        )
                    )
                )
                is BotEvents.Callback -> telegramResponseDispatcher.onDispatch.send(
                    TelegramOnDispatch.Message(
                        chat = update.chat,
                        text = update.data,
                    )
                )
            }
        }
    }
}