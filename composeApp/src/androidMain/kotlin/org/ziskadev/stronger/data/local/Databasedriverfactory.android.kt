package org.ziskadev.stronger.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

actual fun createSqlDriver(): SqlDriver {
    val context = GlobalContext.get().get<android.content.Context>()
    return AndroidSqliteDriver(
        schema = StrongerDatabase.Schema,
        context = context,
        name = "stronger.db"
    )
}