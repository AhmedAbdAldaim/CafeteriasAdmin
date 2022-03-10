package com.example.cafeteriasadmin.Ui.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.ListviewSettingModel
import com.example.cafeteriasadmin.Model.LogoutResponse
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Ui.Adapter.ListviewSetting_Adapter
import com.example.cafeteriasadmin.Utilty.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Settings : AppCompatActivity() {
  lateinit var listView: ListView
  lateinit var localSession: LocalSession
  lateinit var radioButton: RadioButton
    lateinit var mContext: Context


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //Action Bar
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.actionbar_settings)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        listView = findViewById(R.id.listsetting)
        localSession = LocalSession()
        localSession.LocalSession(this)
        mContext = this

        val sharedPreferences = getSharedPreferences("langdb", MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        val arrayList: ArrayList<ListviewSettingModel> = ArrayList<ListviewSettingModel>()
        arrayList.add(ListviewSettingModel(resources.getString(R.string.edit_profile), R.drawable.ic_user))
        arrayList.add(ListviewSettingModel(resources.getString(R.string.language),R.drawable.ic_language))
        arrayList.add(ListviewSettingModel(resources.getString(R.string.logout), R.drawable.ic_logout))

        val arrayAdapter = ListviewSetting_Adapter(this, R.layout.item_row_listview_setting, arrayList)
        listView.adapter = arrayAdapter
        listView.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->

                when (i) {
                    0 -> startActivity(Intent(this@Settings, EditProfile::class.java))
                    1 -> {
                            val builderlan = AlertDialog.Builder(this@Settings)
                            val viewlan: View = layoutInflater.inflate(R.layout.language_select, null)
                            val radioGroup = viewlan.findViewById<RadioGroup>(R.id.radiogroup)
                            builderlan.setView(viewlan)
                            val dialoglan = builderlan.create()
                            radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                                val sel = radioGroup.checkedRadioButtonId
                                radioButton = viewlan.findViewById<RadioButton>(sel)
                                if (radioButton.getText().toString() == "العربية") {
                                    dialoglan.dismiss()
                                    val locale = Locale("ar")
                                    Locale.setDefault(locale)
                                    val config = Configuration()
                                    config.locale = locale
                                    resources.updateConfiguration(config, resources.displayMetrics)
                                    editor.putString("lang", "ar")
                                    editor.commit()
                                    val intent = Intent(this@Settings, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                } else if (radioButton.getText().toString() == "Engilsh") {
                                    dialoglan.dismiss()
                                    val locale = Locale("en")
                                    Locale.setDefault(locale)
                                    val config = Configuration()
                                    config.locale = locale
                                    resources.updateConfiguration(config, resources.displayMetrics)
                                    editor.putString("lang", "en")
                                    editor.commit()
                                    val intent = Intent(this@Settings, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            dialoglan.show()
                    }
                        2->{
                            val builder = AlertDialog.Builder(this)
                            val view1: View = layoutInflater.inflate(R.layout.logout_massage, null)
                            val confirm_tv: TextView = view1.findViewById(R.id.confirm_tv)
                            val cancle_tv: TextView = view1.findViewById(R.id.cancle_tv)
                            builder.setView(view1)

                            val dialog = builder.create()
                            val insetDrawable = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20)
                            dialog.getWindow()!!.setBackgroundDrawable(insetDrawable)

                            confirm_tv.setOnClickListener { v: View? ->
                                dialog.dismiss()
                                val loading = ProgressDialog.show(this, null, getString(R.string.wait), false, false)
                                loading.setContentView(R.layout.progressbar)
                                loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                loading.setCancelable(false)
                                loading.setCanceledOnTouchOutside(false)

                                // <-- Connect WIth Network And Check Response Successful or Failure -- >
                                val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.URL_Logout)!!.create(RequestInterface::class.java)
                                val call: Call<LogoutResponse?>? = requestInterface.LogoutApi("Bearer " + localSession.getToken())
                                call!!.enqueue(object : Callback<LogoutResponse?> {
                                    override fun onResponse(call: Call<LogoutResponse?>, response: Response<LogoutResponse?>) {
                                        if (response.isSuccessful())
                                        {
                                            if(!response.body()!!.error) {
                                                localSession.logout_time_ar(date_ar())
                                                localSession.logout_time_en(date_en())
                                                val intent1 = Intent(this@Settings, Session_Logout::class.java)
                                                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                                startActivity(intent1)
                                                finish()
                                            }
                                            else
                                            {
                                                loading.dismiss()
                                                Utility.showAlertDialog(getString(R.string.error), response.body()!!.message_ar + " " + response.body()!!.message_en, mContext)
                                            }
                                        }
                                        else
                                        {
                                            loading.dismiss()
                                            Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), mContext)
                                        }
                                    }

                                    override fun onFailure(call: Call<LogoutResponse?>, t: Throwable) {
                                        loading.dismiss()
                                        Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet_slow), mContext)
                                    }
                                })
                            }
                            cancle_tv.setOnClickListener { v: View? -> dialog.dismiss() }
                            dialog.show()
                    }
                }
            }
    }

    // <-- GetDate Funcation -->
    private fun date_ar(): String? {
        val sharedPreferences = getSharedPreferences("langdb", MODE_PRIVATE)
        val lang = sharedPreferences.getString("lang", "ar")
        val calendar = Calendar.getInstance()
        var date_ar: String? = null
        val motf_ar: DateFormat = SimpleDateFormat("EE - dd MMM yyyy -  HH:mm a ", Locale.forLanguageTag("ar"))
        date_ar = motf_ar.format(calendar.time)
        return date_ar
    }
    // <-- GetDate Funcation -->
    private fun date_en(): String? {
        val sharedPreferences = getSharedPreferences("langdb", MODE_PRIVATE)
        val lang = sharedPreferences.getString("lang", "ar")
        val calendar = Calendar.getInstance()
        var date_en: String? = null
        val motf_en: DateFormat = SimpleDateFormat("EE - dd MMM yyyy - HH:mm a ", Locale.ENGLISH)
        date_en = motf_en.format(calendar.time)
        return date_en
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
