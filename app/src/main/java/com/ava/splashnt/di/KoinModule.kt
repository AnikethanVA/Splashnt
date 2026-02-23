package com.ava.splashnt.di

import com.ava.splashnt.data.remote.UnsplashApiClient
import com.ava.splashnt.data.repository.UnsplashWallpaperRepository
import com.ava.splashnt.data.repository.WallpaperRepositoryProvider
import org.koin.dsl.module

val networkModule = module {
    single {
        UnsplashApiClient()
    }
}

val repositoryModule = module {
    single { WallpaperRepositoryProvider(get()) }
    single { UnsplashWallpaperRepository(get()) }
}