package com.example.cafeteriasadmin.Ui.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.R

class Session_Logout : AppCompatActivity() {

    lateinit var logintime_tv:TextView
    lateinit var logouttime_tv:TextView
    lateinit var close:Button
    lateinit var localSession: LocalSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session__logout)
        logintime_tv = findViewById(R.id.login_time)
        logouttime_tv = findViewById(R.id.logout_time)
        close = findViewById(R.id.close)
        val sharedPreferences = getSharedPreferences("langdb", MODE_PRIVATE)
        val lang = sharedPreferences.getString("lang", "ar")
        localSession = LocalSession()
        localSession.LocalSession(this)

        if(lang=="ar"){
            logintime_tv.text = localSession.getLogin_time_ar()
            logouttime_tv.text = localSession.getLogout_time_ar()
        }else if(lang == "en"){
            logintime_tv.text = localSession.getLogin_time_en()
            logouttime_tv.text = localSession.getLogout_time_en()
        }

        close.setOnClickListener {
            localSession.clearSession()
            val intent = Intent(this, Login::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        localSession.clearSession()
        super.onBackPressed()
    }
}
