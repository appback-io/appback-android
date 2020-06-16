package com.appback.appbacksdk

internal object AppbackConstants {

    //GENERAL
    const val AUTH_BASE_URL = "https://api-auth.appback.io/api/"
    const val DATABASE_NAME = "appback-database"
    const val EMPTY_STRING = ""

    //EVENT LOGS
    const val EVENT_APP_VERSION = "_app_version"
    const val EVENT_BATTERY_LEVEL = "_battery_level"
    const val EVENT_CARRIER_NAME = "_carrier"
    const val EVENT_CONNECTION_TYPE = "_connection_type"
    const val EVENT_DEVICE_ID = "_device_ID"
    const val EVENT_DEVICE_NAME = "_device"
    const val EVENT_DEVICE_ORIENTATION = "_orientation"
    const val EVENT_DEVICE_STORAGE = "_storage"
    const val EVENT_SYSTEM_VERSION = "_system_version"

    //ERRORS
    const val ROUTE_NOT_DEFINED =
        "The route that you are trying to access has not been defined yet, make sure you called the call the configure method before trying to do this operation"
}