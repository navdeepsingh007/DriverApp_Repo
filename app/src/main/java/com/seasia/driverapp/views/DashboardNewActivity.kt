package com.seasia.driverapp.views

import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.RequestOptions
import com.example.fleet.socket.SocketClass
import com.example.fleet.socket.SocketInterface
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.tabs.TabLayout
import com.seasia.driverapp.R
import com.seasia.driverapp.adapters.NavOptionsAdapter
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.callbacks.DashboardAssignedJobs
import com.seasia.driverapp.common.DialogUtil
import com.seasia.driverapp.common.DialogsInterface
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.ActivityDashboardNewBinding
import com.seasia.driverapp.databinding.ContentMainBinding
import com.seasia.driverapp.databinding.NavHeaderMainBinding
import com.seasia.driverapp.fcm.FcmUtils
import com.seasia.driverapp.maps.FusedLocationClass
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.socket.BackgroundLocationService
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.viewmodel.LoginVM
import com.seasia.driverapp.viewmodel.JobsViewModel
import com.seasia.driverapp.views.driverjobs.AssignedJobsFragment
import com.seasia.driverapp.views.driverjobs.CompletedJobsFragment
import org.json.JSONObject

class DashboardNewActivity : BaseActivity(), DialogsInterface,
    FusedLocationClass.FusedLocationInterface, SocketInterface {
    private val SOCKET_METHOD = "updateLocation"
    lateinit var binding: ActivityDashboardNewBinding
    private lateinit var contentBinding: ContentMainBinding
    private lateinit var navBinding: NavHeaderMainBinding
    private lateinit var navController: NavController
    private lateinit var dialog: Dialog
    private lateinit var loginVM: LoginVM

    private lateinit var jobsViewModel: JobsViewModel
    private var fragment: Fragment? = null

    // Background Service
    var gpsService: BackgroundLocationService? = null
    var serviceIntent: Intent? = null
    var googleApiClient: GoogleApiClient? = null
    var mLastLocation: Location? = null

    // Socket code
    private var socket = SocketClass.socket
    var fusedLocationClass: FusedLocationClass? = null


    override fun getLayoutId(): Int {
        return R.layout.activity_dashboard_new
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityDashboardNewBinding
        loginVM = ViewModelProvider(this).get(LoginVM::class.java)

        navBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.nav_header_main,
            binding.navView,
            false
        )

        navBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))

//        binding.navView.addHeaderView(.root)

        contentBinding = binding.incAppBar.incContentMain

        initNavViewActivity()
        setNavOptionsAdapter()
        initContentMain()
//        initNavUserProfile()

        // Background service
//        initBackgroundService()

        // Init socket code
        socket.updateSocketInterface(this)
        socket.onConnect()

        // FCM
