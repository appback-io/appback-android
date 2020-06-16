package com.appback.appbacksdk.logs

import android.util.Log
import com.appback.appbacksdk.database.LogEventDao
import com.appback.appbacksdk.network.AppbackApi
import com.appback.appbacksdk.poko.log.EventLogRequest
import com.appback.appbacksdk.poko.log.LogEvent

/**
 * Class that will hold all the logic for the logs flow on the library. Everything related
 * to [LogEvent] and it's logic should go inside this class.
 * @param api [AppbackApi] containing the necessary methods to perform api calls
 * @param logEventDao [LogEventDao] containing the necessary methods to perform database
 * transactions
 * @param router String containing the router configured for this helper. This router can be
 * changed on runtime.
 * @author - sapardo10
 */
internal class LogsHelper(
    private val api: AppbackApi,
    private val logEventDao: LogEventDao,
    private var router: String
) {

    /**
     * Method that sends a log to Appback, if it's not possible to send it (the service doesn't
     * return a successful response for some reason) it will store it on the database to send it
     * at a later point when the operation can succeed
     * @param name String containing the name of the event to be sent
     * @param description String containing the description of the log
     * @param level [AppbackLogLevel] stating what kind of log is the one being sent
     */
    suspend fun sendLog(
        router: String,
        name: String,
        time: Long,
        parameters: ArrayList<HashMap<String, Any>>
    ) {
        try {
            val event = EventLogRequest(time = time, name = name, router = router, parameters = parameters )
            val result = api.logEvent(
                parameters = event
            )
            result.code != 200
        } catch (e: Exception) {
            Log.e("error", e.toString())
            false
        }
    }
}
