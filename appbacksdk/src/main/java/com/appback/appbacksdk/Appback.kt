package com.appback.appbacksdk

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.appback.appbacksdk.AppbackConstants.AUTH_BASE_URL
import com.appback.appbacksdk.AppbackConstants.DATABASE_NAME
import com.appback.appbacksdk.callbacks.*
import com.appback.appbacksdk.database.AppbackDatabase
import com.appback.appbacksdk.device.DeviceInformationUtils
import com.appback.appbacksdk.exceptions.RouterNotDefinedException
import com.appback.appbacksdk.logs.LogsHelper
import com.appback.appbacksdk.network.AppbackApi
import com.appback.appbacksdk.network.AuthenticationInterceptor
import com.appback.appbacksdk.poko.AccessToken
import com.appback.appbacksdk.poko.toggle.Toggle
import com.appback.appbacksdk.poko.transalation.LanguageItem
import com.appback.appbacksdk.poko.transalation.Translation
import com.appback.appbacksdk.toggles.TogglesHelper
import com.appback.appbacksdk.translations.TranslationsHelper
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Class containing the public API for the appback android SDK, this class presented as a singleton
 * will provide all methods available for the use of the library.
 * @param context [Context] of the application the library is running.
 * @author - sapardo10
 * @since 0.0.1
 */
open class AppBack private constructor(context: Context) {

    /**
     * ---------------------------------------------------------------------------------------------
     * -------------------------------------STATIC VARIABLES----------------------------------------
     * ---------------------------------------------------------------------------------------------
     */

    companion object : SingletonHolder<AppBack, Context>(::AppBack)

    /**
     * ---------------------------------------------------------------------------------------------
     * ----------------------------------------VARIABLES--------------------------------------------
     * ---------------------------------------------------------------------------------------------
     */

    //Scope for the coroutines to run on
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    //Thread or coroutine where all input/output operations of the library will take place
    private var w1: Deferred<*>? = null

    //Api of Retrofit containing the instance of the retrofit client to make call to the service
    private var api: AppbackApi

    //Database of the library here all elements necessary will be stored locally
    private var database: AppbackDatabase

    //Instance of the [TranslationsHelper] class
    private var translationsHelper: TranslationsHelper? = null

    //Instance of the [TogglesHelper] class
    private var togglesHelper: TogglesHelper? = null

    //Instance of the [LogsHelper] class
    private var logsHelper: LogsHelper? = null

    //Key containing the api key provided by the user to use the library
    private var apiKey = ""

    //Instance of the token provided by the server when the application has been authenticated
    private var token: AccessToken? = null

