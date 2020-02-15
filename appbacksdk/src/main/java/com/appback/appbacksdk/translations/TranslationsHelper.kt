package com.appback.appbacksdk.translations

import com.appback.appbacksdk.database.TranslationDao
import com.appback.appbacksdk.network.AppbackApi
import com.appback.appbacksdk.poko.transalation.Translation

/**
 * Class that will hold all the logic for the translations flow on the library. Everything related
 * to [Translation] and it's logic should go inside this class.
 * @param api [AppbackApi] containing the necessary methods to perform api calls
 * @param translationDao [TranslationDao] containing the necessary methods to perform database
 * transactions
 * @param router String containing the router configured for this helper. This router can be
 * changed on runtime.
 * @author - sapardo10
 */
internal class TranslationsHelper(
    private val api: AppbackApi,
    private val translationDao: TranslationDao,
    var router: String
) {

    /**
     * Method that will load the translations for the router defined when this class was created
     */
    suspend fun loadTranslations() {
        val translationsResponse = api.loadTranslations(router)
        val translations = translationsResponse.translations
        for (translation: Translation in translations) {
            translation.key += "-$router"
        }
        translationDao.insertAll(translations)
    }

    /**
     * Method that will get all the translations available for the specified router
     * @param localRouter String containing the router that wants to be checked, can be passed a
     * null parameter to indicate that the router to use is the one defined on the class
     * @return List of [Translation] containing all the translations for the router, empty list if none are
     * found
     */
    suspend fun getTranslations(localRouter: String? = null): List<Translation> {
        val databaseRouter = if (localRouter.isNullOrBlank()) {
            "-$router"
        } else {
            "-$localRouter"
        }
        var translations = translationDao.getAllByRouter(databaseRouter)
        if (translations.isEmpty()) {
            val translationsResponse = api.loadTranslations(router)
            val translationsList = translationsResponse.translations
            for (translation: Translation in translationsList) {
                translation.key += "-$router"
            }
            translationDao.insertAll(translationsList)
            translations = translationsList
        }

        return translations

    }

    /**
     * Method that will get the translation for the specified router with that key
     * @param key String containing the key of the translation
     * @return [Translation] that has the key and router defined, null if not found on database
     */
    suspend fun getTranslation(key: String): Translation? {
        return try {
            translationDao.findTranslationAsync("$key-$router")
        } catch (e: Exception) {
            null
        }
    }
}
