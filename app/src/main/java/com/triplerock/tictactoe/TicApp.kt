package com.triplerock.tictactoe

import android.app.Application
import android.content.Context
import com.triplerock.tictactoe.koin.koinModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TicApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        startKoin {
            androidContext(this@TicApp)
            modules(koinModule)
        }
    }

    companion object {
        var context: Context? = null
    }
}