package com.example.cafeteriasadmin.Ui.Activity

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.AddNotificationModel
import com.example.cafeteriasadmin.Model.NotificationsViewResponse
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Ui.Adapter.NotificationsView_Adapter
import com.example.cafeteriasadmin.Utilty.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsView : AppCompatActivity() {

    lateinit  var Tv_Users_total: TextView
    lateinit  var tv_empty: TextView
    lateinit var tv_connect: TextView
    lateinit var button_connect: Button
    lateinit var recyclerView: RecyclerView
    lateinit var localSession: LocalSession
    lateinit var mContext: Context
    lateinit var notificationsviewAdapter: NotificationsView_Adapter

    private val TAG_server = "Server"
    private val Tag_failure = "failure"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications_view)

        //Action Bar
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.actionbar_view_notification)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        tv_connect = findViewById(R.id.connection)
        button_connect = findViewById(R.id.btnconnection)
        tv_empty = findViewById(R.id.empty)
        Tv_Users_total = findViewById(R.id.total)
        recyclerView = findViewById(R.id.rectable)

        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val linearLayoutManager = GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager

        mContext = this

        localSession = LocalSession()
        localSession.LocalSession(mContext)


        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected)
        {
            getAllNotifications()
        }
        else
        {
            tv_connect.setText(R.string.connect_internet)
            tv_connect.visibility = View.VISIBLE
            button_connect.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
            tv_empty.visibility = View.INVISIBLE
            button_connect.setOnClickListener {
                tv_connect.visibility = View.INVISIBLE
                button_connect.visibility = View.INVISIBLE
                recyclerView.visibility = View.VISIBLE
                getAllNotifications()
            }
        }
    }


    //<--   Git All Useres -->
    fun getAllNotifications() {
        val loading = ProgressDialog.show(this, null, getString(R.string.wait), false, false)
        loading.setContentView(R.layout.progressbar)
        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.setCancelable(false)
        loading.setCanceledOnTouchOutside(false)

        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
        val call: Call<NotificationsViewResponse?>? = requestInterface.GetAllNotifications("Bearer " + localSession.getToken())
        call!!.enqueue(object : Callback<NotificationsViewResponse?> {
            override fun onResponse(call: Call<NotificationsViewResponse?>, response: Response<NotificationsViewResponse?>) {
                if (response.isSuccessful())
                {
                    if(!response.body()!!.error)
                    {
                        notificationsviewAdapter = NotificationsView_Adapter(response.body()!!.AddNotificationModel!! as MutableList<AddNotificationModel>, mContext)
                        if (notificationsviewAdapter.getItemCount() === 0)
                        {
                            loading.dismiss()
                            tv_empty.visibility = View.VISIBLE
                            recyclerView.visibility = View.INVISIBLE
                            tv_empty.setText(R.string.empty_vu)
                            Tv_Users_total.visibility = View.VISIBLE
                            Tv_Users_total.text = 0.toString() + ""
                            return
                        }
                        else if (notificationsviewAdapter.getItemCount() > 0)
                        {
                            loading.dismiss()
                            notificationsviewAdapter.notifyDataSetChanged()
                            tv_empty.visibility = View.INVISIBLE
                            Tv_Users_total.visibility = View.VISIBLE
                            Tv_Users_total.setText(notificationsviewAdapter.getItemCount().toString() + "")
                            notificationsviewAdapter.notifyDataSetChanged()
                            recyclerView.adapter = notificationsviewAdapter
                        }
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
                    Log.i(TAG_server, response.errorBody().toString())
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), this@NotificationsView)
                }
            }

            override fun onFailure(call: Call<NotificationsViewResponse?>, t: Throwable) {
                loading.dismiss()
                tv_connect.setText(R.string.connect_internet_slow)
                tv_connect.visibility = View.VISIBLE
                button_connect.visibility = View.VISIBLE
                recyclerView.visibility = View.INVISIBLE
                tv_empty.visibility = View.INVISIBLE
                button_connect.setOnClickListener {
                    tv_connect.visibility = View.INVISIBLE
                    button_connect.visibility = View.INVISIBLE
                    recyclerView.visibility = View.VISIBLE
                    getAllNotifications()
                }
            }
        })
     }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
