package com.example.cafeteriasadmin.Ui.Adapter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.ConnectivityManager
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteriasadmin.Ui.Activity.EditUsersProfile
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.*
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.Ui.Activity.NotificationsView
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Utilty.Utility
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ViewUsers_Adapter(var list: MutableList<ViewUsersModel>, var context: Context) : RecyclerView.Adapter<ViewUsers_Adapter.ViewHolder>(),Filterable {
    var filterSearch: List<ViewUsersModel>
    init
    {
        filterSearch = ArrayList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.custom_view_users, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.Tv_username.setText(list!![position]!!.name)
        holder.Tv_phone_number.setText(list!![position]!!.phone)
        holder.Tv_cafetiare_name.setText(list!![position]!!.cafeteria)
        holder.Tv_location.setText(list!![position]!!.location)
        holder.Tv_expir_date.setText(list!![position]!!.expir_date)
        holder.Tv_date.setText(list!![position]!!.created_at)
        holder.connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        holder.localSession.LocalSession(context)


        // <-- Check Active Or NonActive To Users -->
        holder.aSwitch.setOnClickListener { view ->
            if (holder.connectivityManager.activeNetworkInfo != null && holder.connectivityManager.activeNetworkInfo.isConnected) {
                if (holder.aSwitch.isChecked)
                {
                    holder.Tv_status.text = "Active"
                } else if (!holder.aSwitch.isChecked)
                {
                    holder.Tv_status.text = "NonActive"
                }
                val loading = ProgressDialog.show(context, null, context.getString(R.string.wait), false, false)
                loading.setContentView(R.layout.progressbar)
                loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading.setCancelable(false)
                loading.setCanceledOnTouchOutside(false)

                val status = holder.Tv_status.text.toString()
                val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
                val call: Call<UserStatusResponse?>? = requestInterface.UserStatus(list!![position]!!.id, status, "Bearer " + holder.localSession.getToken())
                call!!.enqueue(object : Callback<UserStatusResponse?> {
                    override fun onResponse(call: Call<UserStatusResponse?>, response: Response<UserStatusResponse?>) {
                        if (response.isSuccessful())
                        {
                            if (holder.aSwitch.isChecked)
                            {
                                loading.dismiss()
                                holder.Tv_status.background = context.getDrawable(R.color.greann)
                                Snackbar.make(view, context.resources.getString(R.string.active_status_vu) + " " + list!![position]!!.name, Snackbar.LENGTH_SHORT).show()
                            }
                            else if (!holder.aSwitch.isChecked)
                            {
                                loading.dismiss()
                                holder.Tv_status.background = context.getDrawable(R.color.red)
                                Snackbar.make(view, context.resources.getString(R.string.nonactive_status_vu) + " " + list!![position]!!.name, Snackbar.LENGTH_SHORT).show()
                            }
                        } else
                        {
                            loading.dismiss()
                            Utility.showAlertDialog(context.getString(R.string.error), context.getString(
                                    R.string.servererror
                                ), context)
                        }
                    }

                    override fun onFailure(call: Call<UserStatusResponse?>, t: Throwable) {
                        loading.dismiss()
                        Utility.showAlertDialog(context.getString(R.string.error), context.getString(
                                R.string.connect_internet_slow
                            ), context)
                    }
                })
            } else {
                Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet), context)
                if (holder.aSwitch.isChecked) {
                    holder.aSwitch.isChecked = false
                } else if (!holder.aSwitch.isChecked) {
                    holder.aSwitch.isChecked = true
                }
            }
        }


        // <-- Delete MacAddrss  -->
        holder.Tv_delete_mac.setOnClickListener {
            if (holder.connectivityManager.activeNetworkInfo != null && holder.connectivityManager.activeNetworkInfo.isConnected) {
                val builder = AlertDialog.Builder(context)
                val view1: View = (context as Activity).layoutInflater.inflate(R.layout.deletemac_massage, null)
                val hidder = view1.findViewById<TextView>(R.id.tv_hidder)
                val btn_yes = view1.findViewById<Button>(R.id.btn_yes)
                val btn_no = view1.findViewById<Button>(R.id.btn_no)
                builder.setView(view1)
                hidder.text = context.resources.getString(R.string.remove_mac_massage) + " " + list!![position]!!.name
                val dialog = builder.create()
                btn_yes.setOnClickListener(
                    {
                        dialog.dismiss()
                        val loading = ProgressDialog.show(context, null, context.getString(R.string.wait), false, false)
                        loading.setContentView(R.layout.progressbar)
                        loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        loading.setCancelable(false)
                        loading.setCanceledOnTouchOutside(false)

                        val requestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
                        val call: Call<DeleteMacResponse?>? = requestInterface.DeleteMac(list!![position]!!.id, "Bearer " + holder.localSession.getToken())
                        call!!.enqueue(object : Callback<DeleteMacResponse?> {
                            override fun onResponse(call: Call<DeleteMacResponse?>, response: Response<DeleteMacResponse?>) {
                                if (response.isSuccessful())
                                {
                                    if(!response.body()!!.error)
                                    {
                                        loading.dismiss()
                                        Toast.makeText(context, context.resources.getString(R.string.Done_remove_mac_massage) + " " + list!![position]!!.name, Toast.LENGTH_SHORT).show()
                                    }
                                    else
                                    {
                                        loading.dismiss()
                                        Utility.showAlertDialog(context.getString(R.string.error), response.body()!!.message_ar + " " + response.body()!!.message_en, context)
                                    }
                                }
                                else
                                {
                                    loading.dismiss()
                                    Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.servererror), context)
                                }
                            }

                            override fun onFailure(call: Call<DeleteMacResponse?>, t: Throwable) {
                                loading.dismiss()
                                Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet_slow), context)
                            }
                        })
                    })

                btn_no.setOnClickListener { v: View? -> dialog.dismiss() }
                dialog.show()

            } else {
                Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet), context)
            }
        }


        //<--edit profile-->
        holder.Tv_edit_profile.setOnClickListener {
            val intent = Intent(context, EditUsersProfile::class.java)
            intent.putExtra("id",list.get(position).id)
            intent.putExtra("name",list.get(position).name)
            intent.putExtra("cafeteria",list.get(position).cafeteria)
            intent.putExtra("phone",list.get(position).phone)
            intent.putExtra("location",list.get(position).location)
            intent.putExtra("expir_date",list.get(position).expir_date)
            context.startActivity(intent)
        }


        //<--edit password-->
        holder.Tv_edit_password.setOnClickListener {
            var ed_password: EditText? = null
            var img_cancle: ImageView? = null
            var edit_btn: Button? = null
            var password: String? = null
            lateinit var img_visibiltyoff: ImageView
            lateinit var img_visibilty: ImageView

            val builder = AlertDialog.Builder(context)
            val view1: View = (context as Activity).layoutInflater.inflate(
                R.layout.custom_edit_password,
                null
            )

            ed_password = view1.findViewById(R.id.ed_password)
            img_visibiltyoff = view1.findViewById(R.id.visibiltyoff)
            img_visibilty = view1.findViewById(R.id.visibilty)
            edit_btn = view1.findViewById(R.id.edit_password_btn)
            img_cancle = view1.findViewById(R.id.cancle)


            //<---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
            ed_password.setOnClickListener(View.OnClickListener { view ->
                if (view.id == ed_password.getId()) {
                    ed_password.setCursorVisible(true)
                }
            })
            ed_password.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, event ->
                ed_password.setCursorVisible(false)
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    val inputMethodManager =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(
                        ed_password!!.getApplicationWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
                false
            })
            img_visibiltyoff.setOnClickListener {
                img_visibiltyoff.visibility= View.GONE
                img_visibilty.visibility= View.VISIBLE
                ed_password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }

            img_visibilty.setOnClickListener {
                img_visibilty.visibility= View.GONE
                img_visibiltyoff.visibility= View.VISIBLE
                ed_password.transformationMethod = PasswordTransformationMethod.getInstance()
            }


            builder.setView(view1)
            val dialog = builder.create()
            val insetDrawable = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20)
            dialog.window!!.setBackgroundDrawable(insetDrawable)


            // <-- cancle dialog -->
            img_cancle.setOnClickListener(View.OnClickListener { dialog.dismiss()
            })

            // <-- Check Fields Function -->
            fun CheckFields(password: String): Boolean? {
                if (password.isEmpty()) {
                    ed_password.error = context.getString(R.string.password_empty)
                    ed_password.requestFocus()
                    return false
                } else if (password.length < 8) {
                    ed_password.error = context.getString(R.string.password_check)
                    ed_password.requestFocus()
                    return false
                }
                return true
            }

            // <-- Send Data TO request And Git Response Status
            fun EditPassword(password: String?) {
                val loading = ProgressDialog.show(context, null, context.getString(R.string.wait), false, false)
                loading.setContentView(R.layout.progressbar)
                loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading.setCancelable(false)
                loading.setCanceledOnTouchOutside(false)


                // <-- Connect WIth Network And Check Response Successful or Failure -- >
                val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
                val call: Call<EditProfileResponse?>?= requestInterface.EditUsersPassword(list!!.get(position)!!.id, password,  "Bearer " + holder.localSession.getToken())
                call!!.enqueue(object : Callback<EditProfileResponse?> {
                    override fun onResponse(call: Call<EditProfileResponse?>, response: Response<EditProfileResponse?>) {
                        if (response.isSuccessful())
                        {
                            if (!response.body()!!.error)
                            {
                                Toast.makeText(context, context.resources.getString(R.string.edit_successfully), Toast.LENGTH_SHORT).show()
                                loading.dismiss()
                            }
                            else
                            {
                                loading.dismiss()
                                Utility.showAlertDialog(context.getString(R.string.error), response.body()!!.message_ar + " " + response.body()!!.message_en, context)
                            }
                        } else {
                            loading.dismiss()
                            Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.servererror), context)

                        }
                    }

                    override fun onFailure(call: Call<EditProfileResponse?>, t: Throwable) {
                        loading.dismiss()
                        Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet_slow), context)
                    }
                })
            }

            edit_btn.setOnClickListener {
                password = ed_password.getText().toString().trim()
                if (CheckFields(password!!)!!)
                {
                    if (holder.connectivityManager.activeNetworkInfo != null && holder.connectivityManager.activeNetworkInfo.isConnected)
                    {
                        dialog.dismiss()
                        //<--Hidden Keyboard
                        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        if (inputMethodManager.isAcceptingText)
                        {
                            inputMethodManager.hideSoftInputFromWindow((context as NotificationsView).getCurrentFocus()!!.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
                        }
                        EditPassword(password)
                    }
                    else
                    {
                        Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet), context)
                    }
                }

            }

            dialog.show()
        }


        // <-- Save Switch State -->
        if (list!![position]!!.status.equals("Active")) {
            holder.aSwitch.isChecked = true
            holder.Tv_status.background = context.getDrawable(R.color.greann)
        } else {
            holder.aSwitch.isChecked = false
            holder.Tv_status.background = context.getDrawable(R.color.red)
        }
    }


    override fun getItemCount(): Int {
        return list!!.size
    }

    // <-- Search -->
    override fun getFilter(): Filter? {
        return filterr
    }

    var filterr: Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val key = charSequence.toString()
            val viewUsersModel: MutableList<ViewUsersModel> = ArrayList<ViewUsersModel>()
            if (key.isEmpty() || key.length == 0)
            {
                viewUsersModel.addAll(filterSearch)
            }
            else
            {
                for (item in list!!)
                {
                    if (item!!.name!!.toLowerCase().contains(key) || item!!.name!!.toUpperCase().contains(key) || item!!.cafeteria!!.toLowerCase().contains(key)
                        || item!!.cafeteria!!.toLowerCase().contains(key)|| item!!.phone!!.toLowerCase().contains(key))
                    {
                        viewUsersModel.add(item)
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = viewUsersModel
            return filterResults
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults?) {
            list.clear()
            list.addAll(filterResults!!.values as Collection<ViewUsersModel>)
            notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var Tv_username: TextView
        var Tv_phone_number: TextView
        var Tv_cafetiare_name: TextView
        var Tv_location: TextView
        var Tv_expir_date: TextView
        var Tv_date: TextView
        var Tv_status: TextView
        var Tv_delete_mac: TextView
        var Tv_edit_profile: TextView
        var aSwitch: Switch
        var Tv_edit_password: TextView
        lateinit var connectivityManager: ConnectivityManager
        var localSession: LocalSession

        init {
            Tv_username = view.findViewById(R.id.username)
            Tv_phone_number = view.findViewById(R.id.phone_number)
            Tv_cafetiare_name = view.findViewById(R.id.cafeteria_name)
            Tv_location = view.findViewById(R.id.location)
            Tv_expir_date = view.findViewById(R.id.expir_date)
            Tv_date = view.findViewById(R.id.addeddate)
            Tv_status = view.findViewById(R.id.status)
            Tv_delete_mac = view.findViewById(R.id.delete_mac)
            Tv_edit_profile = view.findViewById(R.id.edituserprofile)
            Tv_edit_password = view.findViewById(R.id.editpassword)
            aSwitch = view.findViewById(R.id.swithc)
            localSession = LocalSession()

        }

    }
}








