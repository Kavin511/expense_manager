package com.devstudio.expensemanager

import android.app.Application
import com.devstudio.sharedmodule.AppContext
import com.devstudio.sharedmodule.SharedModule
import com.devstudio.sharedmodule.SharedModuleConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExpenseManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedModule.initialize(
            SharedModuleConfig(
                appContext = AppContext.apply { set(applicationContext) },
            )
        )
    }
}
