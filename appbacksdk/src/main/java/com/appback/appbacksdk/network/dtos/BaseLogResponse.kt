package com.appback.appbacksdk.network.dtos

import com.google.gson.annotations.SerializedName

internal data class BaseLogResponse(
    @SerializedName("code") val code: Int
)