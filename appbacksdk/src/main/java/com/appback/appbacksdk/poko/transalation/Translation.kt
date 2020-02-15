package com.appback.appbacksdk.poko.transalation

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "translation")
data class Translation(

    @PrimaryKey @SerializedName("key") var key: String,
    @SerializedName("value") val value: String
)