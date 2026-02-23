package com.ava.splashnt.di

import com.ava.splashnt.data.remote.UnsplashApiClient
import com.ava.splashnt.data.repository.UnsplashWallpaperRepository
import com.ava.splashnt.data.repository.WallpaperRepositoryProvider
import com.ava.splashnt.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
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

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
}