package com.example.cafeteriasadmin.Ui.Adapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.cafeteriasadmin.Model.ListviewSettingModel
import com.example.cafeteriasadmin.R

class ListviewSetting_Adapter(context: Context, var resource: Int, var objects: ArrayList<ListviewSettingModel>)
    : ArrayAdapter<ListviewSettingModel>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(context)
        val view: View = layoutInflater.inflate(resource, parent, false)
        val textView = view.findViewById<TextView>(R.id.tilte)
        val imageView = view.findViewById<ImageView>(R.id.image)
        val image_arrow =view.findViewById<ImageView>(R.id.image1)
        val sharedPreferences = context.getSharedPreferences("langdb", MODE_PRIVATE)
        var lang = sharedPreferences.getString("lang", "ar")

        if(lang=="ar")
        {
         image_arrow.setImageResource(R.drawable.ic_arrow)
        }
        else if(lang=="en")
        {
        image_arrow.setImageResource(R.drawable.ic_arrow_left)        }
        textView.text = getItem(position)!!.title
        imageView.setImageResource(getItem(position)!!.image)
        return view
    }


}