package com.example.cafeteriasadmin.Model

import com.google.gson.annotations.SerializedName

class AddNotificationModel {
    @SerializedName("id")
    val id: String? = null

    @SerializedName("name")
    val name: String? = null

    @SerializedName("notification")
    val notification: String? = null

    @SerializedName("created_at")
    var created_at: String? = null

    @SerializedName("updated_at")
    var updated_at: String? = null
}