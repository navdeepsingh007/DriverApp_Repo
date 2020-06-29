package com.seasia.driverapp.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.seasia.driverapp.R

class CurrentLocActivity : FragmentActivity(), OnMapReadyCallback {
    var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private val REQUEST_CODE = 101
    private var mMap: GoogleMap? = null
    private var destinationLat = ""
    private var destinationLon = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
        getExtras()
    }

    private fun getExtras() {
        val intent: Intent = getIntent()
        destinationLat = intent.getStringExtra("lat")
        destinationLon = intent.getStringExtra("lon")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val latLng =
            LatLng(
                currentLocation!!.latitude,
                currentLocation!!.longitude
            )
        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_loc))
            .title("Current Location")
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
        googleMap.addMarker(markerOptions)
        addDestinationMarker()
    }

    private fun addDestinationMarker() {
        try {
            val dest =
                LatLng(
                    destinationLat.toDouble(),
                    destinationLon.toDouble()
                )
            mMap!!.addMarker(
                MarkerOptions()
                    .position(dest)
                    .title("Destination")
            )
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }
        val task =
            fusedLocationProviderClient!!.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                //                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                val supportMapFragment =
                    (getSupportFragmentManager().findFragmentById(R.id.map) as SupportMapFragment)
                supportMapFragment.getMapAsync(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }

    fun showDirectionsOnMap(v: View?) {
//        val lati = "31.3260"
//        val longi = "75.5762"
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?daddr=$destinationLat,$destinationLon")
        )
        startActivity(intent)
    }
}