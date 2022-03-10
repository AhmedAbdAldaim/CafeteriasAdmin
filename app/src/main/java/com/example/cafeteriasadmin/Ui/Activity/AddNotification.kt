package com.example.cafeteriasadmin.Ui.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.AddNotificationResponse
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Utilty.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNotification : AppCompatActivity() {
    lateinit var ed_name: EditText
    lateinit var ed_notification: EditText
    lateinit var btn_notification: Button
    lateinit var mContext: Context
    lateinit var localSession: LocalSession

    private val Tag_failure = "failure"
    private val TAG_server = "TAG_server"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notification)

        //Action Bar
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.actionbar_add_notification)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        ed_name = findViewById(R.id.ed_title)
        ed_notification = findViewById(R.id.ed_noitifcation)
        btn_notification = findViewById(R.id.notification_btn)
        mContext = this

        localSession = LocalSession()
        localSession.LocalSession(mContext)
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        ed_notification.setOnClickListener(View.OnClickListener { view ->
            if (view.id == ed_notification.getId()) {
                ed_notification.setCursorVisible(true)
            }
        })
        ed_notification.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, event ->
            ed_notification.setCursorVisible(false)
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(ed_notification.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
            }
            false
        })

        //<-- btn notification -->
        btn_notification.setOnClickListener {
            val tv_name: String = ed_name.text.toString().trim()
            val tv_notification: String = ed_notification.text.toString().trim()

            if (CheckFields(tv_name, tv_notification)!!)
            {
                if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected)
                {
                    //<--Hidden Keyboard
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isAcceptingText)
                    {
                        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    }
                    Notification(tv_name, tv_notification)
                }
                else
                {
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet), this)
                }
            }
        }
    }



    // <-- Check Fields Function -->
    fun CheckFields(name: String, notification: String): Boolean?
    {
        if (name.isEmpty()) {
            ed_name.error = getString(R.string.title_empty)
            ed_name.requestFocus()
            return false
        }
        if (notification.isEmpty()) {
            ed_notification.error = getString(R.string.notification_empty)
            ed_notification.requestFocus()
            return false
        }
        return true
    }

    // <-- Send Data TO request And Git Response Status
    fun Notification(name: String?, notification: String? ) {
        val loading= ProgressDialog.show(this, null, getString(R.string.wait), false, false)
        loading.setContentView(R.layout.progressbar)
        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.setCancelable(false)
        loading.setCanceledOnTouchOutside(false)


        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
        val call = requestInterface.NotitfactionFun(name, notification, "Bearer " + localSession.getToken())
        call!!.enqueue(object : Callback<AddNotificationResponse?> {
            override fun onResponse(call: Call<AddNotificationResponse?>, response: Response<AddNotificationResponse?>) {
                if (response.isSuccessful())
                {
                    if (!response.body()!!.error)
                    {
                        ed_name.setText("")
                        ed_notification.setText("")
                        loading.dismiss()
                        val builder = AlertDialog.Builder(mContext)
                        val layoutInflater = LayoutInflater.from(mContext)
                        val view: View = layoutInflater.inflate(R.layout.custom_successfully_registered, null)
                        val ok = view.findViewById<TextView>(R.id.successfully_registered)
                        val button = view.findViewById<TextView>(R.id.btn_done)
                        ok.text = resources.getString(R.string.notification_successfully)
                        builder.setView(view)
                        val alertDialog = builder.create()
                        val insetDrawable = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20)
                        alertDialog.getWindow()!!.setBackgroundDrawable(insetDrawable)
                        alertDialog.show()
                        button.setOnClickListener { alertDialog.dismiss() }

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
                    Log.i(TAG_server, response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<AddNotificationResponse?>, t: Throwable) {
                loading.dismiss()
                Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet_slow), mContext)
                Utility.printLog(Tag_failure, t.message)
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
