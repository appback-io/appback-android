package com.appback.appbacksdk

internal object AppbackConstants {
    const val BASE_URL = "https://appback.io/api/"
    const val DATABASE_NAME = "appback-database"

    //ERRORS
    const val ROUTE_NOT_DEFINED =
        "The route that you are trying to access has not been defined yet, make sure you called the call the configure method before trying to do this operation"
}

/**
 * Class containing the different log levels that are allowed on Appback
 * @author - sapardo10
 * @since 0.0.1
 */
enum class AppbackLogLevel(val level: Int) {
    LOG_LEVEL_LOW(1),
    LOG_LEVEL_MEDIUM(2),
    LOG_LEVEL_HIGH(3),
    LOG_LEVEL_CRITIC(4),
    LOG_LEVEL_ALERT(5)
}