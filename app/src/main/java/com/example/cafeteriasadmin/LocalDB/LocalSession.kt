package com.example.cafeteriasadmin.LocalDB

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.*

class LocalSession {
    companion object {
        private val PREF_NAME = "CafeteriasAdmain"
        private val TOKEN = "token"
        private val ID = "id"
        private val NAME = "name"
        private val PHONE = "phone"
        private val PASSWORD = "password"
        private val CAFETERIA = "cafeteria"
        private val ROLE = "role"
        private val STATUS = "status"
        private val MACTADDRESS = "macaddress"
        private val isSessionCreated = "isSessionCreated"
        private val LOGOUT_TIME_AR = "logout_time_ar"
        private val LOGOUT_TIME_EN = "logout_time_en"
        private val LOGIN_TIME_AR = "login_time_ar"
        private val LOGIN_TIME_EN = "login_time_en"
        private val LOCAL = "LOCAL"

        private var mPreferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
    }

    fun LocalSession(context: Context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, 0)
        editor = mPreferences!!.edit()
    }


    fun createSession(token: String?, Id: String?, name: String?, phone: String?, cafeteria: String?, role: String?, status: String?, macaddress: String?, password: String?) {
        editor!!.putBoolean(isSessionCreated, true)
        editor!!.putString(TOKEN, token)
        editor!!.putString(ID, Id)
        editor!!.putString(NAME, name)
        editor!!.putString(PHONE, phone)
        editor!!.putString(CAFETERIA, cafeteria)
        editor!!.putString(ROLE, role)
        editor!!.putString(STATUS, status)
        editor!!.putString(MACTADDRESS, macaddress)
        editor!!.putString(PASSWORD, password)
        editor!!.putString(LOCAL, Locale.getDefault().language)
        editor!!.apply()
        editor!!.commit()
    }

    fun login_time_ar(date: String?) {
        editor!!.putString(LOGIN_TIME_AR, date)
        editor!!.apply()
        editor!!.commit()
    }
    fun login_time_en(date: String?) {
        editor!!.putString(LOGIN_TIME_EN, date)
        editor!!.apply()
        editor!!.commit()
    }

    fun logout_time_ar(date: String?) {
        editor!!.putString(LOGOUT_TIME_AR, date)
        editor!!.apply()
        editor!!.commit()
    }
    fun logout_time_en(date: String?) {
        editor!!.putString(LOGOUT_TIME_EN, date)
        editor!!.apply()
        editor!!.commit()
    }


    fun getIsSessionCreated(): Boolean? {
        return mPreferences!!.getBoolean(isSessionCreated, false)
    }


    fun getId(): String? {
        return mPreferences!!.getString(ID, "")
    }

    fun getName(): String? {
        return mPreferences!!.getString(NAME, "")
    }


    fun getPhone(): String? {
        return mPreferences!!.getString(PHONE, "")
    }

    fun getPassword(): String? {
        return mPreferences!!.getString(PASSWORD, "")
    }

    fun getCafeteria(): String? {
        return mPreferences!!.getString(CAFETERIA, "")
    }

     fun getRole(): String? {
        return mPreferences!!.getString(ROLE, "")
    }


    fun getStatus(): String? {
        return mPreferences!!.getString(STATUS, "")
    }

    fun getMactaddress(): String? {
        return mPreferences!!.getString(MACTADDRESS, "")
    }

    fun getToken(): String? {
        return mPreferences!!.getString(TOKEN, "")
    }


    fun getLogin_time_ar(): String? {
        return mPreferences!!.getString(LOGIN_TIME_AR, "")
    }
    fun getLogin_time_en(): String? {
        return mPreferences!!.getString(LOGIN_TIME_EN, "")
    }

    fun getLogout_time_ar(): String? {
        return mPreferences!!.getString(LOGOUT_TIME_AR, "")
    }
    fun getLogout_time_en(): String? {
        return mPreferences!!.getString(LOGOUT_TIME_EN, "")
    }

    fun getLocal(): String? {
        return mPreferences!!.getString(LOCAL, "")
    }

    fun setLocal(lan: String?, context: Context) {
        editor!!.putString(LOCAL, lan)
        editor!!.apply()
        editor!!.commit()
        val locale = Locale(lan)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.resources.updateConfiguration(
            config,
            context.resources.displayMetrics
        )
    }

    fun clearSession() {
        editor!!.clear()
        editor!!.apply()
        editor!!.commit()
    }


}