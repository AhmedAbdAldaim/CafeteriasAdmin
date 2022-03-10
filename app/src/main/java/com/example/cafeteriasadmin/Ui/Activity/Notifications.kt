package com.example.cafeteriasadmin.Ui.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteriasadmin.Model.NotiifcationsModel
import com.example.cafeteriasadmin.R
import com.example.cafeteriasadmin.Ui.Adapter.Notifications_Adapter
import java.util.ArrayList

class Notifications : AppCompatActivity() {
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: Notifications_Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        //Action Bar
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setCustomView(R.layout.actionbar_notifications)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.rec)

        val linearLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager

        val arrayList: ArrayList<NotiifcationsModel> = ArrayList<NotiifcationsModel>()
        arrayList.add(NotiifcationsModel(getString(R.string.actionbar_add_notification), R.drawable.add_notification))
        arrayList.add(NotiifcationsModel(getString(R.string.actionbar_view_notification), R.drawable.view_notification))

        adapter = Notifications_Adapter(arrayList, this)
        recyclerView.adapter = adapter
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
