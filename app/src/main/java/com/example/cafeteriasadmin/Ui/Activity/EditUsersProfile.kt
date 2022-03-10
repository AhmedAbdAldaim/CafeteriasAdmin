package com.example.cafeteriasadmin.Ui.Activity

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.EditProfileResponse
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Utilty.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class EditUsersProfile : AppCompatActivity() {

    lateinit var ed_username: EditText
    lateinit var ed_cafeteria: EditText
    lateinit var ed_phone_number: EditText
    lateinit var ed_location: EditText
    lateinit var tv_expit_date: TextView
    lateinit var edituserprofile_btn: Button
    lateinit var localSession: LocalSession
    lateinit var mContext: Context

    lateinit var lang:String
    lateinit var userid:String
    var Tag = "reg"
    lateinit var onDateSetListener: DatePickerDialog.OnDateSetListener
    private val Tag_failure = "failure"
    private val TAG_server = "TAG_server"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_users_profile)

        //Action Bar
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.actionbar_edit_users_profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        ed_username = findViewById(R.id.ed_username)
        ed_cafeteria = findViewById(R.id.ed_cafeteria_restaurant)
        ed_phone_number = findViewById(R.id.ed_phone_number)
        ed_location = findViewById(R.id.ed_location)
        tv_expit_date = findViewById(R.id.date)
        edituserprofile_btn = findViewById(R.id.edituserprofile_btn)


        mContext = this

        localSession = LocalSession()
        localSession.LocalSession(mContext)
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        val intent = intent;
        userid = intent.getStringExtra("id")
        var name = intent.getStringExtra("name")
        var cafeteria = intent.getStringExtra("cafeteria")
        var phone = intent.getStringExtra("phone")
        var location = intent.getStringExtra("location")
        var expir_date = intent.getStringExtra("expir_date")

        ed_username.setText(name)
        ed_phone_number.setText(phone)
        ed_location.setText(location)
        ed_cafeteria.setText(cafeteria)
        tv_expit_date.setText(expir_date)

        getBirthDay()

        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        ed_location.setOnClickListener(View.OnClickListener { view ->
            if (view.id == ed_location.getId()) {
                ed_location.setCursorVisible(true)
            }
        })
        ed_location.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, event ->
            ed_location.setCursorVisible(
                false
            )
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(ed_location.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
            }
            false
        })

        edituserprofile_btn.setOnClickListener {
            val username: String = ed_username.text.toString().trim()
            val cafeteria: String = ed_cafeteria.text.toString().trim()
            val phone: String = ed_phone_number.text.toString().trim()
            val location:String  = ed_location.text.toString().trim()
            val expir_date:String = tv_expit_date.text.toString().trim()

            if (CheckFields(username, cafeteria, phone, location, expir_date)!!)
            {
                if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected)
                {
                    //<--Hidden Keyboard
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isAcceptingText)
                    {
                        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    }
                    EditUsersProfile(userid, username, cafeteria, phone, location, expir_date)
                }
                else
                {
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet), this)
                }
            }
        }
    }

    fun getBirthDay() {
        tv_expit_date.setOnClickListener(View.OnClickListener {
            val sharedPreferences = getSharedPreferences("langdb", MODE_PRIVATE)
            lang = sharedPreferences.getString("lang", "ar")!!
            if (lang == "en")
            {
                Locale.setDefault(Locale.forLanguageTag("en"))
            }
            else if (lang == "ar")
            {
                Locale.setDefault(Locale.forLanguageTag("ar"))
            }
            val cal = Calendar.getInstance()
            val year = cal[Calendar.YEAR]
            val month = cal[Calendar.MONTH]
            val Day = cal[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth, onDateSetListener,
                year, month, Day)
            datePickerDialog.datePicker.minDate=System.currentTimeMillis()-1000
            datePickerDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            datePickerDialog.show()
        })

        onDateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            Log.e(Tag, "onDateSet : mm/dd/yyy:" + year + "/" + month + "/" + dayOfMonth)
            val date = "$year-${month + 1}-$dayOfMonth"
            tv_expit_date.setText(date)

        }
    }


    // <-- Check Fields Function -->
    fun CheckFields(username: String, cafeteria: String, phone: String, location: String, expir_date: String): Boolean? {
        if (username.isEmpty()) {
            ed_username.error = getString(R.string.username_cr_empty)
            ed_username.requestFocus()
            return false
        }
        if (cafeteria.isEmpty()) {
            ed_cafeteria.error = getString(R.string.cafeteria_cr_empty)
            ed_cafeteria.requestFocus()
            return false
        }
        if (phone.isEmpty()) {
            ed_phone_number.error = getString(R.string.phone_number_empty)
            ed_phone_number.requestFocus()
            return false
        } else if (!phone.matches(Regex("[0-9]{10}"))) {
            ed_phone_number.error = getString(R.string.phone_valid)
            ed_phone_number.requestFocus()
            return false
        }

        if (location.isEmpty()) {
            ed_location.error = getString(R.string.location_cr_empty)
            ed_location.requestFocus()
            return false
        }
        if (expir_date.isEmpty()) {
            tv_expit_date.error = getString(R.string.sub_expiry_date_cr_empty)
            ed_location.requestFocus()
            return false
        }
        return true
    }


    // <-- Send Data TO request And Git Response Status
    fun EditUsersProfile(id_:String?, username: String?, cafeteria: String?, phone: String?, location: String?, expir_date: String?) {
        val loading= ProgressDialog.show(this, null, getString(R.string.wait), false, false)
        loading.setContentView(R.layout.progressbar)
        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.setCancelable(false)
        loading.setCanceledOnTouchOutside(false)


        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
        val call = requestInterface.EditUsersProfile(id_, username, cafeteria, phone, location, expir_date, "Bearer " + localSession.getToken())
        call!!.enqueue(object : Callback<EditProfileResponse?> {
            override fun onResponse(call: Call<EditProfileResponse?>, response: Response<EditProfileResponse?>) {
                if (response.isSuccessful())
                {
                    if (!response.body()!!.error)
                    {
                        Toast.makeText(mContext, R.string.edit_successfully, Toast.LENGTH_SHORT).show()
                        val intent = Intent(mContext, ViewUsers::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                        loading.dismiss()
                    } else
                    {
                        loading.dismiss()
                        Utility.showAlertDialog(getString(R.string.error), response.body()!!.message_ar + " " + response.body()!!.message_en, mContext)
                    }
                }
                else
                {
                    loading.dismiss()
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror)+response.errorBody().toString(), mContext)
                    Log.i(TAG_server, response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<EditProfileResponse?>, t: Throwable) {
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

