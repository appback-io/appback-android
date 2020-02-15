package com.appback.appbacksdk.callbacks

import com.appback.appbacksdk.poko.transalation.Translation

/**
 * Callback interface that must be implemented by the caller of the methods that search for
 * a specific translation:
 * 1. [com.appback.appbacksdk.Appback.getTranslation]
 * @author - sapardo10
 * @since 0.0.1
 */
interface OnTranslationSearched {

    /**
     * Method that should be called when the translation that was asked is found
     * @param translation [Translation] that the method found for the given key
     * @author - sapardo10
     * @since 0.0.1
     */
    fun onTranslationFound(translation: Translation)

    /**
     * Method that should be called when the translation for the kay is not found
     * @param key [String] the user gave to found a translation.
     * @author - sapardo10
     * @since 0.0.1
     */
    fun onTranslationNotFount(key: String)
}