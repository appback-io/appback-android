package com.appback.appbacksdk.poko.transalation

import com.google.gson.annotations.SerializedName

data class Language(
    @SerializedName("languages") val languages: List<LanguageItem>
)