package com.paprika.plugins

import com.paprika.services.telegram.updates.TelegramUpdatesListener

/*

    Here we do initialization of the Telegram listener and start pulling for new messages

 */

suspend fun configureTelegram(telegramUpdatesListener: TelegramUpdatesListener) {
    telegramUpdatesListener.startPulling()
}