package com.paprika.utils.telegram

import kotlinx.serialization.Serializable

inline fun <reified T> Map<*, *>.getT(key: String): T {
    return get(key) as T
}

inline fun <reified T> Map<*, *>.getTOrNull(key: String): T? {
    return get(key) as? T
}

inline fun <reified T: Enum<T>> Map<*, *>.getEnum(key: String): T? {
    val stringify: String = getT(key)
    return enumValues<T>().find {
        it.name == stringify
    }
}

abstract class TelegramTypes private constructor() {

    class EmptyType(): TelegramTypes()

    enum class ChatType {
        private, group, supergroup, channel
    }

    @Serializable
    data class User(
        val id: Long,
        val isBot: Boolean,
        val firstName: String,
        val username: String?,
        val languageCode: String?,
        val isPremium: Boolean?,
    ): TelegramTypes() {
        constructor(map: Map<*, *>) : this(
            id = map.getT("id"),
            isBot = map.getTOrNull("is_bot")!!,
            firstName = map.getT<String>("first_name"),
            username = map.getTOrNull("username"),
            languageCode = map.getTOrNull("language_code"),
            isPremium = map.getTOrNull("is_premium")
        )
    }

    @Serializable
    data class Chat(
        val id: Long,
        val firstName: String,
        val username: String?,
        val type: ChatType,
    ): TelegramTypes() {
        constructor(map: Map<*, *>) : this(
            id = map.getT("id"),
            firstName = map.getT("first_name"),
            username = map.getTOrNull("username"),
            type = map.getEnum<ChatType>("type")!!
        )
    }

    @Serializable
    data class Message(
        val messageId: Long,
        val messageThreadId: Long?,
        val from: User?,
        val senderChat: Chat?,
        val date: Long,
        val chat: Chat,
        val text: String
    ): TelegramTypes() {
        constructor(map: Map<*, *>): this(
            messageId = map.getT("message_id"),
            messageThreadId = map.getTOrNull("message_thread_id"),
            from = map.getTOrNull<Map<String, Any?>>("from").run { if (this != null) User(this@run) else null },
            senderChat = map.getTOrNull<Map<String, Any?>>("sender_chat").run { if (this != null) Chat(this) else null },
            date = map.getT("date"),
            chat = Chat(map.getT<Map<String, Any?>>("chat")),
            text = map.getT("text")
        ) {}
    }

    @Serializable
    data class CallbackQuery(
        val id: String,
        val from: User,
        val message: Message,
        val data: String?,
        val chatInstance: String,
    ): TelegramTypes() {
        constructor(map: Map<*, *>): this(
            id = map.getT("id"),
            from = User(map.getT("from")),
            message = Message(map.getT("message")),
            data = map.getT("data"),
            chatInstance = map.getT("chat_instance")
        )
    }

    @Serializable
    data class WebappInfo(
        val url: String
    ): TelegramTypes()

    @Serializable
    data class InlineKeyboardButton(
        val text: String,
        val url: String? = null,
        val callback_data: String? = null,
        val web_app: WebappInfo? = null,
    ): TelegramTypes()

    @Serializable
    data class InlineKeyboardMarkup(
        val inline_keyboard: List<List<InlineKeyboardButton>> = listOf()
    ): TelegramTypes()
}