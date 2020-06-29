package com.seasia.driverapp.views

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.seasia.driverapp.R
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.maps.FusedLocationClass

class CurrentLocNewActivity : FragmentActivity(), OnMapReadyCallback,
    FusedLocationClass.FusedLocationInterface {
    private val TAG = "CurrentLocNewActivity"
    var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private var fusedLocationClass: FusedLocationClass? = null
    private val REQUEST_CODE = 101
    private var mMap: GoogleMap? = null
    private var destinationLat = ""
    private var destinationLon = ""
    val REQUEST_PERMISSIONS = 99

    var INIT_ANIMATION = 1
    var INIT_HANDLER = 0
    val MAX_ANIMATION = 3
    val MAX_LOOPS = 5

    val PERMISSION_ID = 333

    var onStateChange = MutableLiveData<Boolean>()


    val PERMISSION_READ_STORAGE = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    )

    var polyPath: MutableList<LatLng>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getExtras()


//        if (CheckRuntimePermissions.checkMashMallowPermissions(
//                this,
//                PERMISSION_READ_STORAGE, REQUEST_PERMISSIONS
//            )
//        ) {
//            fetchLocation()
//        }

//        getLastLocation()
        showMapLocations()
    }

    private fun getLastLocation() {
        val fusedLocation = FusedLocationClass(this, this)
        val loc = fusedLocation.getLastLocation(this)

        fusedLocationClass = fusedLocation

//        if (loc != null) {
//            UtilsFunctions.showToastSuccess("${loc.latitude}")
//        } else {
//            UtilsFunctions.showToastWarning("Location null")
//        }
    }

    private fun showMapLocations() {
        if (checkPermissions()) {
            fetchLocation()
        } else {
            requestPermissions()
        }
    }

    private fun getExtras() {
        val intent: Intent = getIntent()
        destinationLat = intent.getStringExtra("lat")
        destinationLon = intent.getStringExtra("lon")

//        destinationLat = "31.3260"
//        destinationLon = "75.5762"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val src =
            LatLng(
                currentLocation!!.latitude,
                currentLocation!!.longitude
            )

        val dest =
            LatLng(
                destinationLat.toDouble(),
                destinationLon.toDouble()
            )

        val markerOptions = MarkerOptions()
            .position(src)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_loc))
            .title("Current Location")
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(src))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(src, 6f))
        googleMap.addMarker(markerOptions)
        addDestinationMarker(dest)

        drawPolyline(src, dest)
    }

    private fun addDestinationMarker(dest: LatLng) {
        try {

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

    private fun drawPolyline(fkip: LatLng, destLocation: LatLng) {
        var path: MutableList<LatLng> = ArrayList()
        val context = GeoApiContext.Builder()
            .apiKey(getString(R.string.directions_api_key))
            .build()
        val req = DirectionsApi.getDirections(
            context,
            fkip.latitude.toString().plus(",").plus(fkip.longitude.toString()),
            destLocation.latitude.toString().plus(",").plus(destLocation.longitude.toString())
        )
        try {
            val res = req.await()
            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.isNotEmpty()) {
                val route = res.routes[0]
                if (route.legs != null) {
                    for (i in 0 until route.legs.size) {
                        val leg = route.legs[i]
                        if (leg.steps != null) {
                            for (j in 0 until leg.steps.size) {
                                val step = leg.steps[j]
                                if (step.steps != null && step.steps.isNotEmpty()) {
                                    for (k in 0 until step.steps.size) {
                                        val step1 = step.steps[k]
                                        val points1 = step1.polyline
                                        if (points1 != null) {
                                            //Decode polyline and add points to latLongList of route coordinates
                                            val coords1 = points1.decodePath()
                                            for (coord1 in coords1) {
                                                // path.add(LatLng(coord1.lat, coord1.lng))
                                                path.add(LatLng(coord1.lat, coord1.lng))
                                            }
                                        }
                                    }
                                } else {
                                    val points = step.polyline
                                    if (points != null) {
                                        //Decode polyline and add points to latLongList of route coordinates
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
            } else {
                //  mGoogleMap.drawPolyline("Destination is not detected,unable to draw path")
                Log.d("MapPath", "Unable to draw path")
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        drawLine(path, fkip, destLocation)
    }

    private fun drawLine(path: MutableList<LatLng>, fkip: LatLng, destLocation: LatLng) {
        polyPath = path
        mMap?.clear()
        //  var icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_car)
        var icon = bitmapDescriptorFromVector(this, R.drawable.ic_car)
        mMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(fkip.latitude, fkip.longitude))
            //.snippet(points[0].longitude.toString() + "")
//                .icon(icon)
        )
        mMap!!.addMarker(
            MarkerOptions()
                .position(LatLng(destLocation.latitude, destLocation.longitude))
            //.snippet(points[0].longitude.toString() + "")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        )
        if (polyPath!!.size > 0) {
            val opts = PolylineOptions().addAll(path).color(R.color.colorRed).width(20f)
            /*polylineFinal = */mMap?.addPolyline(opts)
        }

        val maps = mMap
        val directionPoints = ArrayList<LatLng>()
//        directionPoints.add(LatLng(30.9010, 75.8573)) // Ludhiana
        directionPoints.add(LatLng(30.9785, 75.7938)) // Ladowal
        directionPoints.add(LatLng(31.0190, 75.7879)) // Phillaur
//        directionPoints.add(LatLng(31.1241, 75.7713)) // Goraya
        directionPoints.add(LatLng(31.2232, 75.7670)) // Phgwara
//        directionPoints.add(LatLng(31.2551, 75.7050)) // LPU
//        directionPoints.add(LatLng(31.2770, 75.6804)) // Khajurla
//        directionPoints.add(LatLng(31.3098, 75.6120)) // PAP chowk
        directionPoints.add(LatLng(31.3260, 75.5762)) // Jalandhar

//        setAnimation(maps!!, directionPoints)
//        setContinuousAnimation(maps!!, directionPoints)


        // Calculate mid-point and place camera position in center
//        val cameraPosition: CameraPosition = CameraPosition.Builder()
//            .target(midPoint(fkip.latitude, fkip.longitude, destLocation.latitude, destLocation.longitude)).zoom(14f)
//                .bearing(angleBteweenCoordinate(fkip.latitude, fkip.longitude, destLocation.latitude, destLocation.longitude).toFloat()).build()
//        mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun setContinuousAnimation(maps: GoogleMap, directionPoints: ArrayList<LatLng>) {
        val handler = Handler()

        val runnable = Runnable {
            0
            setAnimation(maps, directionPoints)
        }

        handler.postDelayed(runnable, 4000)
    }

    fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int
    ): BitmapDescriptor {
        var background = ContextCompat.getDrawable(this, R.drawable.ic_car);
        background?.setBounds(
            0,
            0,
            background.getIntrinsicWidth(),
            background.getIntrinsicHeight()
        )
        var vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable?.setBounds(
            40,
            20,
            vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight()
        )
        var bitmap = Bitmap.createBitmap(
            background!!.getIntrinsicWidth(),
            background.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888
        )
        var canvas = Canvas(bitmap)
        background?.draw(canvas)
        vectorDrawable?.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    fun setAnimation(
        myMap: GoogleMap,
        directionPoint: List<LatLng?>
//        bitmap: Bitmap?
    ) {
        val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.current_loc)

        val marker = myMap.addMarker(
            MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                .position(directionPoint[0]!!)
                .flat(true)
        )
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(directionPoint[0], 9f))

//        for (i in 1..5) {
//            animateMarker(myMap, marker, directionPoint, false)
//        }

        animateMarker(myMap, marker, directionPoint, false)
//        animateMarker(myMap, marker, directionPoint, true)
//        animateMarker(myMap, marker, directionPoint, true)

//        customAnimateMarker(myMap, marker, directionPoint, false)
        initStateChangeListener(myMap, marker, directionPoint, true)
    }


    private fun animateMarker(
        myMap: GoogleMap, marker: Marker, directionPoint: List<LatLng?>,
        hideMarker: Boolean
    ) {
        val handler = Handler()
        val start: Long = SystemClock.uptimeMillis()
        val proj: Projection = myMap.projection
        val duration: Long = 6000
        val interpolator: Interpolator = LinearInterpolator()
        handler.post(object : Runnable {
            var i = 0
            override fun run() {
                val elapsed: Long = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(
                    elapsed.toFloat()
                            / duration
                )
                if (i < directionPoint.size) marker.setPosition(directionPoint[i]!!)
                i++
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 1500)
                    Log.d(TAG, "==============>> animateMarker - Postdelay")
                } else {
//                    if (INIT_ANIMATION < MAX_ANIMATION) {
//                        animateMarker(myMap, marker, directionPoint, hideMarker)
//                        INIT_ANIMATION++
//                    }

                    Log.d(TAG, "==============>> animateMarker - else")

                    onStateChange.postValue(true)

                    if (hideMarker) {
                        marker.isVisible = false
                    } else {
                        marker.isVisible = true
                    }
                }
            }
        })
    }


    private fun customAnimateMarker(
        myMap: GoogleMap, marker: Marker, directionPoint: List<LatLng?>,
        hideMarker: Boolean
    ) {
        val handler = Handler()
        val start: Long = SystemClock.uptimeMillis()
        val proj: Projection = myMap.projection
        val duration: Long = 30000
        val interpolator: Interpolator = LinearInterpolator()
        handler.post(object : Runnable {
            var i = 0
            override fun run() {
                val elapsed: Long = SystemClock.uptimeMillis() - start
                val t: Float = interpolator.getInterpolation(
                    elapsed.toFloat()
                            / duration
                )
                if (i < directionPoint.size) marker.setPosition(directionPoint[i]!!)
                i++

                if (INIT_HANDLER < directionPoint.size) {
                    for (direction in INIT_HANDLER..directionPoint.size) {
                        handler.postDelayed(this, 1000)
                        INIT_HANDLER++
                    }
                } else {
                    INIT_HANDLER = 0
                    onStateChange.postValue(true)
                }

/*                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 1500)
                    Log.d(TAG, "==============>> animateMarker - Postdelay")
                } else {
//                    if (INIT_ANIMATION < MAX_ANIMATION) {
//                        animateMarker(myMap, marker, directionPoint, hideMarker)
//                        INIT_ANIMATION++
//                    }

                    Log.d(TAG, "==============>> animateMarker - else")

                    onStateChange.postValue(true)

                    if (hideMarker) {
                        marker.isVisible = false
                    } else {
                        marker.isVisible = true
                    }
                }*/
            }
        })
    }


/*    private fun movingMarker() {
        try {
//            if (googleMap == null) {
//                googleMap =
//                    (fragmentManager.findFragmentById(R.id.map) as MapFragment).getMap()
//            }
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL)
            googleMap.setMyLocationEnabled(true)
            googleMap.setTrafficEnabled(false)
            googleMap.setIndoorEnabled(false)
            googleMap.setBuildingsEnabled(true)
            googleMap.getUiSettings().setZoomControlsEnabled(true)
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(SomePos))
            googleMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    Builder()
                        .target(googleMap.getCameraPosition().target)
                        .zoom(17)
                        .bearing(30)
                        .tilt(45)
                        .build()
                )
            )
            myMarker = googleMap.addMarker(
                MarkerOptions()
                    .position(SomePos)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                    .title("Hello world")
            )
            googleMap.setOnMarkerClickListener(object : GoogleMap.OnMarkerClickListener {
                val startPosition: LatLng = myMarker.getPosition()
                val finalPosition = LatLng(37.7801569, -122.4148528)
                val handler = Handler()
                val start = SystemClock.uptimeMillis()
                val interpolator: Interpolator =
                    AccelerateDecelerateInterpolator()
                val durationInMs = 3000f
                val hideMarker = false
                handler.post(
                object : Runnable {
                    var elapsed: Long = 0
                    var t = 0f
                    var v = 0f
                    override fun run() {
                        // Calculate progress using interpolator
                        elapsed = SystemClock.uptimeMillis() - start
                        t = elapsed / durationInMs
                        v = interpolator.getInterpolation(t)
                        val currentPosition = LatLng(
                            startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                            startPosition.longitude * (1 - t) + finalPosition.longitude * t
                        )
                        myMarker.setPosition(currentPosition)

                        // Repeat till progress is complete.
                        if (t < 1) {
                            // Post again 16ms later.
                            handler.postDelayed(this, 16)
                        } else {
                            if (hideMarker) {
                                myMarker.setVisible(false)
                            } else {
                                myMarker.setVisible(true)
                            }
                        }
                    }
                })
                return true
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }*/

    private fun initStateChangeListener(
        myMap: GoogleMap, marker: Marker, directionPoint: List<LatLng?>,
        hideMarker: Boolean
    ) {
        onStateChange.observe(this, Observer {
            Log.d(TAG, "==============>> initStateChangeListener")

            if (it) {
                Log.d(TAG, "==============>> initStateChangeListener TRUE")
                if (INIT_ANIMATION < MAX_ANIMATION) {
                    animateMarker(myMap, marker, directionPoint, false)
//                    customAnimateMarker(myMap, marker, directionPoint, false)
                    INIT_ANIMATION++
                }
//                animateMarker(myMap, marker, directionPoint, true)
            }
        })
    }


    /**
     *  Locations Permissions
     */
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    /**
     *  Camera mid-point position
     */
    private fun midPoint(
        lat1: Double,
        long1: Double,
        lat2: Double,
        long2: Double
    ): LatLng? {
        return LatLng((lat1 + lat2) / 2, (long1 + long2) / 2)
    }

    private fun angleBteweenCoordinate(
        lat1: Double, long1: Double, lat2: Double,
        long2: Double
    ): Double {
        val dLon = long2 - long1
        val y = Math.sin(dLon) * Math.cos(lat2)
        val x =
            Math.cos(lat1) * Math.sin(lat2) - (Math.sin(lat1)
                    * Math.cos(lat2) * Math.cos(dLon))
        var brng = Math.atan2(y, x)
        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360
        brng = 360 - brng
        return brng
    }

//    private fun requestLocationUpdates() {
//        fusedLocationProviderClient?.requestLocationUpdates(getLocationRequest(), mLocationCallback, null);
//    }

    override fun onLocationChanged(location: Location) {
        currentLocation = location
//        UtilsFunctions.showToastSuccess("${location.latitude} - ${location.longitude}")
//        updateMap()

        fusedLocationClass?.stopLocationUpdates()
    }

    private fun updateMap() {
        val src =
            LatLng(
                currentLocation!!.latitude,
                currentLocation!!.longitude
            )

        val dest =
            LatLng(
                destinationLat.toDouble(),
                destinationLon.toDouble()
            )

        val markerOptions = MarkerOptions()
            .position(src)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.current_loc))
            .title("Current Location")
        mMap?.animateCamera(CameraUpdateFactory.newLatLng(src))
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(src, 6f))
        mMap?.addMarker(markerOptions)
        addDestinationMarker(dest)

        drawPolyline(src, dest)
    }

    override fun onDestroy() {
        super.onDestroy()

//        fusedLocationClass?.stopLocationUpdates()
        fusedLocationClass = null
        fusedLocationProviderClient = null
    }
}