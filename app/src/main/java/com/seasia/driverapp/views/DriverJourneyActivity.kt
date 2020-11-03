package com.seasia.driverapp.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.fleet.socket.SocketClass
import com.example.fleet.socket.SocketInterface
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.DialogUtil
import com.seasia.driverapp.common.DialogsInterface
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.DriverJourneyActivityBinding
import com.seasia.driverapp.maps.FusedLocationClass
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.driverjob.CashCollectInput
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.socket.BackgroundLocationService
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.viewmodel.DriverJourneyVM
import com.skyfishjy.library.RippleBackground
import kotlinx.android.synthetic.main.driver_journey_activity.*
import kotlinx.android.synthetic.main.upload_document_dialog.*
import org.json.JSONArray
import org.json.JSONObject


class DriverJourneyActivity : BaseActivity(), DialogsInterface,
    FusedLocationClass.FusedLocationInterface, SocketInterface {
    private lateinit var binding: DriverJourneyActivityBinding
    private lateinit var driverJourneyVM: DriverJourneyVM
    private lateinit var orderId: String
    private lateinit var paymentType: String
    private lateinit var amount: String
    private lateinit var orderStatus: String
    private lateinit var orderDate: String
    private lateinit var currDate: String
    private lateinit var lat: String
    private lateinit var lon: String
    private val START = "Start"
    private val ON_THE_WAY = "On the way"
    private val REACHED = "Reached"
    private val COMPLETED = "Complete"

    private val STATUS_BEFORE_START = "0"
    private val STATUS_ON_START = "8"
    private val STATUS_ON_THE_WAY = "3"
    private val STATUS_ON_REACHED = "9"
    private val STATUS_ON_COMPLETED = "5"

    // Background Service
    var gpsService: BackgroundLocationService? = null
    var serviceIntent: Intent? = null

    // Socket code
    private var socket = SocketClass.socket
    var fusedLocationClass: FusedLocationClass? = null
    private val SOCKET_METHOD = "updateLocation"

    // Animation start
    var animStart: Animation = AlphaAnimation(0.0f, 1.0f)
    var animReached: Animation = AlphaAnimation(0.0f, 1.0f)
    var animComplete: Animation = AlphaAnimation(0.0f, 1.0f)

    // Button alpha fading animation
    val mAnimationSet: AnimatorSet = AnimatorSet() //Start
    val reachedAnimationSet: AnimatorSet = AnimatorSet() //Reached
    val completeAnimationSet: AnimatorSet = AnimatorSet() //Complete

    // Icon below button fading animation
    val onWayIconAnimationSet: AnimatorSet = AnimatorSet() //on way
    val reachedIconAnimationSet: AnimatorSet = AnimatorSet() //reached
    val completeIconAnimationSet: AnimatorSet = AnimatorSet() //complete


    override fun getLayoutId(): Int {
        return R.layout.driver_journey_activity
    }

    override fun initViews() {
        binding = viewDataBinding as DriverJourneyActivityBinding
        driverJourneyVM = ViewModelProvider(this).get(DriverJourneyVM::class.java)

//        binding.rlJourneyRoot.addView(MyJouneyAnimation(this, binding.jStart.path, binding.jStart.paint))

        binding.btnStart.setOnClickListener {
//            showAlert(START, getString(R.string.job_start))
            checkAlreadyStartedJob()
        }
        binding.btnOnTheWay.setOnClickListener {
//            showAlert(
//                ON_THE_WAY,
//                getString(R.string.job_on_way)
//            )
            onJobOnWay(true)
        }
        binding.btnReached.setOnClickListener {
//            showAlert(
//                REACHED,
//                getString(R.string.job_reached)
//            )
            onJobReached(true)
        }
        binding.btnComplete.setOnClickListener {
            showAlert(COMPLETED, getString(R.string.job_complete))
        }

        getExtras()
        initJobDetailsObserver()
        initToolbar()

//        openSocketStartOrderLocationUpdate()

//        animateThing()
//        blinkEffect()

//        onTheWayAnimation()
        onLocationClick()

//        onTheWayRipple()

        setFadingAnimation(binding.btnStart, START)

        // Ripple reached
//        binding.rbReached.startRippleAnimation()
    }

    private fun onTheWayRipple() {
//        binding.content.startRippleAnimation()

        // Icon animation
//        val v = binding.centerImage
        fadeInAnimationOnWay(binding.content, 4000)

//        val fadeOut: ObjectAnimator = ObjectAnimator.ofFloat(v, "alpha", 1f, .3f)
//        fadeOut.setDuration(1000)
//        val fadeIn: ObjectAnimator = ObjectAnimator.ofFloat(v, "alpha", .3f, 1f)
//        fadeIn.setDuration(2000)
//        onWayIconAnimationSet.play(fadeIn).after(fadeOut)
//        onWayIconAnimationSet.addListener(object : AnimatorListenerAdapter() {
//            override fun onAnimationEnd(animation: Animator?) {
//                super.onAnimationEnd(animation)
//                onWayIconAnimationSet.start()
//            }
//        })
//        onWayIconAnimationSet.start()
    }

    private fun onTheWayRipplesStop() {
//        binding.content.stopRippleAnimation()

        // Stop icon animation
        fadeOutAnimationOnWay(binding.content, 500)

//        Handler().postDelayed(Runnable {
//            fadeOutAnimationOnWay(binding.content, 1500)
//        }, 2000)

//        onWayIconAnimationSet.removeAllListeners()
//        onWayIconAnimationSet.end()
//        onWayIconAnimationSet.cancel()
    }

    private fun onLocationClick() {
        binding.commonToolBar.tvEdit.setOnClickListener {
            showGoogleMaps()
        }
    }

    private fun showGoogleMaps() {
        val intent = Intent(this, CurrentLocNewActivity::class.java)
        intent.putExtra("lat", lat)
        intent.putExtra("lon", lon)
        startActivity(intent)
    }

    private fun onTheWayAnimation() {
        Glide.with(this)
            .load(resources.getDrawable(R.drawable.smoke2))
            .into(binding.ivOnTheWay)
    }

    private fun blinkEffectStart() {
        animStart.duration = 1000 //You can manage the blinking time with this parameter

        animStart.startOffset = 20
        animStart.repeatMode = Animation.REVERSE
        animStart.repeatCount = Animation.INFINITE
        binding.jOnTheWay.startAnimation(animStart)
    }

    private fun resetAnimationStart() {
        animStart.cancel()
        animStart.reset()
    }

    private fun blinkEffectReached() {
        animReached.duration = 500 //You can manage the blinking time with this parameter

        animReached.startOffset = 20
        animReached.repeatMode = Animation.REVERSE
        animReached.repeatCount = Animation.INFINITE

        binding.jComplete.startAnimation(animReached)
        resetAnimationStart()
    }

    private fun resetAnimationReached() {
        animReached.cancel()
        animReached.reset()
    }

    private fun ifStartEnabledDiableOtherButtons() {
        if (binding.btnStart.isEnabled) {
            // Hide others
            binding.btnOnTheWay.isEnabled = false
            binding.btnReached.isEnabled = false
            binding.btnComplete.isEnabled = false

            binding.btnOnTheWay.background.alpha = 180
            binding.btnReached.background.alpha = 180
            binding.btnComplete.background.alpha = 180

            binding.btnOnTheWay.getBackground()
                .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)
            binding.btnReached.getBackground()
                .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)
            binding.btnComplete.getBackground()
                .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)

