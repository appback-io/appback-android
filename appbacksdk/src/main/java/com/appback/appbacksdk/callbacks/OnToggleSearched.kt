package com.appback.appbacksdk.callbacks

import com.appback.appbacksdk.poko.toggle.Toggle

/**
 * Callback interface that must be implemented by the caller of the methods that search for
 * a specific toggle:
 * 1. [com.appback.appbacksdk.Appback.getToggle]
 * @author - sapardo10
 * @since 0.0.1
 */
interface OnToggleSearched {

    /**
     * Method that should be called when the toggle that was asked is found
     * @param toggle [Toggle] that the method found for the given key
     * @author - sapardo10
     * @since 0.0.1
     */
    fun onToggleFound(toggle: Toggle)

    /**
     * Method that should be called when the toggle for the kay is not found
     * @param key [String] the user gave to found a toggle.
     * @author - sapardo10
     * @since 0.0.1
     */
    fun onToggleNotFound(key: String)
}