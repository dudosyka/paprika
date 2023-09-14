package com.paprika.utils.database

import com.paprika.conf.AppConf
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnector(vararg tables: Table, initializer: Transaction.() -> Unit) {
    init {
        TransactionManager.defaultDatabase = Database.connect(AppConf.databaseConfiguration.connectionUrl, driver = AppConf.databaseConfiguration.driver,
            user = AppConf.databaseConfiguration.user, password = AppConf.databaseConfiguration.password)

        transaction {
            tables.forEach {
                SchemaUtils.create(it)
            }
            initializer()
        }
    }
}