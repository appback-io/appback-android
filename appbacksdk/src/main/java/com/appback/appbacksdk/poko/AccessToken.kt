package com.appback.appbacksdk.poko

import com.google.gson.annotations.SerializedName

internal data class AccessToken(
    @SerializedName("access_token") val accessToken: String
)