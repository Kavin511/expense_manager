package com.devstudio.expensemanager

import android.app.Application
import com.devstudio.database.AppContext
import com.devstudio.database.ApplicationContainer
import com.devstudio.database.ApplicationModule
import com.devstudio.database.Factory
import com.devstudio.expensemanager.di.commonModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class ExpenseManagerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApplicationModule.initialize(
            ApplicationContainer(
                appContext = AppContext.apply { set(applicationContext) },
                factory = Factory(this),
            ),
        )
        startKoin {
            androidContext(this@ExpenseManagerApplication)
            modules(commonModules)
        }
    }
}