//            activeButtonOrange(START)
        }
    }

    private fun onStartClickEnableOtherButtons() {
        binding.btnReached.isEnabled = true
        binding.btnComplete.isEnabled = true

//        binding.btnReached.background.alpha = 255
//        binding.btnComplete.background.alpha = 255

        binding.btnReached.getBackground()
            .setColorFilter(resources.getColor(R.color.colorActive), PorterDuff.Mode.SRC_IN)
        binding.btnComplete.getBackground()
            .setColorFilter(resources.getColor(R.color.colorActive), PorterDuff.Mode.SRC_IN)
    }

    private fun initToolbar() {
        binding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.job_status)
        binding.commonToolBar.imgToolbarText.setTextColor(
            resources.getColor(R.color.colorBlue)
        )
    }

    private fun getExtras() {
        orderId = intent.getStringExtra("orderId")
        paymentType = intent.getStringExtra("paymentType")
        amount = intent.getStringExtra("amount")
        orderStatus = intent.getStringExtra("orderStatus")
        currDate = intent.getStringExtra("currDate")
        orderDate = intent.getStringExtra("orderDate")
        lat = intent.getStringExtra("lat")
        lon = intent.getStringExtra("lon")
//        orderId = "8ea26301-67d1-45b4-b8f8-d7b103ccdda6"

        if(orderStatus.equals(STATUS_ON_START)){
            binding.btnStart.setText("Started")
        }
    }

    private fun onJobStart(statusUpdateAlongWithVisibility: Boolean) {
        binding.jStart.drawColoredPath(true)
        binding.jStart.invalidate()

        // current path
        binding.jOnTheWay.drawDriverCurrentPath(true)
        binding.jOnTheWay.invalidate()
        // Blink current path
        blinkEffectStart()

        binding.btnStart.isEnabled = false
        binding.btnStart.getBackground()
            .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)
        binding.btnStart.background.alpha = 180

        if (statusUpdateAlongWithVisibility) {
            // Befor driver can start check, if not same day
            updateDriverJourneyStatus(STATUS_ON_START)
            binding.commonToolBar.tvEdit.visibility = View.VISIBLE
        }

        // On start, disable ON the way also
        onJobOnWay(false)
    }

    private fun onJobOnWay(statusUpdateAlongWithVisibility: Boolean) {
        binding.jStart.drawColoredPath(true)
        binding.jOnTheWay.drawColoredPath(false)

        // current path
//        binding.jReached.drawDriverCurrentPath(true)
//        binding.jReached.invalidate()

        // Blink current path
        // nothing to do, handled in start

        // Changed both START / ON the way to orange
        binding.jOnTheWay.drawDriverCurrentPath(true)

        binding.jStart.invalidate()
        binding.jOnTheWay.invalidate()

        binding.btnStart.isEnabled = false
        binding.btnOnTheWay.isEnabled = false
        binding.btnReached.isEnabled = true
        binding.btnComplete.isEnabled = true

        binding.btnStart.background.alpha = 180
        binding.btnOnTheWay.background.alpha = 180

        if (statusUpdateAlongWithVisibility) {
            updateDriverJourneyStatus(STATUS_ON_THE_WAY)
        }
//        activeButtonOrange(REACHED)

//        binding.ivOnTheWay.visibility = View.VISIBLE
//        binding.content.visibility = View.VISIBLE
        binding.centerImage.visibility = View.VISIBLE
        onTheWayRipple()

        setFadingAnimation(binding.btnReached, REACHED)
        stopFadingAnimation(START)

        binding.btnStart.getBackground()
            .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)
        binding.btnOnTheWay.getBackground()
            .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)

        binding.btnReached.getBackground()
            .setColorFilter(resources.getColor(R.color.colorActive), PorterDuff.Mode.SRC_IN)
        binding.btnComplete.getBackground()
            .setColorFilter(resources.getColor(R.color.colorActive), PorterDuff.Mode.SRC_IN)
    }

    private fun onJobReached(statusUpdateAlongWithVisibility: Boolean) {
        binding.jStart.drawColoredPath(true)
        binding.jOnTheWay.drawColoredPath(true)
        binding.jReached.drawColoredPath(true)

        // current path
        binding.jComplete.drawDriverCurrentPath(true)
        binding.jComplete.invalidate()
        binding.jReached.drawDriverCurrentPath(false)
        binding.jOnTheWay.drawDriverCurrentPath(false)
        // Blink current path
        blinkEffectReached()

        binding.jStart.invalidate()
        binding.jOnTheWay.invalidate()
        binding.jReached.invalidate()

        binding.btnStart.isEnabled = false
        binding.btnOnTheWay.isEnabled = false
        binding.btnReached.isEnabled = false



        binding.btnStart.background.alpha = 180
        binding.btnOnTheWay.background.alpha = 180
        binding.btnReached.background.alpha = 180

        if (statusUpdateAlongWithVisibility) {
            updateDriverJourneyStatus(STATUS_ON_REACHED)

            binding.commonToolBar.tvEdit.visibility = View.GONE

            onTheWayRipplesStop()
        }

//        activeButtonOrange(COMPLETED)

//        binding.ivOnTheWay.visibility = View.GONE
//        binding.content.visibility = View.GONE
        onTheWayRipplesStop()
//        binding.centerImage.visibility = View.GONE

        setFadingAnimation(binding.btnComplete, COMPLETED)
        stopFadingAnimation(START)
        stopFadingAnimation(REACHED)

        binding.btnStart.getBackground()
            .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)
        binding.btnOnTheWay.getBackground()
            .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)
        binding.btnReached.getBackground()
            .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)

        // Ripple reached
