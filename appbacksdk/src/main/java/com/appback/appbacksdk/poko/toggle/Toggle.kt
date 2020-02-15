package com.appback.appbacksdk.poko.toggle

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "toggle")
data class Toggle(
    @PrimaryKey @SerializedName("key") var key: String,
    @SerializedName("value") val value: String
)