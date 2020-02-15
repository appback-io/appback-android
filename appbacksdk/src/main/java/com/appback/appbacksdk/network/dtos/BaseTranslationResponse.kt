package com.appback.appbacksdk.network.dtos

import com.appback.appbacksdk.poko.transalation.Translation
import com.google.gson.annotations.SerializedName

internal data class BaseTranslationResponse(
    @SerializedName("translations") val translations: List<Translation>
)