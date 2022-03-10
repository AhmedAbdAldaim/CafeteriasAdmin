package com.example.cafeteriasadmin.Ui.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeteriasadmin.*
import com.example.cafeteriasadmin.Model.MainModel
import com.example.cafeteriasadmin.Ui.Activity.*

class Main_Adapter(var mainmodel: List<MainModel?>, var context: Context): RecyclerView.Adapter<Main_Adapter.ViewHolder>()
    {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.custom_main, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.Title.setText(mainmodel!![position]!!.name)
            holder.imageView.setImageResource(mainmodel!![position]!!.img)
            holder.cardView.setOnClickListener {
                when(position){
                    0 -> {
                      val intent = Intent(context, CafeteriasRegisteration::class.java)
                        context.startActivity(intent)
                    }
                    1 ->  {
                        val intent = Intent(context, ViewUsers::class.java)
                        context.startActivity(intent)
                    }
                    2 -> {
                        val intent = Intent(context, Settings::class.java)
                        context.startActivity(intent)
                    }
                    3 -> {
                        val intent = Intent(context, Notifications::class.java)
                        context.startActivity(intent)
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return mainmodel!!.size
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            var Title: TextView
            var imageView: ImageView
            var cardView: CardView


            init {
                Title = view.findViewById(R.id.name)
                imageView = view.findViewById(R.id.image)
                cardView = view.findViewById(R.id.card)
            }
        }

    }


