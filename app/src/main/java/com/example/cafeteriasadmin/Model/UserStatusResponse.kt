package com.example.cafeteriasadmin.Model

import com.google.gson.annotations.SerializedName

class UserStatusResponse {
    @SerializedName("user")
    private var loginModel: LoginModel? = null

    @SerializedName("error")
    private var error = false

    @SerializedName("message_en")
    private var message_en: String? = null

    @SerializedName("message_ar")
    private var message_ar: String? = null
}