//        FcmUtils.isGooglePlayServicesAvailable(this)
        FcmUtils.getInstanceId()
    }

    fun initNavViewActivity() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        navController = findNavController(R.id.my_nav_host_fragment)
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, binding.drawerLayout)

        binding.navView.setupWithNavController(navController)

        toolbar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setNavOptionsAdapter() {
        binding.rvNavDrawer.adapter = NavOptionsAdapter(this)
        binding.rvNavDrawer.layoutManager = LinearLayoutManager(this)
        binding.tvLogout.setOnClickListener { showLogoutAlert() }
    }

    private fun initNavUserProfile() {
        navBinding.ivNavImage.setOnClickListener { onProfileClick() }
        navBinding.ivEditImage.setOnClickListener { onProfileClick() }
        initNavUserPicture()
    }

    private fun onProfileClick() {
        binding.drawerLayout.closeDrawers()
        startActivity(Intent(this, ProfileNewActivity::class.java))
    }

    fun initNavUserPicture() {
        val picturePath =
            MyApplication.sharedPref.getPrefValue(
                this,
                GlobalConstants.USER_IMAGE
            ) as String?

        UtilsFunctions.loadImage(
            this,
            picturePath!!,
            RequestOptions(),
            R.drawable.user,
            navBinding.ivNavImage
        )

        val userName = MyApplication.sharedPref.getPrefValue(
            this,
            GlobalConstants.USER_NAME
        ) as String?

        navBinding.tvNavUserName.setText(userName)
    }

    override fun onResume() {
        super.onResume()
        initNavUserPicture()

        // Ask user to turn on GPS
        if (!UtilsFunctions.checkGpsEnabled(this)) {
            // GPS not enabled asked user, no need to do anything
        }
        // Check play services for FCM
//        FcmUtils.isGooglePlayServicesAvailable(this)

//        ifAlreadyStartedJobOpenThat()
    }

    private fun ifAlreadyStartedJobOpenThat() {
        val isAlreadyStartedJobExist = MyApplication.sharedPref.getBoolPreference(
            MyApplication.instance,
            GlobalConstants.JOB_STARTED_STATUS
        )
        if (isAlreadyStartedJobExist) {
            val orderId = MyApplication.sharedPref.getPrefValue(
                MyApplication.instance,
                GlobalConstants.JOB_ORDER_ID
            ) as String?
            val lat = MyApplication.sharedPref.getPrefValue(
                MyApplication.instance,
                GlobalConstants.JOB_LAT
            ) as String?
            val lon = MyApplication.sharedPref.getPrefValue(
                MyApplication.instance,
                GlobalConstants.JOB_LON
            ) as String?

            val intent = Intent(this, DriverJourneyActivity::class.java)
            intent.putExtra("orderId", orderId ?: "")
            intent.putExtra("lat", lat ?: "")
            intent.putExtra("lon", lon ?: "")
            startActivity(intent)
        }
    }

  /*  override fun onBackPressed() {
        super.onBackPressed()
        //showLogoutAlert()
    }*/

    fun showLogoutAlert() {
        dialog = DialogUtil.showDialog(
            this,
            this,
            "Logout!",
            getString(R.string.logout_confirmation)
        )
//        val intent1 = Intent(this, LoginUserActivity::class.java)
//        startActivity(intent1)
    }

    /**
     *  Content Main
     */
    private fun initContentMain() {
        jobsViewModel = ViewModelProviders.of(this).get(JobsViewModel::class.java)
        fragment = AssignedJobsFragment()
        callFragments(fragment, supportFragmentManager, false, "send_data", "")

        contentBinding.tablayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var fragment: Fragment? = null

                when (tab!!.position) {
                    0 -> fragment = AssignedJobsFragment()
                    1 -> fragment = CompletedJobsFragment()
                }
                callFragments(fragment, supportFragmentManager, false, "send_data", "")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //var fragment : Fragment? = null
                //Not In use
            }
        })
    }

    /**
     *  lOGOUT user
     */
    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        startProgressDialog()
        when (mKey) {
            "Logout!" -> initLogoutResponseObserver()
        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            "Logout!" -> dialog.dismiss()
        }
    }

    fun initLogoutResponseObserver() {
        loginVM.getLogoutData().observe(this, Observer { response ->
            stopProgressDialog()

            if (response != null) {
                val message = response.message!!
                if (response.code == 200) {
                    // Go to login after logout
                    startActivity(Intent(this, LoginUserActivity::class.java))

                    UtilsFunctions.showToastSuccess(message)
                    finish()

                    SharedPrefClass().putObject(
                        MyApplication.instance.applicationContext,
                        "isLogin",
                        false
                    )
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        ApiKeysConstants.OFFLINE_STATUS,
                        ""
                    )
                } else {
                    UtilsFunctions.showToastError(message)
                }
            } else {
                UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
            }
        })
    }

    fun assignedJobsCount(size: Int) {
        if (size > 0) {
            val count = "($size)"
            binding.incAppBar.tvAssignedJobsCount.text = count

            binding.incAppBar.tvAssignedJobsCount.visibility = View.VISIBLE
        } else {
            binding.incAppBar.tvAssignedJobsCount.visibility = View.GONE
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

        // Stop fused location updates
//        mFusedLocationClass?.stopLocationUpdates()
    }


    override fun onLocationChanged(location: Location) {
//        UtilsFunctions.showToastSuccess("Locations Updates -> ${location.latitude}, ${location.longitude}")

//        callSocketMethods(
//            SOCKET_METHOD,
//            location.latitude.toString(),
//            location.longitude.toString()
//        )
    }

    /**
     *  Socket code
     */
    private fun callSocketMethods(methodName: String, currentLat: String, currentLong: String) {
        val object5 = JSONObject()
        when (methodName) {
            SOCKET_METHOD -> try {
                object5.put("methodName", methodName)
                object5.put("latitude", currentLat)
                object5.put("longitude", currentLong)
//                object5.put(
//                    "driver_id", SharedPrefClass().getPrefValue(
//                        MyApplication.instance,
//                        GlobalConstants.USERID
//                    ).toString()

                // Extra parameter added
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
            when (onMethadCall) {
                SOCKET_METHOD -> try {
//                    if (fusedLocationClass != null) { }
                    fusedLocationClass!!.stopLocationUpdates()

//                    mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                } catch (e1: Exception) {
                    e1.printStackTrace()
                }
            }
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }

    override fun onSocketConnect(vararg args: Any) {

    }

    override fun onSocketDisconnect(vararg args: Any) {

    }

    // Get FCM token

}