//        binding.rbReached.startRippleAnimation()

        reachedIconFadingAnimation()

        fadeInAnimationReached(binding.ivIconReached, 4000)
    }

    private fun onJobComplete(statusUpdateAlongWithVisibility: Boolean) {
        binding.jStart.drawColoredPath(true)
        binding.jOnTheWay.drawColoredPath(true)
        binding.jReached.drawColoredPath(true)
        binding.jComplete.drawColoredPath(true)

        // Current path
        binding.jComplete.drawDriverCurrentPath(false)
        binding.jReached.drawDriverCurrentPath(false)
        binding.jOnTheWay.drawDriverCurrentPath(false)

        binding.jStart.invalidate()
        binding.jOnTheWay.invalidate()
        binding.jReached.invalidate()
        binding.jComplete.invalidate()

        binding.btnStart.isEnabled = false
        binding.btnOnTheWay.isEnabled = false
        binding.btnReached.isEnabled = false
        binding.btnComplete.isEnabled = false

        binding.btnStart.background.setColorFilter(
            resources.getColor(R.color.colorDisabled),
            PorterDuff.Mode.SRC_IN
        )

        binding.btnStart.background.alpha = 180
        binding.btnOnTheWay.background.alpha = 180
        binding.btnReached.background.alpha = 180
        binding.btnComplete.background.alpha = 180

        if (statusUpdateAlongWithVisibility) {
            updateDriverJourneyStatus(STATUS_ON_COMPLETED)

//            binding.content.visibility = View.GONE
//            binding.centerImage.visibility = View.GONE
        }

//        onTheWayRipplesStop()

//        binding.ivOnTheWay.visibility = View.GONE
//        binding.content.visibility = View.GONE
//        binding.centerImage.visibility = View.GONE

        resetAnimationReached()

        binding.btnOnTheWay.getBackground()
            .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)
        binding.btnReached.getBackground()
            .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)
        binding.btnComplete.getBackground()
            .setColorFilter(resources.getColor(R.color.colorDisabled), PorterDuff.Mode.SRC_IN)

        stopFadingAnimation(START)
        stopFadingAnimation(REACHED)
        stopFadingAnimation(COMPLETED)

        // Ripple completed
