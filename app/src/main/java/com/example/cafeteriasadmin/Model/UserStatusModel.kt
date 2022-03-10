package com.example.cafeteriasadmin.Model

import com.google.gson.annotations.SerializedName

class UserStatusModel {
    @SerializedName("id")
     val id: String? = null

    @SerializedName("name")
     val name: String? = null

    @SerializedName("phone")
     val phone: String? = null

    @SerializedName("cafeteria")
     val cafeteria: String? = null

    @SerializedName("status")
     val status: String? = null

    @SerializedName("role")
     val role: String? = null

    @SerializedName("mac_address")
     val mac_address: String? = null

    @SerializedName("location")
    val location: String? = null

    @SerializedName("expir_date")
     val expir_date: String? = null

    @SerializedName("created_at")
    var created_at: String? = null

    @SerializedName("updated_at")
    var updated_at: String? = null
}