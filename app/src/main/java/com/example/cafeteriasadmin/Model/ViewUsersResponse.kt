package com.example.cafeteriasadmin.Model

import com.google.gson.annotations.SerializedName

class ViewUsersResponse {
    @SerializedName("users")
     var viewUsersModel: List<ViewUsersModel>? = null

    @SerializedName("error")
     var error = false

    @SerializedName("message_en")
     var message_en: String? = null

    @SerializedName("message_ar")
     var message_ar: String? = null
}