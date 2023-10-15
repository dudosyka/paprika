package com.paprika.plugins

import com.paprika.services.telegram.updates.TelegramUpdatesListener

suspend fun configureTelegram(telegramUpdatesListener: TelegramUpdatesListener) {
    telegramUpdatesListener.startPulling()
}