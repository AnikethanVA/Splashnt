package com.ava.splashnt.di

import com.ava.splashnt.data.remote.UnsplashApiClient
import org.koin.dsl.module

val networkModule = module {
    single {
        UnsplashApiClient()
    }
}