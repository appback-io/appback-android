package com.appback.appbacksdk.network

import com.appback.appbacksdk.poko.AccessToken
import com.appback.appbacksdk.network.dtos.BaseLogResponse
import com.appback.appbacksdk.network.dtos.BaseToggleResponse
import com.appback.appbacksdk.network.dtos.BaseTranslationResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interface holding all the services to be call from the backend with Retrofit
 * @author - sapardo10
 * @since 0.0.1
 */
internal interface AppbackApi {

    /**
     * Method to get all translations from a router
     * @param router String containing the router to the translations
     * @return [BaseTranslationResponse] holding the response from the server
     */
    @GET("v1/translations")
    suspend fun loadTranslations(@Query("router") router: String): BaseTranslationResponse

    /**
     * Method to get all toggles from a router
     * @param router String containing the router to the toggles
     * @return [BaseToggleResponse] holding the response from the server
     */
    @GET("v1/toggles")
    suspend fun loadToggles(@Query("router") router: String): BaseToggleResponse

    /**
     * Method that will log the event onto the Appback core
     * @param router String containing the router where to send the log
     * @param name String containing the name of the log
     * @param description String containing the description of the log
     * @param level Int stating the level of the log
     * @return [BaseLogResponse] holding the response from the server
     */
    @POST("v1/eventLog")
    suspend fun logEvent(
        @Query("router") router: String,
        @Query("name") name: String,
        @Query("description") description: String,
        @Query("level") level: Int
    ): BaseLogResponse

    /**
     * Method that will get the token from the service given an authorized api key
     * @param apiKey String containing the api key to generate the token
     * @return [AccessToken] holding the token to communicate with the back
     */
    @GET("token")
    suspend fun getToken(@Query("key") apiKey: String): AccessToken

}