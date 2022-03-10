package com.example.cafeteriasadmin.Model

import com.google.gson.annotations.SerializedName

class NotificationsViewResponse {
    @SerializedName("notifications")
    var AddNotificationModel: List<AddNotificationModel>? = null

    @SerializedName("error")
    var error = false

    @SerializedName("message_en")
    var message_en: String? = null

    @SerializedName("message_ar")
    var message_ar: String? = null
}