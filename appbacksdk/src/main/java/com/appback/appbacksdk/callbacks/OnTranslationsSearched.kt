package com.appback.appbacksdk.callbacks

import com.appback.appbacksdk.poko.transalation.Translation

/**
 * Callback interface that must be implemented by the caller of the methods that search for
 * a list of translations:
 * 1. [com.appback.appbacksdk.Appback.getTranslations]
 * @author - sapardo10
 * @since 0.0.1
 */
interface OnTranslationsSearched {

    /**
     * Method that should be called when the translations that were asked for are found, if
     * no translations are found it gives as parameter an empty list
     * @param translations List of [Translation] that has all the translations found for the given router
     * @author - sapardo10
     * @since 0.0.1
     */
    fun onTranslationsFound(translations: List<Translation>)
}