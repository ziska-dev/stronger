package org.ziskadev.stronger.data.local

import app.cash.sqldelight.db.SqlDriver

fun createDatabase(driver: SqlDriver): StrongerDatabase =
    StrongerDatabase(driver)