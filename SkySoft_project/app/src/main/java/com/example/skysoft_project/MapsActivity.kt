package com.example.skysoft_project

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.skysoft_project.`interface`.RetrofitServices
import com.example.skysoft_project.controller.Controller
import com.example.skysoft_project.model.ATM
import com.example.skysoft_project.model.Device
import com.example.skysoft_project.model.Terminal
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.database.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mService: RetrofitServices = Controller.retrofitService
    private var atm: ATM = ATM(arrayListOf(), "", "")
    private var tso: Terminal = Terminal(arrayListOf(), "", "")

    private var myLocation: Location? = Location("")

    private var destinationLocation: LatLng? = null
    private val LOCATION_REQUEST_CODE = 23
    private var locationPermission = false
    private var opts: PolylineOptions = PolylineOptions()

    private var infoButtonSwitcher = false
    private lateinit var infoButton: Button
    private lateinit var editText_city: EditText

    private lateinit var database: FirebaseDatabase
    private lateinit var atmReference: DatabaseReference
    private lateinit var tsoReference: DatabaseReference
    private lateinit var rootDb: DatabaseReference


    companion object {
        lateinit var viewModel: InfoViewModel
    }

    val atmsFromFirebase = mutableListOf<Device>()
    val terminalsFromFirebase = mutableListOf<Device>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        requestPermission()

        database = FirebaseDatabase.getInstance("https://skysoft-project-default-rtdb.firebaseio.com/")
        rootDb = database.reference
        atmReference = database.getReference("atms")
        tsoReference = database.getReference("terminals")

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = ViewModelProvider(this).get(InfoViewModel::class.java)
        editText_city = findViewById(R.id.editText_city)

        findViewById<Button>(R.id.searchButton).setOnClickListener {

            getObjectsFromFirebase(editText_city.text.toString())
            getTSOList(editText_city.text.toString())
            getATMList(editText_city.text.toString())
        }

        findViewById<Button>(R.id.draw_button).setOnClickListener {
            destinationLocation?.let { it1 -> drawRoute(it1) }
        }

        infoButton = findViewById(R.id.showInfo_button)
        val infoFragment = InfoFragment()
        infoButton.setOnClickListener {
            if (!infoButtonSwitcher) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.infoFragm_container, infoFragment).commit()
                infoButtonSwitcher = !infoButtonSwitcher
                infoButton.text = "CLOSE INFO"
            } else {
                supportFragmentManager.beginTransaction().remove(infoFragment).commit()
                infoButtonSwitcher = !infoButtonSwitcher
                infoButton.text = "SHOW INFO"
            }
        }
    }

    private fun getObjectsFromFirebase(city: String):List<Device> {
        rootDb.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChild("${city}_atms")) {
                    for (deviceSnapshot in dataSnapshot.child("${city}_atms").children) {
                        deviceSnapshot.getValue(Device::class.java)?.let { atmsFromFirebase.add(it) }
                    }
                }
                if (dataSnapshot.hasChild("${city}_terminals")) {
                    for (deviceSnapshot in dataSnapshot.child("${city}_terminals").children) {
                        deviceSnapshot.getValue(Device::class.java)?.let { terminalsFromFirebase.add(it) }
                    }
                }
                Log.i("ATM FROM FIREBASE", atmsFromFirebase.toString())
                Log.i("TSO FROM FIREBASE", terminalsFromFirebase.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText( applicationContext,"Error while fetching data from DB",Toast.LENGTH_LONG).show()
            }
        })

        return atmsFromFirebase
    }


    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),LOCATION_REQUEST_CODE)
        } else {
            locationPermission = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermission = true
                    getMyLocation()
                } else {
                    // permission denied
                }
                return
            }
        }
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationChangeListener { location ->
            myLocation = location
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (locationPermission) {
            getMyLocation()
        }
        mMap.setOnMarkerClickListener { marker ->
            if (marker != null) {
                destinationLocation = marker.position
            }
            false
        }
    }

    private fun getATMList(city: String) {

        mService.getATMs(city).enqueue(object : Callback<ATM> {
            override fun onFailure(call: Call<ATM>, t: Throwable) {
                Toast.makeText(applicationContext, "Failed to load ATMs", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ATM>,
                response: Response<ATM>
            ) {

                atm = response.body() as ATM
                viewModel.atm.value = atm
                atmReference = database.getReference("${city}_atms")
                atmReference.setValue(atm.devices)//todo
                createATMsMarkers(atm.devices)
            }
        })
    }

    private fun getTSOList(city: String) {

        mService.getTSOs(city).enqueue(object : Callback<Terminal> {
            override fun onFailure(call: Call<Terminal>, t: Throwable) {
                Toast.makeText(applicationContext, "Failed to load terminals", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<Terminal>,
                response: Response<Terminal>
            ) {
                tso = response.body() as Terminal
                viewModel.tso.value = tso
                tsoReference = database.getReference("${city}_terminals")
                tsoReference.setValue(tso.devices)//todo

                createTSOsMarkers(tso.devices)
            }
        })
    }

    private fun createATMsMarkers(atms: List<Device>) {
        for (item in atms) {
            mMap.addMarker(
                MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(90F))
                    .position(LatLng(item.longitude, item.latitude))
                    .title("${item.type} , ${item.placeUa}")
            )
        }
    }

    private fun createTSOsMarkers(tsos: List<Device>) {
        for (item in tsos) {
            mMap.addMarker(
                MarkerOptions().position(LatLng(item.longitude, item.latitude))
                    .title("${item.type} , ${item.placeUa}")
            )
        }
    }

    private fun drawRoute(destination: LatLng) {

        val path = arrayListOf<LatLng>()

        val context = GeoApiContext.Builder()
            .apiKey("AIzaSyB8mLboaBl-TpLaiun6NG-ChbYioDsJto4")
            .build()
        val req = DirectionsApi.getDirections(
            context,
            "${myLocation?.latitude},${myLocation?.longitude}",
            "${destination.latitude},${destination.longitude}"
        )
        try {
            val res: DirectionsResult = req.await()

            if (res.routes != null && res.routes.isNotEmpty()) {
                val route = res.routes[0]
                if (route.legs != null) {
                    for (element in route.legs) {
                        val leg = element
                        if (leg.steps != null) {
                            for (element1 in leg.steps) {
                                val step = element1
                                if (step.steps != null && step.steps.isNotEmpty()) {
                                    for (element2 in step.steps) {
                                        val step1 = element2
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            val coords1 = points1.decodePath()
                                            for (coord1 in coords1) {
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        val coords = points.decodePath()
                                        for (coord in coords) {
                                            path.add(LatLng(coord.lat, coord.lng))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e("EXCEPTION", ex.localizedMessage!!)
        }
        if (path.size > 0) {
            opts = PolylineOptions().addAll(path).color(Color.BLUE).width(5F)
            mMap.addPolyline(opts)
        }
        mMap.uiSettings.isZoomControlsEnabled = true
    }

}