package com.ava.splashnt.di

import com.ava.splashnt.BuildConfig
import com.ava.splashnt.data.remote.PexelsApiClient
import com.ava.splashnt.data.remote.UnsplashApiClient
import com.ava.splashnt.data.repository.PexelsWallpaperRepository
import com.ava.splashnt.data.repository.UnsplashWallpaperRepository
import com.ava.splashnt.data.repository.WallpaperRepositoryProvider
import com.ava.splashnt.ui.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val networkModule = module {
    single { UnsplashApiClient() }
    single { PexelsApiClient(BuildConfig.PEXELS_API_KEY) }
}

val repositoryModule = module {
    single { WallpaperRepositoryProvider(unsplashRepository = get(), pexelsWallpaperRepository = get()) }
    single { UnsplashWallpaperRepository(get()) }
    single { PexelsWallpaperRepository(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
}