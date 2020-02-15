package com.appback.appbacksdk.poko.log

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "log_event")
data class LogEvent(
    @PrimaryKey(autoGenerate = true) val key: Int? = 0,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("level") val level: Int
)