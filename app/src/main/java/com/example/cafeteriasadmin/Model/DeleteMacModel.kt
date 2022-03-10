package com.example.cafeteriasadmin.Model

import com.google.gson.annotations.SerializedName

class DeleteMacModel {
    @SerializedName("id")
    private val id: String? = null

    @SerializedName("name")
    private val name: String? = null

    @SerializedName("phone")
    private val phone: String? = null

    @SerializedName("cafeteria")
    private val cafeteria: String? = null

    @SerializedName("status")
    private val status: String? = null

    @SerializedName("role")
    private val role: String? = null

    @SerializedName("mac_address")
    private val mac_address: String? = null

    @SerializedName("expir_date")
    private val expir_date: String? = null

    @SerializedName("created_at")
    var created_at: String? = null

    @SerializedName("updated_at")
    var updated_at: String? = null
}