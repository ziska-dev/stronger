package org.ziskadev.stronger.di

import org.koin.dsl.module
import org.ziskadev.stronger.data.local.AppSettingsLocalDataSource
import org.ziskadev.stronger.data.repository.AppSettingsRepositoryImpl
import org.ziskadev.stronger.domain.repository.AppSettingsRepository

val settingsModule = module {
    single { AppSettingsLocalDataSource(get()) }
    single<AppSettingsRepository> { AppSettingsRepositoryImpl(get()) }
}