package com.paprika.utils.database

import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.toEntity
import java.time.LocalDateTime

abstract class BaseIntEntityClass<E : BaseIntEntity>(table: BaseIntIdTable) : IntEntityClass<E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = LocalDateTime.now()
                } catch (_: Exception) { }
            }
        }
    }
}