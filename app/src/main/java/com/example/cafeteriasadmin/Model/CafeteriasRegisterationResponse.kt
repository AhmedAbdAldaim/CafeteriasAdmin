package com.example.cafeteriasadmin.Model

import com.google.gson.annotations.SerializedName

class CafeteriasRegisterationResponse {
    @SerializedName("newUser")
    var cafeteriasRegisterationModel: CafeteriasRegisterationModel? = null

    @SerializedName("error")
    var error = false

    @SerializedName("message_en")
    var message_en: String? = null

    @SerializedName("message_ar")
    var message_ar: String? = null
}