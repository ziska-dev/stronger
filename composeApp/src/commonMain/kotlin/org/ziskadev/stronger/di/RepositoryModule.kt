package org.ziskadev.stronger.di

import org.koin.dsl.module
import org.ziskadev.stronger.data.local.ExerciseLocalDataSource
import org.ziskadev.stronger.data.local.WorkoutPlanLocalDataSource
import org.ziskadev.stronger.data.repository.ExerciseRepositoryImpl
import org.ziskadev.stronger.data.repository.WorkoutPlanRepositoryImpl
import org.ziskadev.stronger.domain.repository.ExerciseRepository
import org.ziskadev.stronger.domain.repository.WorkoutPlanRepository

val repositoryModule = module {
    single { ExerciseLocalDataSource(get()) }
    single<ExerciseRepository> { ExerciseRepositoryImpl(get(), get()) }

    single { WorkoutPlanLocalDataSource(get()) }
    single<WorkoutPlanRepository> { WorkoutPlanRepositoryImpl(get()) }
}