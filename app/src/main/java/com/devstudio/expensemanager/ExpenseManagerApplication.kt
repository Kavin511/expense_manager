package com.devstudio.expensemanager

import android.app.Application
import com.devstudio.database.AppContext
import com.devstudio.database.ApplicationModule
import com.devstudio.database.ApplicationContainer
import com.devstudio.database.Factory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExpenseManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationModule.initialize(
            ApplicationContainer(
                appContext = AppContext.apply { set(applicationContext) },
                factory = Factory(this)
            )
        )
    }
}
