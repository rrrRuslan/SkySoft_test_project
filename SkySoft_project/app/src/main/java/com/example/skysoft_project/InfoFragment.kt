package com.example.skysoft_project

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skysoft_project.model.Device

class InfoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private var viewModel: InfoViewModel = MapsActivity.viewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_info, container, false)

        val finalList = mutableListOf<Device>()
        viewModel.atm.value?.let { it.devices }?.forEach { finalList.add(it) }
        viewModel.tso.value?.let { it.devices }?.forEach { finalList.add(it) }

        Log.i("FINAL LIST", "res list -> ${viewModel.atm.value?.devices}")

        val adapter = RecyclerViewAdapter(root.context,finalList as ArrayList<Device>)
        recyclerView = root.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(root.context)
        recyclerView.adapter = adapter

        return root
    }




}