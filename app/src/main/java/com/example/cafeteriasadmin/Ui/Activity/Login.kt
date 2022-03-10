package com.example.cafeteriasadmin.Ui.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.LoginResponse
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Utilty.Utility
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class Login : AppCompatActivity() {
    lateinit var img_visibiltyoff: ImageView
    lateinit var img_visibilty: ImageView
    lateinit var ed_password: EditText
    lateinit var localSession: LocalSession
    lateinit var mContext: Context
    private val Tag_failure = "failure"
    private val TAG_server = "TAG_server"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        img_visibiltyoff = findViewById(R.id.visibiltyoff)
        img_visibilty = findViewById(R.id.visibilty)
        ed_password = findViewById(R.id.ed_password)

        mContext = this
        localSession = LocalSession()
        localSession.LocalSession(mContext)
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        ed_password.setOnClickListener(View.OnClickListener { view ->
            if (view.id == ed_password.getId()) {
                ed_password.setCursorVisible(true)
            }
        })
        ed_password.setOnEditorActionListener(OnEditorActionListener { textView, i, event ->
            ed_password.setCursorVisible(false)
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(ed_password.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS)
            }
            false
        })


        //  <-- Onclick Register Button-->
        btn_login.setOnClickListener(View.OnClickListener {
            val phone: String = ed_phone_number.text.toString().trim()
            val password = ed_password.text.toString().trim()
            if (CheckFields(phone, password)!!)
            {
                if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected)
                {
                    //<--Hidden Keyboard
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isAcceptingText)
                    {
                        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    }
                    localSession.login_time_ar(date_ar())
                    localSession.login_time_en(date_en())
                    Login(phone, password)
                } else
                {
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet), this@Login)
                }
            }
        })
    }

    // <-- Visibilty and Invisibilty Password -->
    fun Visibiltyoff_Password(view: View) {
        img_visibiltyoff.visibility= View.GONE
        img_visibilty.visibility= View.VISIBLE
        ed_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }

    fun Visibilty_Password(view: View) {
        img_visibilty.visibility= View.GONE
        img_visibiltyoff.visibility= View.VISIBLE
        ed_password.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    // <-- Check Fields Function -->
    fun CheckFields(phone: String, password: String): Boolean? {
        if (phone.isEmpty()) {
            ed_phone_number.error = getString(R.string.phone_number_empty)
            ed_phone_number.requestFocus()
            return false
        } else if (!phone.matches(Regex("[0-9]{10}"))) {
            ed_phone_number.error = getString(R.string.phone_valid)
            ed_phone_number.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            ed_password.error = getString(R.string.password_empty)
            ed_password.requestFocus()
            return false
        } else if (password.length < 8) {
            ed_password.error = getString(R.string.password_check)
            ed_password.requestFocus()
            return false
        }
        return true
    }

    // <-- Send Data TO request And Git Response Status
    fun Login(phone: String?, password: String?) {
        val loading= ProgressDialog.show(this, null, getString(R.string.wait), false, false)
        loading.setContentView(R.layout.progressbar)
        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.setCancelable(false)
        loading.setCanceledOnTouchOutside(false)


        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
        val call = requestInterface.Login(phone, password)
        call!!.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(call: Call<LoginResponse?>, response: Response<LoginResponse?>) {
                if (response.isSuccessful())
                {
                    if (!response.body()!!.error)
                    {
                        loading.dismiss()
                        localSession.createSession(
                            response.body()!!.token,
                            response.body()!!.loginModel!!.id,
                            response.body()!!.loginModel!!.name,
                            response.body()!!.loginModel!!.phone,
                            response.body()!!.loginModel!!.cafeteria,
                            response.body()!!.loginModel!!.role,
                            response.body()!!.loginModel!!.status,
                            response.body()!!.loginModel!!.mac_address,
                            ed_password.text.toString().trim())
                        val intent = Intent(mContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()

                    }
                    else
                    {
                        loading.dismiss()
                        if (ed_phone_number.getHint().toString().contains("رقم الهاتف") || ed_password.hint.toString().contains("كلمة المرور"))
                        {
                            Utility.showAlertDialog(getString(R.string.error), response.body()!!.message_ar, mContext)
                        }
                        else if (ed_phone_number.getHint().toString().contains("Phone Number") || ed_password.hint.toString().contains("Password"))
                        {
                            Utility.showAlertDialog(getString(R.string.error), response.body()!!.message_en, mContext)
                        }
                    }

                }
                else
                {
                    loading.dismiss()
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), mContext)
                    Log.i(TAG_server, response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                loading.dismiss()
                Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet_slow), mContext)
                Utility.printLog(Tag_failure, t.message)
            }
        })
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


