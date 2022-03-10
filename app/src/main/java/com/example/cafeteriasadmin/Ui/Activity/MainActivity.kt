package com.example.cafeteriasadmin.Ui.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.LogoutResponse
import com.example.cafeteriasadmin.Model.MainModel
import com.example.cafeteriasadmin.Model.ViewUsersModel
import com.example.cafeteriasadmin.Model.ViewUsersResponse
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Ui.Adapter.Main_Adapter
import com.example.cafeteriasadmin.Ui.Adapter.ViewUsers_Adapter
import com.example.cafeteriasadmin.Utilty.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
   lateinit var recyclerView: RecyclerView
   lateinit var adapter: Main_Adapter
   lateinit var Tv_Name: TextView
   lateinit var localSession: LocalSession
   lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Tv_Name = findViewById(R.id.name)
        recyclerView = findViewById(R.id.ProductRecyclerView)
        mContext = this
        localSession = LocalSession()
        localSession.LocalSession(mContext)


        Tv_Name.text=localSession.getName()
        val linearLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager

        val arrayList: ArrayList<MainModel> = ArrayList<MainModel>()
        arrayList.add(MainModel(getString(R.string.cafeteria_registeration), R.drawable.cafeterias_registeration))
        arrayList.add(MainModel(getString(R.string.users_view), R.drawable.view_users))
        arrayList.add(MainModel(getString(R.string.settings), R.drawable.settengs))
        arrayList.add(MainModel(getString(R.string.actionbar_notification), R.drawable.notitfcation))
        adapter = Main_Adapter(arrayList, this)
        recyclerView.adapter = adapter
    }

    // <-- Logout -->
    fun logout(view: View) {
        val builder = AlertDialog.Builder(this)
        val view1: View = layoutInflater.inflate(R.layout.logout_massage, null)
        val confirm_tv:TextView = view1.findViewById(R.id.confirm_tv)
        val cancle_tv:TextView = view1.findViewById(R.id.cancle_tv)
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
                        if(!response.body()!!.error)
                        {
                            localSession.logout_time_ar(date_ar())
                            localSession.logout_time_en(date_en())
                            val intent1 = Intent(this@MainActivity, Session_Logout::class.java)
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


}
