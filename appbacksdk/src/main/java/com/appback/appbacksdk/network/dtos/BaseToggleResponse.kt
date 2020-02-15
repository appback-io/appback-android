package com.appback.appbacksdk.network.dtos

import com.appback.appbacksdk.poko.toggle.Toggle
import com.google.gson.annotations.SerializedName

internal data class BaseToggleResponse(
    @SerializedName("toggles") val toggles: List<Toggle>
)