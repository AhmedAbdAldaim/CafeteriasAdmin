package com.example.cafeteriasadmin.Ui.Adapter

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.ConnectivityManager
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteriasadmin.LocalDB.LocalSession
import com.example.cafeteriasadmin.Model.AddNotificationModel
import com.example.cafeteriasadmin.Model.AddNotificationResponse
import com.example.cafeteriasadmin.Model.DeleteNotificationResponse
import com.example.cafeteriasadmin.Network.ApiClient
import com.example.cafeteriasadmin.Network.RequestInterface
import com.example.cafeteriasadmin.Ui.Activity.NotificationsView
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Utilty.Utility
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsView_Adapter(var list: MutableList<AddNotificationModel>, var context: Context)
    : RecyclerView.Adapter<NotificationsView_Adapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.custom_notifcations_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.TV_name.setText(list!![position]!!.name)
        holder.Tv_notification.setText(list!![position]!!.notification)
        holder!!.connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        holder.localSession.LocalSession(context)


        holder.edit_cardviwe.setOnClickListener {
            var ed_name: EditText? = null
            var ed_notitfaction: EditText? = null
            var img_cancle: ImageView? = null
            var edit_btn: Button? = null
            var name: String? = null
            var notitfaction: String? = null

            val builder = AlertDialog.Builder(context)
            val view1: View = (context as Activity).layoutInflater.inflate(R.layout.custom_edit_notification, null)

            ed_name = view1.findViewById(R.id.ed_title)
            ed_notitfaction = view1.findViewById(R.id.ed_noitifcation)
            edit_btn = view1.findViewById(R.id.edit_notification_btn)
            img_cancle = view1.findViewById(R.id.cancle)

            ed_name.setText(list[position].name)
            ed_notitfaction.setText(list[position].notification)


            //<---EditText Hidden EditText Cursor When OnClick Done On Keyboard-->
            ed_notitfaction.setOnClickListener(View.OnClickListener { view ->
                if (view.id == ed_notitfaction.getId()) {
                    ed_notitfaction.setCursorVisible(true)
                }
            })
            ed_notitfaction.setOnEditorActionListener(OnEditorActionListener { textView, i, event ->
                ed_notitfaction.setCursorVisible(false)
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(ed_notitfaction!!.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }
                false })


            builder.setView(view1)
            val dialog = builder.create()
            val insetDrawable = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20)
            dialog.window!!.setBackgroundDrawable(insetDrawable)


            // <-- cancle dialog -->
            img_cancle.setOnClickListener(View.OnClickListener { dialog.dismiss()
            })

            // <-- Check Fields Function -->
            fun CheckFields(name: String, notification: String): Boolean? {
                if (name.isEmpty()) {
                    ed_name.error = context.getString(R.string.title_empty)
                    ed_name.requestFocus()
                    return false
                }
                if (notification.isEmpty()) {
                    ed_notitfaction.error = context.getString(R.string.notification_empty)
                    ed_notitfaction.requestFocus()
                    return false
                }
                return true
            }

            // <-- Send Data TO request And Git Response Status
            fun EditNotification(name: String?, notification: String?) {
                val loading = ProgressDialog.show(context, null, context.getString(R.string.wait), false, false)
                loading.setContentView(R.layout.progressbar)
                loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loading.setCancelable(false)
                loading.setCanceledOnTouchOutside(false)


                // <-- Connect WIth Network And Check Response Successful or Failure -- >
                val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
                val call: Call<AddNotificationResponse?>?= requestInterface.EditNotification(list!!.get(position)!!.id, name, notification, "Bearer " + holder.localSession.getToken())
                call!!.enqueue(object : Callback<AddNotificationResponse?> {
                    override fun onResponse(call: Call<AddNotificationResponse?>, response: Response<AddNotificationResponse?>) {
                        if (response.isSuccessful())
                        {
                            if (!response.body()!!.error)
                            {
                                loading.dismiss()
                                Toast.makeText(context, context.resources.getString(R.string.edit_notification_successfully), Toast.LENGTH_SHORT).show()
                                (context as NotificationsView).getAllNotifications()
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

                    override fun onFailure(call: Call<AddNotificationResponse?>, t: Throwable) {
                        loading.dismiss()
                        Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet_slow), context)
                    }
                })
            }

            edit_btn.setOnClickListener {
                name = ed_name.getText().toString().trim()
                notitfaction = ed_notitfaction.getText().toString().trim()
                if (CheckFields(name!!, notitfaction!!)!!)
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
                        EditNotification(name, notitfaction)
                    }
                    else
                    {
                       Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet), context)
                    }
                }

            }
            dialog.show()
        }

        holder.delete_cardview.setOnClickListener {
            if (holder.connectivityManager.activeNetworkInfo != null && holder.connectivityManager.activeNetworkInfo.isConnected) {
                val builder = AlertDialog.Builder(context)
                val view1: View = (context as Activity).layoutInflater.inflate(R.layout.logout_massage, null)
                val item_delete_massage = view1.findViewById<TextView>(R.id.tv_Massage)
                val confirm_tv:TextView = view1.findViewById(R.id.confirm_tv)
                val cancle_tv:TextView = view1.findViewById(R.id.cancle_tv)

                item_delete_massage.text = context.resources.getString(R.string.remove_notification) + " " + list!![position]!!.name
                builder.setView(view1)
                val dialog = builder.create()
                val insetDrawable = InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20)
                dialog.window!!.setBackgroundDrawable(insetDrawable)
                cancle_tv.setOnClickListener { dialog.dismiss() }
                confirm_tv.setOnClickListener { v: View? ->
                    dialog.dismiss()
                    val loading = ProgressDialog.show(context, null, context.getString(R.string.wait), false, false)
                    loading.setContentView(R.layout.progressbar)
                    loading.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    loading.setCancelable(false)
                    loading.setCanceledOnTouchOutside(false)
                    val requestInterface: RequestInterface = ApiClient.getClient(ApiClient.BASE_URL)!!.create(RequestInterface::class.java)
                    val call: Call<DeleteNotificationResponse?>? = requestInterface.DeleteNotification(list!![position]!!.id, "Bearer " + holder.localSession.getToken())
                    call!!.enqueue(object : Callback<DeleteNotificationResponse?> {
                        override fun onResponse(call: Call<DeleteNotificationResponse?>, response: Response<DeleteNotificationResponse?>) {
                            if (response.isSuccessful())
                            {
                                if (!response.body()!!.error)
                                {
                                    Toast.makeText(context, context.resources.getString(R.string.Done_remove_notification) + "", Toast.LENGTH_SHORT).show()
                                    (context as NotificationsView).getAllNotifications()
                                    loading.dismiss()
                                }
                                else
                                {
                                    loading.dismiss()
                                    Utility.showAlertDialog(context.getString(R.string.error), response.body()!!.message_ar + " " + response.body()!!.message_en,context)
                                }
                            }
                            else
                            {
                                loading.dismiss()
                                Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.servererror), context)
                            }
                        }

                        override fun onFailure(call: Call<DeleteNotificationResponse?>, t: Throwable) {
                            loading.dismiss()
                            Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet_slow), context)
                        }
                    })
                }
                cancle_tv.setOnClickListener { v: View? -> dialog.dismiss() }
                dialog.show()
            }
            else
            {
                Utility.showAlertDialog(context.getString(R.string.error), context.getString(R.string.connect_internet), context)
            }
        }
    }


    override fun getItemCount(): Int {
        return list!!.size
    }



    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var TV_name: TextView
        var Tv_notification: TextView
        var delete_cardview: CardView
        var edit_cardviwe:CardView
        lateinit var connectivityManager: ConnectivityManager
        var localSession: LocalSession

        init {
            TV_name = view.findViewById(R.id.title)
            Tv_notification = view.findViewById(R.id.notification)
            edit_cardviwe = itemView.findViewById(R.id.edit_cardview)
            delete_cardview = itemView.findViewById(R.id.delete_cardview)
            localSession = LocalSession()
        }

    }




}