    private var endpoint: String? = null
    /**
     * ---------------------------------------------------------------------------------------------
     * -------------------------------------INITIALIZATION------------------------------------------
     * ---------------------------------------------------------------------------------------------
     */
    init {

        //Instantiates the Retrofit client
        api = getRetrofitClient(baseURL = AUTH_BASE_URL)
        //Instantiates the database
        database = getAppbackDatabase(context)
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * -------------------------------------PUBLIC METHODS------------------------------------------
     * ---------------------------------------------------------------------------------------------
     */

    /**
     * Method that will configure the instance of the [Appback] class with the necessary companions
     * and authentications
     * @param apiKey String containing the api key that is needed to be connected with the Appback
     * server, if null it means the user does not want to change the key stored
     * @param translationRouter String stating the router that wants to be used for the
     * translations, if null is passed, it means the user is not interested in setting the router
     * at the moment this method was called
     * @param toggleRouter String stating the router that wants to be used for the toggles, if
     * null is passed, it means the user is not interested in setting the router at the moment
     * this method was called
     * @param logRouter String stating the router that wants to be used for the logs it means the
     * user is not interested in setting the router at the moment this method was called
     * @author - sapardo10
     * @since 0.0.1
     */
    fun configure(
        apiKey: String? = null,
        translationRouter: String? = null,
        toggleRouter: String? = null,
        logRouter: String? = null,
        callback: OnAppBackInitialization
    ) {
        w1 = scope.async {
            if (apiKey != null) {
                getAuthenticationToken(apiKey)
            }
            //translationRouter?.let {
            //    initializeTranslationsHelper(it)
            //}
            toggleRouter?.let {
                initializeTogglesHelper(it)
            }
//            logRouter?.let {
//                initializeLogsHelper(it)
//            }
            callback.onInitialization(token != null)
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * ---------------------------------TRANSLATIONS API--------------------------------------------
     * ---------------------------------------------------------------------------------------------
     */

    /**
     * Suspend method that will get all the translations for the given router, if no router is provided the
     * method will try to use a router provided on a time before, being on another method called or
     * on the [configure] method.
     * Be careful to call this method only from a [CoroutineScope] or from another suspending
     * function.
     * @param router String containing the router from where the translations wants to be fetched
     * from, null if a previous router wants to be used
     * @throws [RouterNotDefinedException] if no router has been defined for translations up to
     * this point
     * @return List of [Translation] for the given router
     * @author - sapardo10
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    suspend fun getTranslations(router: String? = null, languageIdentifier: String): List<Translation> {
        return translationsHelper?.getTranslations(router, languageIdentifier) ?: emptyList()
    }

    /**
     * Method that will get all the translations for the given router, if no router is provided the
     * method will try to use a router provided on a time before, being on another method called or
     * on the [configure] method
     * @param callback [OnTranslationsSearched] that will be called when the operation has been finished
     * @param router String containing the router from where the translations wants to be fetched
     * from, null if a previous router wants to be used
     * @throws [RouterNotDefinedException] if no router has been defined for translations up to
     * this point
     * @author - sapardo10
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    fun getTranslations(router: String? = null, languageIdentifier: String, callback: OnTranslationsSearched) {
        router?.let { translationsHelper = TranslationsHelper(api, database.translationDao(), it) }
        scope.launch {
            w1?.await()
            val translations = translationsHelper?.getTranslations(router, languageIdentifier) ?: emptyList()
            callback.onTranslationsFound(translations)
        }
    }

    /**
     * Suspend method that will get a defined translation given a key and a router, if no router is
     * provided the method will try to use a router provided on a time before, being on another
     * method called or on the [configure] method.
     * Be careful to call this method only from a [CoroutineScope] or from another suspending
     * function
     * @param key String containing the key of the translation that wants to be fetched
     * @param router String containing the router where the translation should be searched, if null
     * it will try to use an older router to fetch the [Translation]
     * @return [Translation] with the given key and router, null if it wasn't possible to find it
     * @throws [RouterNotDefinedException] if no router has been defined for translations up to
     * this point
     * @author - sapardo10
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    suspend fun getTranslation(key: String, router: String): Translation? {
        router?.let { translationsHelper = TranslationsHelper(api, database.translationDao(), it) }
        return translationsHelper?.getTranslation(router, key)
    }

    /**
     * Method that will get all languages available
     * @param router String containing the router key of the get all languages that wants to be fetched
     * @param callback that will be called when the operation has been
     * completed successfully or null when was failed.
     */
    fun getLanguages(router: String, callback: (items: List<LanguageItem>?) -> Unit) {
        translationsHelper = TranslationsHelper(api, database.translationDao(), router)
        scope.launch {
            try {
                val languages = translationsHelper?.getLanguages(router)
                callback(languages)
            } catch (e: Exception) {
                callback(null)
            }
        }
    }

    /**
     * Method that will get a defined translation given a key and a router, if no router is
     * provided the method will try to use a router provided on a time before, being on another
     * method called or on the [configure] method.
     * @param key String containing the key of the translation that wants to be fetched
     * @param callback [OnTranslationSearched] that will be called when the operation has been
     * completed successfully.
     * @param router String containing the router where the translation should be searched, if null
     * it will try to use an older router to fetch the [Translation]
     * @throws [RouterNotDefinedException] if no router has been defined for translations up to
     * this point
     * @author - sapardo10
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    fun getTranslation(router: String, key: String, callback: OnTranslationSearched) {
        router?.let { translationsHelper = TranslationsHelper(api, database.translationDao(), it) }
        scope.launch {
            w1?.await()
            val translation = withContext(Dispatchers.Default) {
                translationsHelper?.getTranslation(router, key)
            }
            if (translation != null) {
                callback.onTranslationFound(translation)
            } else {
                callback.onTranslationNotFount(key)
            }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * ------------------------------------TOGGLES API----------------------------------------------
     * ---------------------------------------------------------------------------------------------
     */

    /**
     * Suspend method that will get all the toggles for the given router, if no router is provided
     * the method will try to use a router provided on a time before, being on another method
     * called or on the [configure] method.
     * Be careful to call this method only from a [CoroutineScope] or from another suspending
     * function.
     * @param router String containing the router from where the toggles wants to be fetched
     * from, null if a previous router wants to be used
     * @throws [RouterNotDefinedException] if no router has been defined for toggles up to
     * this point
     * @return List of [Toggle] for the given router
     * @author - sapardo10
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    suspend fun getToggles(router: String? = null): List<Toggle> {
        return togglesHelper?.getToggles(router) ?: emptyList()
    }

    /**
     * Method that will get all the toggles for the given router, if no router is provided the
     * method will try to use a router provided on a time before, being on another method called or
     * on the [configure] method
     * @param callback [OnTogglesSearched] that will be called when the operation has been finished
     * @param router String containing the router from where the toggles wants to be fetched
     * from, null if a previous router wants to be used
     * @throws [RouterNotDefinedException] if no router has been defined for toggles up to
     * this point
     * @author - sapardo10
     * @since 0.0.1
     */
    fun getToggles(router: String, callback: (list: List<Toggle>?) -> Unit) {
        router?.let { initializeLogsHelper(it) }
        scope.launch {
            w1?.await()
            var toggles = togglesHelper?.getToggles(router) ?: emptyList()
            if (toggles != null) {
                for (toggle: Toggle in toggles) {
                    toggle.key = "${toggle.key.replace("-${router}", "", ignoreCase = true)}"
                }
            }
            callback(toggles)
        }
    }

    /**
     * Suspend method that will get a defined toggle given a key and a router, if no router is
     * provided the method will try to use a router provided on a time before, being on another
     * method called or on the [configure] method.
     * Be careful to call this method only from a [CoroutineScope] or from another suspending
     * function
     * @param key String containing the key of the toggle that wants to be fetched
     * @param router String containing the router where the toggle should be searched, if null
     * it will try to use an older router to fetch the [Toggle]
     * @return [Toggle] with the given key and router, null if it wasn't possible to find it
     * @throws [RouterNotDefinedException] if no router has been defined for toggles up to
     * this point
     * @author - sapardo10
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    suspend fun getToggle(key: String, router: String? = null): Toggle? {
        router?.let { initializeLogsHelper(it) }
        return togglesHelper?.getToggle(key)
    }

    /**
     * Method that will get a defined toggle given a key and a router, if no router is
     * provided the method will try to use a router provided on a time before, being on another
     * method called or on the [configure] method.
     * @param key String containing the key of the toggle that wants to be fetched
     * @param callback [OnToggleSearched] that will be called when the operation has been
     * completed successfully.
     * @param router String containing the router where the toggle should be searched, if null
     * it will try to use an older router to fetch the [Toggle]
     * @throws [RouterNotDefinedException] if no router has been defined for toggles up to
     * this point
     * @author - sapardo10
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    fun getToggle(key: String, router: String? = null, callback: (result: String?) -> Unit) {
        router?.let { initializeLogsHelper(it) }
        scope.launch {
            w1?.await()
            val toggle = withContext(Dispatchers.Default) {
                togglesHelper?.getToggle(key)
            }
            if (toggle != null) {
                callback(toggle.value)
            } else {
                callback(null)
            }
        }
    }

    /**
     * Method that will get a defined toggle given a key and a router, if no router is
     * provided the method will try to use a router provided on a time before, being on another
     * method called or on the [configure] method.
     * @param key String containing the key of the toggle that wants to be fetched
     * @param callback [OnToggleSearched] that will be called when the operation has been
     * completed successfully.
     * @param router String containing the router where the toggle should be searched, if null
     * it will try to use an older router to fetch the [Toggle]
     * @throws [RouterNotDefinedException] if no router has been defined for toggles up to
     * this point
     * @author - Juan David
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    fun getBoolToggle(key: String, router: String? = null, callback: (result: Boolean?) -> Unit) {
        router?.let { initializeLogsHelper(it) }
        scope.launch {
            w1?.await()
            val toggle = withContext(Dispatchers.Default) {
                togglesHelper?.getToggle(key)
            }
            if (toggle != null) {
                if (toggle.value == "1") {
                    callback(true)
                } else {
                    callback(false)
                }
            } else {
                callback(null)
            }
        }
    }

    /**
     * Method that will get a defined toggle given a key and a router, if no router is
     * provided the method will try to use a router provided on a time before, being on another
     * method called or on the [configure] method.
     * @param key String containing the key of the toggle that wants to be fetched
     * @param callback [OnToggleSearched] that will be called when the operation has been
     * completed successfully.
     * @param router String containing the router where the toggle should be searched, if null
     * it will try to use an older router to fetch the [Toggle]
     * @throws [RouterNotDefinedException] if no router has been defined for toggles up to
     * this point
     * @author - Juan David
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    fun getIntToggle(key: String, callback: (result: Int?) -> Unit, router: String? = null) {
        router?.let { initializeLogsHelper(it) }
        scope.launch {
            w1?.await()
            val toggle = withContext(Dispatchers.Default) {
                togglesHelper?.getToggle(key)
            }
            if (toggle != null) {
                try {
                    callback(toggle.value.toInt())
                } catch (e: Exception) {
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }

    /**
     * Method that will get a defined toggle given a key and a router, if no router is
     * provided the method will try to use a router provided on a time before, being on another
     * method called or on the [configure] method.
     * @param key String containing the key of the toggle that wants to be fetched
     * @param callback [OnToggleSearched] that will be called when the operation has been
     * completed successfully.
     * @param router String containing the router where the toggle should be searched, if null
     * it will try to use an older router to fetch the [Toggle]
     * @throws [RouterNotDefinedException] if no router has been defined for toggles up to
     * this point
     * @author - Juan David
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    fun getDoubleToggle(key: String, callback: (result: Double?) -> Unit, router: String? = null) {
        router?.let { initializeLogsHelper(it) }
        scope.launch {
            w1?.await()
            val toggle = withContext(Dispatchers.Default) {
                togglesHelper?.getToggle(key)
            }
            if (toggle != null) {
                try {
                    callback(toggle.value.toDouble())
                } catch (e: Exception) {
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }


    /**
     * Method that will set the lifespan of the toggles for the given router
     * @param seconds Time in seconds the toggles are useful for.
     * @author - sapardo10
     * @since 0.0.1
     */
    fun setTogglesLifetime(seconds: Int) {
        scope.launch {
            delay((seconds * 1000).toLong())
            togglesHelper?.loadToggles()
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * --------------------------------------LOGS API-----------------------------------------------
     * ---------------------------------------------------------------------------------------------
     */

    /**
     * Method that sends a log to Appback
     * @param name String containing the name of the event to be sent
     * @param description String containing the description of the log
     * @param level [AppbackLogLevel] stating what kind of log is the one being sent
     * @param router String containing the router where the event should be sent, if null
     * it will try to use an older router to send the events
     * @author - sapardo10
     * @since 0.0.1
     */
    @Throws(RouterNotDefinedException::class)
    fun addEventLog(
        context: Context,
        router: String? = null,
        eventName: String,
        parameters: ArrayList<HashMap<String, Any>>,
        deviceInformation: Boolean = false
    ) {
        if (deviceInformation) {
            for ((key, value) in DeviceInformationUtils.getDeviceInformation(context = context)) {
                parameters.add(hashMapOf(key to value))
            }
        }

        router?.let { logsHelper = LogsHelper(api, database.logEventDao(), it) }
        scope.launch {
            w1?.await()
            router?.let { logsHelper?.sendLog(name = eventName, parameters = parameters, time = System.currentTimeMillis()/1000, router = it) }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * ------------------------------------PRIVATE METHODS------------------------------------------
     * ---------------------------------------------------------------------------------------------
     */

    /**
     * Method that builds the [AppbackDatabase] database for the library
     * @param context [Context] of the application
     * @return [AppbackDatabase] database of the library
     */
    private fun getAppbackDatabase(context: Context): AppbackDatabase {
        return Room.databaseBuilder(
            context,
            AppbackDatabase::class.java, DATABASE_NAME
        ).build()
    }

    /**
     * Method that will return a retrofit client for the [AppbackApi]
     * @param token [AccessToken] of the library to send the authorization to the server, if null
     * it will return a client without logging nor authorization interceptor
     * @return Retrofit client of [AppbackApi]
     */
    private fun getRetrofitClient(token: AccessToken? = null, baseURL: String?): AppbackApi {
        val client = if (token != null) {
            val httpClient = OkHttpClient.Builder()
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient
                .addInterceptor(AuthenticationInterceptor(token.accessToken))
                .addInterceptor(logging)
                .build()
        } else {
            OkHttpClient.Builder().build()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL ?: endpoint)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create<AppbackApi>(
            AppbackApi::class.java
        )
    }

    /**
     * Method that given an api key ill try to fetch an [AccessToken] from the service
     * @param apiKey String containing the api key that wants to be used for authentication
     */
    private suspend fun getAuthenticationToken(apiKey: String) {
        this@AppBack.apiKey = apiKey
        val localToken = token
        try {
            token = withContext(Dispatchers.Default) {
                api.getToken(apiKey)
            }
            api = getRetrofitClient(token, AUTH_BASE_URL)
            token.let {
                endpoint = it?.endpoint
            }
            api = getRetrofitClient(token, token?.endpoint + "/api/")
        } catch (e: Exception) {
            token = localToken
            e.printStackTrace()
        }
    }

    /**
     * Method that will initialize or change the router for [translationsHelper]
     * @param router String that states the router that wants to be used from now on
     */
    private suspend fun initializeTranslationsHelper(router: String, languageIdentifier: String) {
        translationsHelper =
            TranslationsHelper(api, database.translationDao(), router)
        translationsHelper?.loadTranslations(languageIdentifier)
    }

    /**
     * Method that will initialize or change the router for [togglesHelper]
     * @param router String that states the router that wants to be used from now on
     */
    private suspend fun initializeTogglesHelper(router: String) {
        togglesHelper =
            TogglesHelper(api, database.toggleDao(), router)
        togglesHelper?.loadToggles()
    }

    /**
     * Method that will initialize or change the router for [logsHelper]
     * @param router String that states the router that wants to be used from now on
     */
    private fun initializeLogsHelper(router: String) {
        logsHelper =
            LogsHelper(api, database.logEventDao(), router)
    }

}