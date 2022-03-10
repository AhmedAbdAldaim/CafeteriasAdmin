package com.example.cafeteriasadmin.Ui.Activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.R
import java.util.*

class SplashScreen : AppCompatActivity() {
    var img_splash: ImageView? = null
    var tv_splash: TextView? = null

    var runnable = Runnable{
    val intent = Intent(applicationContext, Login::class.java)
    startActivity(intent)
    finish()

    var localSession = LocalSession()
        localSession.LocalSession(applicationContext)
    var IsSessionCreated: Boolean? = localSession!!.getIsSessionCreated()


        /*
        * Svae Language Selected
        * */
        val sharedPreferences = getSharedPreferences("langdb", MODE_PRIVATE)
        val lang = sharedPreferences.getString("lang", "ar")
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)



    if ((IsSessionCreated!!.equals(true)))
     {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
     }
     else
     {
        val intent = Intent(applicationContext, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
     }
  }


   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        img_splash = findViewById(R.id.img_splash)
        tv_splash = findViewById(R.id.tv_splash)
        val animationUtils1 = AnimationUtils.loadAnimation(this, R.anim.animation_splashscreen)
        val animationUtils2 = AnimationUtils.loadAnimation(this, R.anim.animation_splashscreen_tv)

        img_splash!!.setAnimation(animationUtils1)
        tv_splash!!.setAnimation(animationUtils2)
        tv_splash!!.postDelayed(runnable, 3010)
    }
}
