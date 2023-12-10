package com.paprika.services.telegram

import com.paprika.utils.kodein.KodeinService
import com.paprika.utils.telegram.TelegramApiDispatcher
import com.paprika.utils.telegram.dto.ApiResult
import com.paprika.utils.telegram.dto.sendmessage.SendMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import org.kodein.di.DI

class TelegramResponseDispatcher(di: DI) : KodeinService(di) {
    private val telegramApiDispatcher = TelegramApiDispatcher()

    //Channel for dispatching new messages from US to the CLIENT
    @OptIn(ObsoleteCoroutinesApi::class)
    val onDispatch = CoroutineScope(Job()).actor<TelegramOnDispatch>(capacity = Channel.BUFFERED) {
        for (onDispatch in this) {
            when (onDispatch) {
                is TelegramOnDispatch.Message -> {
                    telegramApiDispatcher.call<ApiResult>(
                        SendMessage(
                            SendMessage.Body(
                                chat_id = onDispatch.chat.id,
                                text = onDispatch.text,
                                reply_markup = onDispatch.replyMarkup
                            )
                        )
                    )
                }
            }
        }
    }
}