//        binding.rbComplete.startRippleAnimation()
//        binding.rbReached.stopRippleAnimation()

        completeIconFadingAnimation()

        fadeOutAnimationReached(binding.ivIconReached, 500)
        fadeInAnimationCompleted(binding.ivIconComplete, 4000)
    }

    private fun showAlert(title: String, msg: String) {
        DialogUtil.showDialog(
            this,
            this,
            title,
            msg
        )
    }

    private fun checkAlreadyStartedJob() {
        val isAlreadyStartedJobExist = MyApplication.sharedPref.getBoolPreference(
            MyApplication.instance,
            GlobalConstants.JOB_STARTED_STATUS
        )
//        if (isAlreadyStartedJobExist) {
//            UtilsFunctions.showToastWarning("Please complete already started job")
//        } else {
//            onJobStart(true)
//        }
        onJobStart(true)
        onStartClickEnableOtherButtons()
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
            START -> {
                checkAlreadyStartedJob()
            }
            ON_THE_WAY -> {
                onJobOnWay(true)
            }
            REACHED -> {
                onJobReached(true)
            }
            COMPLETED -> {
               // onJobComplete(true)

                if(paymentType.equals("2")){
                    cashCollectApi()
                } else{
                    onJobComplete(true)
                }
            }
        }
    }

    private fun cashCollect() {
        val uploadImage = Dialog(this,R.style.Theme_Dialog);
        uploadImage.requestWindowFeature(Window.FEATURE_NO_TITLE)
        uploadImage.setContentView(R.layout.upload_document_dialog)

        uploadImage.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        uploadImage.setCancelable(true)
        uploadImage.setCanceledOnTouchOutside(true)
        uploadImage.window!!.setGravity(Gravity.BOTTOM)

        uploadImage.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        uploadImage.tvPay.setOnClickListener {


            cashCollectApi()
            uploadImage.dismiss()
        }
        uploadImage.show()
    }

    fun cashCollectApi(){
        var input=CashCollectInput()
        input.amount=amount
        input.orderId=orderId
        startProgressDialog()
        driverJourneyVM.onCashCollect(input)
        driverJourneyVM.cashCollectResponse().observe(this, Observer { response ->
            stopProgressDialog()

            if (response != null) {
                val message = response.message
                if (response.code == 200) {
                    onJobComplete(true)

                } else {
                    showToastError(message)
                }
            } else {
                showToastError(MyApplication.instance.getString(R.string.internal_server_error))
            }

        })
    }


    override fun onDialogCancelAction(mView: View?, mKey: String) {}

    private fun updateDriverJourneyStatus(orderTrackingStatus: String) {
        driverJourneyVM.updateJobStatus(orderId, orderTrackingStatus).observe(this,
            Observer<CommonModel> { response ->
                if (response != null) {
                    val message = response.message
                    stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            // Don't show success messages
//                            UtilsFunctions.showToastSuccess(message!!)

                            if (orderTrackingStatus.equals(STATUS_ON_COMPLETED)) {
                                // UPDATE job already started status - END
//                                finish()
                                MyApplication.sharedPref.putBoolPreference(
                                    MyApplication.instance,
                                    GlobalConstants.JOB_STARTED_STATUS,
                                    false
                                )
                                MyApplication.sharedPref.putObject(
                                    MyApplication.instance,
                                    GlobalConstants.JOB_ORDER_ID,
                                    ""
                                )
                                MyApplication.sharedPref.putObject(
                                    MyApplication.instance,
                                    GlobalConstants.JOB_LAT,
                                    ""
                                )
                                MyApplication.sharedPref.putObject(
                                    MyApplication.instance,
                                    GlobalConstants.JOB_LON,
                                    ""
                                )

                                // Close socket, end getting order location
                                closeSocketEndOrderLocationUpdate()
                            } else if (orderTrackingStatus.equals(STATUS_ON_START)) {
                                // UPDATE job already started status - STARTED
                                MyApplication.sharedPref.putBoolPreference(
                                    MyApplication.instance,
                                    GlobalConstants.JOB_STARTED_STATUS,
                                    true
                                )
                                MyApplication.sharedPref.putObject(
                                    MyApplication.instance,
                                    GlobalConstants.JOB_ORDER_ID,
                                    orderId
                                )
                                MyApplication.sharedPref.putObject(
                                    MyApplication.instance,
                                    GlobalConstants.JOB_LAT,
                                    lat
                                )
                                MyApplication.sharedPref.putObject(
                                    MyApplication.instance,
                                    GlobalConstants.JOB_LON,
                                    lon
                                )
                                binding.btnStart.setText("Started")

                                // Open socket, start getting order location
                                openSocketStartOrderLocationUpdate()
                            }
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                } else {
                    stopProgressDialog()
                }
            })
    }

    fun initJobDetailsObserver() {
        driverJourneyVM.getJobDtlData(orderId).observe(this, Observer { response ->
            stopProgressDialog()

            if (response != null) {
                val message = response.message
                if (response.code == 200) {
                    checkTrackingStatus(response.body.trackStatus.toString())

//                    setOrderDetailsAdapter(response.body.currency, response.body.suborders)
//                    initOrderDetails(response.body)
                } else {
                    showToastError(message)
                }
            } else {
                showToastError(MyApplication.instance.getString(R.string.internal_server_error))
            }

        })
    }

    private fun checkTrackingStatus(trackingStatus: String) {
        when (trackingStatus) {
            STATUS_ON_START -> {
                onJobStart(false)

                binding.commonToolBar.tvEdit.visibility = View.VISIBLE
            }
            STATUS_ON_THE_WAY -> {
                onJobStart(false)
                onJobOnWay(false)
            }
            STATUS_ON_REACHED -> {
                onJobStart(false)
                onJobOnWay(false)
                onJobReached(false)

                binding.commonToolBar.tvEdit.visibility = View.GONE

                binding.centerImage.visibility = View.GONE
            }
            STATUS_ON_COMPLETED -> {
                onJobStart(false)
                onJobOnWay(false)
                onJobReached(false)
                onJobComplete(false)

                binding.centerImage.visibility = View.GONE
            }
        }

        ifStartEnabledDiableOtherButtons()
    }


    private fun openSocketStartOrderLocationUpdate() {
        try {
            // Background service and start location updates
            initBackgroundService()

            // Init socket code
            socket.updateSocketInterface(this)
            socket.onConnect()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
//            fusedLocationClass = null
//            socket.onDisconnect()
        }
    }

    private fun closeSocketEndOrderLocationUpdate() {
        try {
            // Stop location tracking and background service and Unbind
            stopTrackingAndService()
//            this.getApplication().stopService(serviceIntent)
//            this.getApplication().unbindService(serviceConnection)

            // Stop location updates
//            fusedLocationClass!!.stopLocationUpdates()

            // Destroy socket
            socket.onDisconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }


    /**
     *  Background location tracking
     */

    fun initBackgroundService() {
        //service
        val fusedLocation = FusedLocationClass(this, this)

        serviceIntent = Intent(this.getApplication(), BackgroundLocationService::class.java)
        this.getApplication().startService(serviceIntent)
        this.getApplication()
            .bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        fusedLocation.getLastLocation(this)
        fusedLocationClass = fusedLocation
    }


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            className: ComponentName,
            service: IBinder
        ) {
//             gpsService = ((BackgroundLocationService.LocationServiceBinder) service).getService()
            val binder = service as BackgroundLocationService.LocationServiceBinder
            gpsService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            gpsService = null
        }
    }

    private fun stopTrackingAndService() {
        gpsService?.stopTracking()
        gpsService?.stopService(
            Intent(
                this,
                BackgroundLocationService::class.java
            )
        )
        this.getApplication().unbindService(serviceConnection)

        fusedLocationClass?.stopLocationUpdates()

        // Stop fused location updates
//        mFusedLocationClass?.stopLocationUpdates()
    }


    override fun onLocationChanged(location: Location) {
//        UtilsFunctions.showToastSuccess("Locations Updates -> ${location.latitude}, ${location.longitude}")

        callSocketMethods(
            SOCKET_METHOD,
            location.latitude.toString(),
            location.longitude.toString()
        )
    }

    /**
     *  Socket code
     */
    private fun callSocketMethods(methodName: String, currentLat: String, currentLong: String) {
        val object5 = JSONObject()
        when (methodName) {
            SOCKET_METHOD -> try {
                object5.put("methodName", methodName)
                val jArr = JSONArray()
                val latLng = JSONObject()
                latLng.put("lat", currentLat)
                latLng.put("long", currentLong)
                jArr.put(latLng)
                object5.put("latLong", jArr)
                object5.put("orderId", orderId)
                object5.put("platform", "android")
                object5.put(
                    "empId", SharedPrefClass().getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.USERID
                    ).toString()
                )
                socket.sendDataToServer(methodName, object5)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSocketCall(onMethadCall: String, vararg args: Any) {
        try {
            Log.d("driverJourney", args.toString())
            when (onMethadCall) {
                SOCKET_METHOD -> try {
                    // No need to stop location updates, handled on Complete ordered
//                    fusedLocationClass!!.stopLocationUpdates()
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }

    override fun onSocketConnect(vararg args: Any) {}

    override fun onSocketDisconnect(vararg args: Any) {}

    /**
     *  Icon below button fading animation
     */
    private fun reachedIconFadingAnimation() {
/*        val v = binding.ivIconReached
        val fadeOut: ObjectAnimator = ObjectAnimator.ofFloat(v, "alpha", 1f, .3f)
        fadeOut.setDuration(1000)
        val fadeIn: ObjectAnimator = ObjectAnimator.ofFloat(v, "alpha", .3f, 1f)
        fadeIn.setDuration(2000)

        // Icon fading animation
        reachedIconAnimationSet.play(fadeIn).after(fadeOut)
        reachedIconAnimationSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                reachedIconAnimationSet.start()
            }
        })
        reachedIconAnimationSet.start()*/

        // Icon ripple animation
//        binding.rbReached.startRippleAnimation()

        // icon visibility
        binding.ivIconReached.visibility = View.VISIBLE
    }

    private fun completeIconFadingAnimation() {
/*        val v = binding.ivIconComplete
        val fadeOut: ObjectAnimator = ObjectAnimator.ofFloat(v, "alpha", 1f, .3f)
        fadeOut.setDuration(1000)
        val fadeIn: ObjectAnimator = ObjectAnimator.ofFloat(v, "alpha", .3f, 1f)
        fadeIn.setDuration(2000)

        // Icon fading animation
        completeIconAnimationSet.play(fadeIn).after(fadeOut)
        completeIconAnimationSet.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                completeIconAnimationSet.start()
            }
        })
        completeIconAnimationSet.start()*/

        // Icon ripple animation
//        binding.rbReached.stopRippleAnimation()
//        binding.rbComplete.startRippleAnimation()

        // icon visibility
        binding.ivIconReached.visibility = View.GONE
        binding.ivIconComplete.visibility = View.VISIBLE
    }

    /**
     *  Button fading animation
     */
    fun setFadingAnimation(v: View?, status: String) {
        val fadeOut: ObjectAnimator = ObjectAnimator.ofFloat(v, "alpha", 1f, .3f)
        fadeOut.setDuration(1000)
        val fadeIn: ObjectAnimator = ObjectAnimator.ofFloat(v, "alpha", .3f, 1f)
        fadeIn.setDuration(2000)

        when (status) {
            START -> {
//                mAnimationSet = AnimatorSet()
                mAnimationSet.play(fadeIn).after(fadeOut)
                mAnimationSet.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        mAnimationSet.start()
                    }
                })
                mAnimationSet.start()
            }
            REACHED -> {
                reachedAnimationSet.play(fadeIn).after(fadeOut)
                reachedAnimationSet.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        reachedAnimationSet.start()
                    }
                })
                reachedAnimationSet.start()
            }
            COMPLETED -> {
                completeAnimationSet.play(fadeIn).after(fadeOut)
                completeAnimationSet.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        completeAnimationSet.start()
                    }
                })
                completeAnimationSet.start()
            }
        }
    }

    private fun stopFadingAnimation(status: String) {
        when (status) {
            START -> {
                mAnimationSet.removeAllListeners()
                mAnimationSet.end()
                mAnimationSet.cancel()
            }
            REACHED -> {
                reachedAnimationSet.removeAllListeners()
                reachedAnimationSet.end()
                reachedAnimationSet.cancel()
            }
            COMPLETED -> {
                completeAnimationSet.removeAllListeners()
                completeAnimationSet.end()
                completeAnimationSet.cancel()
            }
        }
    }


    fun fadeInAnimationOnWay(view: View, animationDuration: Long) {
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = animationDuration
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                (view as RippleBackground).startRippleAnimation()
                view.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        view.startAnimation(fadeIn)
    }

    fun fadeOutAnimationOnWay(
        view: View,
        animationDuration: Long
    ) {
        val fadeOut: Animation = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.startOffset = animationDuration
        fadeOut.duration = animationDuration
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
//                (view as RippleBackground).stopRippleAnimation()
                view.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        view.startAnimation(fadeOut)
    }

    fun fadeInAnimationReached(view: View, animationDuration: Long) {
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = animationDuration
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        view.startAnimation(fadeIn)
    }

    fun fadeOutAnimationReached(
        view: View,
        animationDuration: Long
    ) {
        val fadeOut: Animation = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.startOffset = animationDuration
        fadeOut.duration = animationDuration
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        view.startAnimation(fadeOut)
    }

    fun fadeInAnimationCompleted(view: View, animationDuration: Long) {
        val fadeIn: Animation = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = animationDuration
        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        view.startAnimation(fadeIn)
    }

    fun fadeOutAnimationCompleted(
        view: View,
        animationDuration: Long
    ) {
        val fadeOut: Animation = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.startOffset = animationDuration
        fadeOut.duration = animationDuration
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.INVISIBLE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        view.startAnimation(fadeOut)
    }
}