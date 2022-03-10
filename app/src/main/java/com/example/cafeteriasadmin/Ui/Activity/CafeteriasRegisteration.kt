package com.example.cafeteriasadmin.Ui.Activity


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.icu.util.Calendar
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.CafeteriasRegisterationResponse
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Utilty.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.util.*

class CafeteriasRegisteration : AppCompatActivity() {
    lateinit var img_visibiltyoff: ImageView
    lateinit var img_visibilty: ImageView
    lateinit var img_visibiltyoff_confirm: ImageView
    lateinit var img_visibilty_confirm: ImageView
    lateinit var ed_username: EditText
    lateinit var ed_cafeteria: EditText
    lateinit var ed_phone_number: EditText
    lateinit var ed_password: EditText
    lateinit var ed_password_confirm: EditText
    lateinit var ed_location: EditText
    lateinit var tv_expit_date:TextView
    lateinit var registeration_btn:Button
    lateinit var localSession: LocalSession
    lateinit var mContext: Context

    lateinit var lang:String
    var Tag = "reg"
    lateinit var onDateSetListener: OnDateSetListener

    private val Tag_failure = "failure"
    private val TAG_server = "TAG_server"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafeterias_registeration)

        //Action Bar
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.actionbar_cafeteria_registeration)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        img_visibiltyoff = findViewById(R.id.visibiltyoff)
        img_visibilty = findViewById(R.id.visibilty)
        img_visibiltyoff_confirm = findViewById(R.id.visibiltyoff_confirm)
        img_visibilty_confirm = findViewById(R.id.visibilty_confirm)
        ed_username = findViewById(R.id.ed_username)
        ed_cafeteria = findViewById(R.id.ed_cafeteria_restaurant)
        ed_phone_number = findViewById(R.id.ed_phone_number)
        ed_password = findViewById(R.id.ed_password)
        ed_password_confirm = findViewById(R.id.ed_password_confirm)
        ed_location = findViewById(R.id.ed_location)
        tv_expit_date = findViewById(R.id.date)
        registeration_btn = findViewById(R.id.cafeteria_registeration_btn)


        mContext = this

        localSession = LocalSession()
        localSession.LocalSession(mContext)
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


        getBirthDay()

        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        ed_location.setOnClickListener(View.OnClickListener { view ->
            if (view.id == ed_location.getId()) {
                ed_location.setCursorVisible(true)
            }
        })
        ed_location.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, event ->
            ed_location.setCursorVisible(false)
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(ed_location.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
            }
            false
        })

        registeration_btn.setOnClickListener {
            val username: String = ed_username.text.toString().trim()
            val cafeteria: String = ed_cafeteria.text.toString().trim()
            val phone: String = ed_phone_number.text.toString().trim()
            val password = ed_password.text.toString().trim()
            val password_confirm: String = ed_password_confirm.text.toString().trim()
            val location:String  = ed_location.text.toString().trim()
            val expir_date:String = tv_expit_date.text.toString().trim()

            if (CheckFields(username, cafeteria, phone, password,password_confirm, location, expir_date)!!)
            {
                if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected)
                {
                    //<--Hidden Keyboard
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isAcceptingText)
                    {
                        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    }
                    CafeteriasRegisteration(username, cafeteria, phone, password, location, expir_date)
                }
                else
                {
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet), this@CafeteriasRegisteration)
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

        onDateSetListener = OnDateSetListener { view, year, month, dayOfMonth ->
             Log.e(Tag, "onDateSet : mm/dd/yyy:" + year + "/" + month + "/" + dayOfMonth)
             val date = "$year-${month + 1}-$dayOfMonth"
             tv_expit_date.setText(date)

        }
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
    fun Visibiltyoff_Password_confirm(view: View) {
        img_visibiltyoff_confirm.visibility= View.GONE
        img_visibilty_confirm.visibility= View.VISIBLE
        ed_password_confirm.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    }

    fun Visibilty_Password_confirm(view: View) {
        img_visibilty_confirm.visibility= View.GONE
        img_visibiltyoff_confirm.visibility= View.VISIBLE
        ed_password_confirm.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    // <-- Check Fields Function -->
    fun CheckFields(username: String, cafeteria: String, phone: String, password: String, password_confirm: String, location: String, expir_date: String): Boolean? {
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
        if (password.isEmpty()) {
            ed_password.error = getString(R.string.password_empty)
            ed_password.requestFocus()
            return false
        } else if (password.length < 8) {
            ed_password.error = getString(R.string.password_check)
            ed_password.requestFocus()
            return false
        }
        if (password_confirm.isEmpty()) {
            ed_password_confirm.error = getString(R.string.password_confirm_cr_empty)
            ed_password_confirm.requestFocus()
            return false
        } else if (password_confirm != password) {
            ed_password_confirm.error = getString(R.string.password_similarity)
            ed_password_confirm.requestFocus()
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
    fun CafeteriasRegisteration(username: String?, cafeteria: String?, phone: String?, password: String?, location: String?, expir_date: String?) {
        val loading= ProgressDialog.show(this, null, getString(R.string.wait), false, false)
        loading.setContentView(R.layout.progressbar)
        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.setCancelable(false)
        loading.setCanceledOnTouchOutside(false)


        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
        val call = requestInterface.CafeteriasRegisteration(username, cafeteria, phone, password, location, expir_date, "Bearer " + localSession.getToken())
        call!!.enqueue(object : Callback<CafeteriasRegisterationResponse?> {
            override fun onResponse(call: Call<CafeteriasRegisterationResponse?>, response: Response<CafeteriasRegisterationResponse?>) {
                if (response.isSuccessful())
                {
                    if (!response.body()!!.error)
                    {
                        ed_username.setText("")
                        ed_cafeteria.setText("")
                        ed_phone_number.setText("")
                        ed_password.setText("")
                        ed_password_confirm.setText("")
                        ed_location.setText("")
                        tv_expit_date.setText("")
                        loading.dismiss()
                        val builder = AlertDialog.Builder(mContext)
                        val layoutInflater = LayoutInflater.from(mContext)
                        val view: View = layoutInflater.inflate(R.layout.custom_successfully_registered, null)
                        val ok = view.findViewById<TextView>(R.id.successfully_registered)
                        val button = view.findViewById<TextView>(R.id.btn_done)
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
                        Utility.showAlertDialog(getString(R.string.error), response.body()!!.message_ar + " " + response.body()!!.message_en,mContext)
                    }
                }
                else
                {
                    loading.dismiss()
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), mContext)
                    Log.i(TAG_server, response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<CafeteriasRegisterationResponse?>, t: Throwable) {
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

