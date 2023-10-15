package com.paprika.utils.telegram.dto.sendmessage

import com.paprika.utils.telegram.TelegramTypes
import com.paprika.utils.telegram.dto.ApiCall
import kotlinx.serialization.Serializable

class SendMessage(body: Body): ApiCall("sendMessage", body) {

    @Serializable
    data class Body(
        val chat_id: Long,
        val text: String,
        val reply_markup: TelegramTypes.InlineKeyboardMarkup = TelegramTypes.InlineKeyboardMarkup()
    )
}