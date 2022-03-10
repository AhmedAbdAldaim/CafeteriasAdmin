package com.example.cafeteriasadmin.Network

import com.example.cafeteriasadmin.Model.*
import retrofit2.Call
import retrofit2.http.*

interface RequestInterface {

//  <-- Login -->
    @FormUrlEncoded
    @POST("login")
    fun Login(@Field("phone") phone: String?,
              @Field("password") password: String?)
        : Call<LoginResponse?>?


//  <-- Cafeterias Registeration -->
    @FormUrlEncoded
    @POST("register")
    fun CafeteriasRegisteration(
    @Field("name") name: String?,
    @Field("cafeteria") cafeteria: String?,
    @Field("phone") phone: String?,
    @Field("password") password: String?,
    @Field("location") location: String?,
    @Field("expir_date") expir_date: String?,
    @Header("Authorization") authorization: String?
): Call<CafeteriasRegisterationResponse?>?


//  <-- Edit Profile -->
    @FormUrlEncoded
    @PUT("profile/edit")
    fun EditProfile(
    @Field("name") name: String?,
    @Field("phone") phone: String?,
    @Field("password") password: String?,
    @Header("Authorization") authorization: String?
): Call<EditProfileResponse?>?


//  <--  GET All users -->
    @GET("users")
    fun GetAllCafeterias(
        @Header("Authorization") authorization: String?
    ): Call<ViewUsersResponse?>?


//  <-- User Status -->
    @FormUrlEncoded
    @PUT("status/{user_id}")
    fun UserStatus(
    @Path("user_id") user_id: String?,
    @Field("status") status: String?,
    @Header("Authorization") authorization: String?
): Call<UserStatusResponse?>?


//  <-- Delete Mac -->
    @PUT("mac/{user_id}")
    fun DeleteMac(
    @Path("user_id") user_id: String?,
    @Header("Authorization") authorization: String?
): Call<DeleteMacResponse?>?

    //  <-- Edit Users -->
    @FormUrlEncoded
    @PUT("profile/user/{user_id}")
    fun EditUsersProfile(
        @Path("user_id") user_id: String?,
        @Field("name") name: String?,
        @Field("cafeteria") cafeteria: String?,
        @Field("phone") phone: String?,
        @Field("location") location: String?,
        @Field("expir_date") expir_date: String?,
        @Header("Authorization") authorization: String?
    ): Call<EditProfileResponse?>?

    //  <-- Edit Users password -->
    @FormUrlEncoded
    @PUT("profile/user/{user_id}")
    fun EditUsersPassword(
        @Path("user_id") user_id: String?,
        @Field("password") password: String?,
        @Header("Authorization") authorization: String?
    ): Call<EditProfileResponse?>?

    //  <-- Notificaton -->
    @FormUrlEncoded
    @POST("notifications")
    fun NotitfactionFun(
        @Field("name") name: String?,
        @Field("notification") cafeteria: String?,
        @Header("Authorization") authorization: String?
    ): Call<AddNotificationResponse?>?

    //  <--  GET All Notificatons -->
    @GET("notifications")
    fun GetAllNotifications(
        @Header("Authorization") authorization: String?
    ): Call<NotificationsViewResponse?>?

    //  <-- Delete Notitfaction -->
    @DELETE("notifications/{id}")
    fun DeleteNotification(
        @Path("id") id: String?,
        @Header("Authorization") authorization: String?
    ): Call<DeleteNotificationResponse?>?

    //  <-- Edit Notitfaction -->
    @FormUrlEncoded
    @PUT("notifications/{id}")
    fun EditNotification(
        @Path("id") id: String?,
        @Field("name") name: String?,
        @Field("notification") cafeteria: String?,
        @Header("Authorization") authorization: String?
    ): Call<AddNotificationResponse?>?

    //  <-- Logout -->
    @POST("logoutApi")
    fun LogoutApi(
        @Header("Authorization") authorization: String?
    ): Call<LogoutResponse?>?

}