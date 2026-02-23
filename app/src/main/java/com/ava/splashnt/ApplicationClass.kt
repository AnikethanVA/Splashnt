package com.ava.splashnt

import android.app.Application
import com.ava.splashnt.di.networkModule
import com.ava.splashnt.di.repositoryModule
import com.ava.splashnt.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ApplicationClass: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ApplicationClass)
            modules(networkModule, repositoryModule, viewModelModule)
        }
    }
}