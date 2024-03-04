package com.triplerock.tictactoe

import android.app.Application
import com.triplerock.tictactoe.koin.koinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TicApp)
            modules(koinModule)
        }
    }
}