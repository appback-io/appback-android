package com.appback.appbacksdk.callbacks

import com.appback.appbacksdk.poko.toggle.Toggle

/**
 * Callback interface that must be implemented by the caller of the methods that search for
 * a list of toggles:
 * 1. [com.appback.appbacksdk.Appback.getToggles]
 * @author - sapardo10
 * @since 0.0.1
 */
interface OnTogglesSearched {

    /**
     * Method that should be called when the toggles that were asked for are found, if
     * no toggles are found it gives as parameter an empty list
     * @param toggles List of [Toggle] that has all the toggles found for the given router
     * @author - sapardo10
     * @since 0.0.1
     */
    fun onTogglesFound(toggles: List<Toggle>)
}