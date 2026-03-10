package org.ziskadev.stronger.data.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual fun createSqlDriver(): SqlDriver =
    NativeSqliteDriver(
        schema = StrongerDatabase.Schema,
        name = "stronger.db"
    )