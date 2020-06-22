package com.appback.appbacksdk.poko.transalation

import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class LanguageItem(
    @PrimaryKey @SerializedName("name") var key: String,
    @SerializedName("identifier") val value: String
)