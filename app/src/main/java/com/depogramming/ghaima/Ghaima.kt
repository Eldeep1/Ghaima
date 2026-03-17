package com.depogramming.ghaima

import android.app.Application
import com.depogramming.ghaima.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Ghaima: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@Ghaima)
            modules(dataModule)
        }
    }
}