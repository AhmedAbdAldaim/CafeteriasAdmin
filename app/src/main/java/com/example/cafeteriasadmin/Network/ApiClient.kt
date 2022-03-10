package com.example.cafeteriasadmin.Network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    private val TAG = "ApiClient"
    companion object {
        var BASE_URL = "http://cafeterias.herokuapp.com/api/admin/"
        var URL_Logout = "http://cafeterias.herokuapp.com/api/"
    fun getClient(baseUrl: String): Retrofit? {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.MINUTES)
            .writeTimeout(60, TimeUnit.MINUTES)
            .addInterceptor(logging)
            .build()

        val gson = GsonBuilder().setLenient().setLenient().create()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    }
}