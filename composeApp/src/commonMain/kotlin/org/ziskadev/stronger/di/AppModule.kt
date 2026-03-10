package org.ziskadev.stronger.di

import org.koin.dsl.module

// Alle Module zusammengefasst – wird in der Application-Klasse übergeben
val appModules = listOf(
    databaseModule,
    networkModule,
    repositoryModule,
    viewModelModule,
)