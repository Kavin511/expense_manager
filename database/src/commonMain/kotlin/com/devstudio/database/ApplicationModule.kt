package com.devstudio.database

/**
 * @Author: Kavin
 * @Date: 12/07/24
 */

expect object AppContext

expect class Factory {
    fun getRoomInstance(): ExpenseManagerDataBase
}


class ApplicationContainer(
    val appContext: AppContext,
    val factory: Factory
)

object ApplicationModule {
    lateinit var config: ApplicationContainer
    fun initialize(config: ApplicationContainer) {
        this.config = config
    }
}