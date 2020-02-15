package com.appback.appbacksdk.toggles

import com.appback.appbacksdk.database.ToggleDao
import com.appback.appbacksdk.network.AppbackApi
import com.appback.appbacksdk.poko.toggle.Toggle

/**
 * Class that will hold all the logic for the toggles flow on the library. Everything related
 * to [Toggle] and it's logic should go inside this class.
 * @param api [AppbackApi] containing the necessary methods to perform api calls
 * @param toggleDao [ToggleDao] containing the necessary methods to perform database
 * transactions
 * @param router String containing the router configured for this helper. This router can be
 * changed on runtime.
 * @author - sapardo10
 */
internal class TogglesHelper(
    private val api: AppbackApi,
    private val toggleDao: ToggleDao,
    var router: String
) {

    /**
     * Method that will load the toggles for the router defined when this class was created
     */
    suspend fun loadToggles() {
        val togglesResponse = api.loadToggles(router)
        val toggles = togglesResponse.toggles
        for (toggle: Toggle in toggles) {
            toggle.key += "-$router"
        }
        toggleDao.insertAll(toggles)
    }

    /**
     * Method that will get all the toggles available for the specified router
     * @param localRouter String containing the router that wants to be checked, can be passed a
     * null parameter to indicate that the router to use is the one defined on the class
     * @return List of [Toggle] containing all the toggles for the router, empty list if none are
     * found
     */
    suspend fun getToggles(localRouter: String? = null): List<Toggle> {
        val databaseRouter = if (localRouter.isNullOrBlank()) {
            "-$router"
        } else {
            "-$localRouter"
        }
        var toggles = toggleDao.getAllByRouter(databaseRouter)
        if (toggles.isEmpty()) {
            val togglesResponse = api.loadToggles(router)
            val togglesList = togglesResponse.toggles
            for (toggle: Toggle in togglesList) {
                toggle.key += "-$router"
            }
            toggleDao.insertAll(togglesList)
            toggles = togglesList
        }

        return toggles

    }

    /**
     * Method that will get the toggle for the specified router with that key
     * @param key String containing the key of the toggle
     * @return [Toggle] that has the key and router defined, null if not found on database
     */
    suspend fun getToggle(key: String): Toggle? {
        return try {
            toggleDao.findToggleAsync("$key-$router")
        } catch (e: Exception) {
            null
        }
    }
}

