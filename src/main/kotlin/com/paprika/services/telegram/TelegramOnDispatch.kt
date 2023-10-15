package com.paprika.services.telegram

import com.paprika.utils.telegram.TelegramTypes

open class TelegramOnDispatch private constructor() {
    class Message(val chat: TelegramTypes.Chat, val text: String, val replyMarkup: TelegramTypes.InlineKeyboardMarkup = TelegramTypes.InlineKeyboardMarkup()): TelegramOnDispatch()
}