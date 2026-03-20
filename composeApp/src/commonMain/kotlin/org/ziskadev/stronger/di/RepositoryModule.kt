package org.ziskadev.stronger.di

import org.koin.dsl.module
import org.ziskadev.stronger.data.local.ExerciseLocalDataSource
import org.ziskadev.stronger.data.local.UserProfileLocalDataSource
import org.ziskadev.stronger.data.local.WorkoutPlanLocalDataSource
import org.ziskadev.stronger.data.local.WorkoutSessionLocalDataSource
import org.ziskadev.stronger.data.repository.ExerciseRepositoryImpl
import org.ziskadev.stronger.data.repository.UserProfileRepositoryImpl
import org.ziskadev.stronger.data.repository.WorkoutPlanRepositoryImpl
import org.ziskadev.stronger.data.repository.WorkoutSessionRepositoryImpl
import org.ziskadev.stronger.domain.repository.ExerciseRepository
import org.ziskadev.stronger.domain.repository.UserProfileRepository
import org.ziskadev.stronger.domain.repository.WorkoutPlanRepository
import org.ziskadev.stronger.domain.repository.WorkoutSessionRepository

val repositoryModule = module {
    single { ExerciseLocalDataSource(get()) }
    single<ExerciseRepository> { ExerciseRepositoryImpl(get(), get()) }

    single { WorkoutPlanLocalDataSource(get()) }
    single<WorkoutPlanRepository> { WorkoutPlanRepositoryImpl(get()) }

    single { WorkoutSessionLocalDataSource(get()) }
    single<WorkoutSessionRepository> { WorkoutSessionRepositoryImpl(get()) }

    single { UserProfileLocalDataSource(get()) }
    single<UserProfileRepository> { UserProfileRepositoryImpl(get()) }
}