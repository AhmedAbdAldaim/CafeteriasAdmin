package com.example.cafeteriasadmin.Ui.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.ViewUsersModel
import com.example.cafeteriasadmin.Model.ViewUsersResponse
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Ui.Adapter.ViewUsers_Adapter
import com.example.cafeteriasadmin.Utilty.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewUsers : AppCompatActivity() {
    lateinit var edsearh: EditText
    lateinit  var Tv_Users_total: TextView
    lateinit  var tv_empty:TextView
    lateinit var tv_connect:TextView
    lateinit var button_connect: Button
    lateinit var recyclerView: RecyclerView
    lateinit var localSession: LocalSession
    lateinit var mContext: Context
    lateinit var viewusersAdapter:ViewUsers_Adapter

    private val TAG_server = "Server"
    private val Tag_failure = "failure"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_users)

        //Action Bar
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.actionbar_view_users)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        tv_connect = findViewById(R.id.connection)
        button_connect = findViewById(R.id.btnconnection)
        tv_empty = findViewById(R.id.empty)
        edsearh = findViewById(R.id.search)
        Tv_Users_total = findViewById(R.id.total)
        recyclerView = findViewById(R.id.rectable)

        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val linearLayoutManager = GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager

        mContext = this

        localSession = LocalSession()
        localSession.LocalSession(mContext)


        //       <-- SEARCH -->
        edsearh.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                try {
                    viewusersAdapter.getFilter()!!.filter(charSequence)
                } catch (e: Exception) {
                }
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                try {
                    viewusersAdapter.getFilter()!!.filter(charSequence)
                } catch (e: Exception) {
                }
            }

            override fun afterTextChanged(editable: Editable) {
            }
        })


        //      <---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
        edsearh.setOnClickListener { view ->
            if (view.id == edsearh.id) {
                edsearh.isCursorVisible = true
            }
        }
        edsearh.setOnEditorActionListener { textView, i, event ->
            edsearh.isCursorVisible = false
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(edsearh.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
            false
        }



        if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo.isConnected) {
            getUserShow()
        } else {
            tv_connect.setText(R.string.connect_internet)
            tv_connect.visibility = View.VISIBLE
            button_connect.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
            tv_empty.visibility = View.INVISIBLE
            button_connect.setOnClickListener {
                tv_connect.visibility = View.INVISIBLE
                button_connect.visibility = View.INVISIBLE
                recyclerView.visibility = View.VISIBLE
                getUserShow()
            }
        }
    }


    //<--   Git All Useres -->
     fun getUserShow() {
        val loading = ProgressDialog.show(this, null, getString(R.string.wait), false, false)
        loading.setContentView(R.layout.progressbar)
        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loading.setCancelable(false)
        loading.setCanceledOnTouchOutside(false)

        // <-- Connect WIth Network And Check Response Successful or Failure -- >
        val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
        val call: Call<ViewUsersResponse?>? = requestInterface.GetAllCafeterias("Bearer " + localSession.getToken())
        call!!.enqueue(object : Callback<ViewUsersResponse?> {
            override fun onResponse(call: Call<ViewUsersResponse?>, response: Response<ViewUsersResponse?>) {
                if (response.isSuccessful())
                {
                    if (!response.body()!!.error)
                    {
                    viewusersAdapter = ViewUsers_Adapter(response.body()!!.viewUsersModel!! as MutableList<ViewUsersModel>, mContext)
                    if (viewusersAdapter.getItemCount() === 0) {
                        loading.dismiss()
                        tv_empty.visibility = View.VISIBLE
                        recyclerView.visibility = View.INVISIBLE
                        tv_empty.setText(R.string.empty_vu)
                        Tv_Users_total.text = 0.toString() + ""
                        return
                    } else if (viewusersAdapter.getItemCount() > 0) {
                        loading.dismiss()
                        viewusersAdapter.notifyDataSetChanged()
                        tv_empty.visibility = View.INVISIBLE
                        Tv_Users_total.setText(viewusersAdapter.getItemCount().toString() + "")
                        viewusersAdapter.notifyDataSetChanged()
                        recyclerView.adapter = viewusersAdapter
                    }
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
                    Utility.showAlertDialog(getString(R.string.error), getString(R.string.servererror), this@ViewUsers)
                }
            }

            override fun onFailure(call: Call<ViewUsersResponse?>, t: Throwable) {
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
                    getUserShow()
                }
            }
        })
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}

