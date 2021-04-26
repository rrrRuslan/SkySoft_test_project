package com.example.skysoft_project

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skysoft_project.model.Device

class RecyclerViewAdapter(private var context: Context,
                          private var list: ArrayList<Device>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private inner class InfoViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var type: TextView = itemView.findViewById(R.id.type_container)
        var adress: TextView = itemView.findViewById(R.id.fullAdress_container)

        fun bind(position: Int) {
            val recyclerViewModel = list[position]
            type.text = recyclerViewModel.type
            adress.text = recyclerViewModel.fullAddressUa
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return InfoViewHolder(
            LayoutInflater.from(context).inflate(R.layout.object_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as InfoViewHolder).bind(position)
    }

    override fun getItemCount(): Int = list.size



}