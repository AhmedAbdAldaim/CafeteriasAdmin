package com.example.cafeteriasadmin.Ui.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.EditProfileResponse
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Utilty.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfile : AppCompatActivity() {
    lateinit var ed_username: EditText
    lateinit var ed_phone_number:EditText
    lateinit var ed_password:EditText
    lateinit var edit_btn: Button
    lateinit var localSession: LocalSession
    lateinit var mContext:Context
    private val TAG_server = "Server"
    private val Tag_failure = "failure"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        //Action Bar
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.actionbar_edit_profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mContext = this
        localSession = LocalSession()
        localSession.LocalSession(this)
        ed_username = findViewById(R.id.ed_username)
        ed_phone_number = findViewById(R.id.ed_phone_number)
        ed_password = findViewById(R.id.ed_password)


        edit_btn = findViewById<Button>(R.id.edit_btn)
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        ed_username.setText(localSession.getName())
        ed_phone_number.setText(localSession.getPhone())
        ed_password.setText(localSession.getPassword())


        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        ed_phone_number.setOnClickListener(View.OnClickListener { view ->
            if (view.id == ed_phone_number.getId())
            {
                ed_phone_number.setCursorVisible(true)
            }
        })
        ed_phone_number.setOnEditorActionListener(OnEditorActionListener { textView, i, event ->
            ed_phone_number.setCursorVisible(false)
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(ed_phone_number.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
            }
            false
        })



        // <-- Onclick Update Button -->
        edit_btn.setOnClickListener(View.OnClickListener {
            val name: String = ed_username.getText().toString().trim()
            val phone: String = ed_phone_number.getText().toString().trim()

            if (Valided(name, phone))
            {
                if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected)
                {
                    //<--Hidden Keyboard
                    val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isAcceptingText)
                    {
                        inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    }
                    EditProfile(name, phone, ed_password.getText().toString())

                }
                else
                {
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.connect_internet), this)
                }
            }
        })
    }


    //<--   Check Fields Function -->
    fun Valided(username: String, phone_number: String): Boolean {
        if (username.isEmpty()) {
            ed_username.setError(resources.getString(R.string.username_edit_profile_empty))
            ed_username.requestFocus()
            return false
        } else if (username.length < 4) {
            ed_username.setError(getString(R.string.username_edit_profile_empty))
            ed_username.requestFocus()
            return false
        } else if (username.length > 40) {
            ed_username.setError(resources.getString(R.string.check_username_length_edit_profile))
            ed_username.requestFocus()
            return false
        }
        if (phone_number.isEmpty()) {
            ed_phone_number.setError(resources.getString(R.string.phone_number_edit_profile_empty))
            ed_phone_number.requestFocus()
            return false
        } else if (!phone_number.matches(Regex ("[0-9]{10}"))) {
            ed_phone_number.setError(getString(R.string.phone_number_edit_profile_valid))
            ed_phone_number.requestFocus()
            return false
        }
        return true
    }


    // <--  Send Data TO request And Git Response Status -->
    private fun EditProfile(username: String, phone_numer: String, password: String) {
        val loading = ProgressDialog.show(this, null, getString(R.string.wait), false, false)
        loading.setContentView(R.layout.progressbar)
        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.setCancelable(false)
        loading.setCanceledOnTouchOutside(false)

        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
        val call: Call<EditProfileResponse?>? = requestInterface.EditProfile(username, phone_numer, password, "Bearer " + localSession.getToken())
        call!!.enqueue(object : Callback<EditProfileResponse?> {
            override fun onResponse(call: Call<EditProfileResponse?>,response: Response<EditProfileResponse?>) {
                if (response.isSuccessful())
                {
                    if (!response.body()!!.error)
                    {
                        localSession.createSession(
                            localSession.getToken(),
                            response.body()!!.editProfileModel!!.id,
                            response.body()!!.editProfileModel!!.name,
                            response.body()!!.editProfileModel!!.phone,
                            response.body()!!.editProfileModel!!.cafeteria,
                            response.body()!!.editProfileModel!!.role,
                            response.body()!!.editProfileModel!!.status,
                            response.body()!!.editProfileModel!!.mac_address,
                            ed_password.text.toString().trim())

                        Toast.makeText(mContext, R.string.edit_successfully, Toast.LENGTH_SHORT).show()
                        val intent = Intent(mContext, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
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
                    Log.i(TAG_server, response.errorBody().toString())
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), mContext)
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
