package org.ziskadev.stronger.di

import org.koin.dsl.module
import org.ziskadev.stronger.data.local.createDatabase
import org.ziskadev.stronger.data.local.createSqlDriver

val databaseModule = module {
    single { createSqlDriver() }
    single { createDatabase(get()) }
}