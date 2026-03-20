package org.ziskadev.stronger.di

import org.koin.dsl.module
import org.ziskadev.stronger.data.local.ExerciseLocalDataSource
import org.ziskadev.stronger.data.repository.ExerciseRepositoryImpl
import org.ziskadev.stronger.domain.repository.ExerciseRepository

val repositoryModule = module {
    single { ExerciseLocalDataSource(get()) }
    single<ExerciseRepository> { ExerciseRepositoryImpl(get(), get()) }
}