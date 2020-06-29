package com.seasia.driverapp.views.driverjobs

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.callbacks.DriverJobCallbacks
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.databinding.FragmentServicesBinding
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.utils.BaseFragment
import com.seasia.driverapp.utils.CheckRuntimePermissions
import com.seasia.driverapp.viewmodel.JobsViewModel
import com.seasia.driverapp.views.CurrentLocNewActivity
import com.seasia.driverapp.views.DashboardNewActivity
import com.seasia.driverapp.views.OrderDetailsActivity
import com.uniongoods.adapters.AssignedJobsAdapter


class AssignedJobsFragment(/*val totalAssignedJobs: DashboardAssignedJobs*/) : BaseFragment() {
    val REQUEST_CANCEL_REASON = 1
    private var servicesList = ArrayList<OrderStatusResponse.Body>()
    private lateinit var fragmentServicesBinding: FragmentServicesBinding
    private lateinit var jobsViewModel: JobsViewModel
    private val mJsonObject = JsonObject()
    private var servicesListAdapter: AssignedJobsAdapter? = null
//    private var totalAssignedJobs: DashboardAssignedJobs = DashboardAssignedJobs()

    // lOCATION
    val REQUEST_LOCATION_PERMISSIONS = 91
    val PERMISSION_LOCATION = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    )

//    constructor(): this() {
//
//    }

//    constructor(totalAssignedJobs: DashboardAssignedJobs): this() {
//        this.totalAssignedJobs = totalAssignedJobs
//    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_services
    }

    override fun initView() {
        fragmentServicesBinding = viewDataBinding as FragmentServicesBinding
        jobsViewModel = ViewModelProviders.of(this).get(JobsViewModel::class.java)
        baseActivity.startProgressDialog()
        //acceptStatus
        mJsonObject.addProperty(
            "acceptStatus", "1"/* sharedPrefClass!!.getPrefValue(
                MyApplication.instance,
                GlobalConstants.USERID
            ).toString()*/
        )

        initRecyclerView()

        //   servicesViewModel.getServicesList()
        jobsViewModel.getServicesList().observe(this,
            Observer<OrderStatusResponse> { response ->
                if (response != null) {
                    val message = response.message
                    baseActivity.stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            if (!response.body.isEmpty()) {
                                servicesList.addAll(response.body!!)
                                fragmentServicesBinding.rvServices.visibility = View.VISIBLE
                                fragmentServicesBinding.tvNoRecord.visibility = View.GONE
//                                initRecyclerView()

                                // TO DO clear service list
                                servicesList.clear()
                                servicesList.addAll(response.body)
                                servicesListAdapter?.notifyDataSetChanged()

                                (activity as DashboardNewActivity).assignedJobsCount(response.body.size)

//                                totalAssignedJobs.assignedJobsCount(response.body.size)
                            } else {
                                message?.let {
//                                    UtilsFunctions.showToastError(it)
                                    fragmentServicesBinding.rvServices.visibility = View.GONE
                                    fragmentServicesBinding.tvNoRecord.visibility = View.VISIBLE

                                    (activity as DashboardNewActivity).assignedJobsCount(response.body.size)
//                                    totalAssignedJobs.assignedJobsCount(response.body.size)
                                }
                            }
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            servicesList.clear()
                            fragmentServicesBinding.rvServices.visibility = View.GONE
                            fragmentServicesBinding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }
                } else {
                    baseActivity.stopProgressDialog()
                }
            })
        // initRecyclerView()
    }

    private fun initRecyclerView() {
/*        for (item in servicesList) {
            // Save Order ID of already started Job
            if (item.trackStatus > 0) {
                MyApplication.sharedPref.putObject(
                    MyApplication.instance,
                    GlobalConstants.JOB_ORDER_ID,
                    item.id
                )
            }
        }*/

        servicesListAdapter =
            AssignedJobsAdapter(
                this@AssignedJobsFragment,
                servicesList, requireActivity(),
                object : DriverJobCallbacks {
                    override fun onJobStarted(
                        orderId: String,
                        orderStatus: String,
                        lat: String, lon: String,
                        flagYesNo: String
                    ) {
                        baseActivity.stopProgressDialog()

                        if (flagYesNo.equals("Yes")) {
                            onJobStartClick(orderId, orderStatus, lat, lon)
                        } else {
                            showGMaps(lat, lon)
                        }
                    }

                    override fun onJobCanceled(
                        orderId: String,
                        orderStatus: String,
                        lat: String,
                        lon: String
                    ) {
                        baseActivity.stopProgressDialog()
//                        JobStartOrCancelClick(orderId, orderStatus, lat, lon)
                        onJobCanceledClick(orderId, orderStatus, lat, lon)
                    }

                    override fun onJobDetails(orderId: String, orderStatus: String) {
                        onJobDetailsClick(orderId, orderStatus)
                    }

                    override fun onShowGMaps(lat: String, lon: String) {
                        showGMaps(lat, lon)
                    }
                }
            )

        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentServicesBinding.rvServices.layoutManager = linearLayoutManager
        fragmentServicesBinding.rvServices.adapter = servicesListAdapter
        fragmentServicesBinding.rvServices.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (UtilsFunctions.isNetworkConnected()) {
//            baseActivity.startProgressDialog()

            servicesList.clear()
            servicesListAdapter?.notifyDataSetChanged()

            val companyId = MyApplication.sharedPref.getPrefValue(
                requireContext(),
                ApiKeysConstants.COMPANY_ID
            ) as String
            jobsViewModel.getServices("1", companyId)
        }
    }

    private fun onJobStartClick(
        orderId: String,
        orderStatus: String,
        lat: String,
        lon: String
    ) {
        jobsViewModel.updateOrderStatus(orderId, orderStatus).observe(this,
            Observer<CommonModel> { response ->
                if (response != null) {
                    val message = response.message
                    baseActivity.stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            showGMaps(lat, lon)
//                            onJobCanceledClick(orderId, orderStatus, lat, lon)

//                            if (flag.equals("start")) {
//                                showGMaps(lat, lon)
//                            } else if(flag.equals("cancel")) {
//                                onJobCanceledClick(orderId, orderStatus, lat, lon)
//                            }
                            UtilsFunctions.showToastSuccess(message!!)
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                } else {
                    baseActivity.stopProgressDialog()
                }
            })
    }

    private fun onJobCanceledClick(orderId: String, orderStatus: String, lat: String, lon: String) {
        val intent = Intent(context, JobCancelReasonActivity::class.java)
        intent.putExtra("orderId", orderId)
        startActivity(intent)
//        startActivityForResult(intent, REQUEST_CANCEL_REASON)

//        showRadioButtonDialog()
    }

    private fun onJobDetailsClick(orderId: String, orderStatus: String) {
        val intent = Intent(context, OrderDetailsActivity::class.java)
        intent.putExtra("orderId", orderId)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CANCEL_REASON -> {

                }
            }
        }
    }

    private fun showRadioButtonDialog() {
        val context = requireContext()
        // custom dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.cancel_job_dialog)
        val stringList: MutableList<String> = ArrayList() // here is list
        for (i in 0..1) {
            if (i == 0) {
                stringList.add("Number Mode")
            } else {
                stringList.add("Character Mode")
            }
        }
        val rg = dialog.findViewById(R.id.radio_group) as RadioGroup
        for (i in stringList.indices) {
            val rb =
                RadioButton(context) // dynamically creating RadioButton and adding to RadioGroup.
            rb.text = stringList[i]
            rg.addView(rb)
        }
        dialog.show()
    }

    private fun showGMaps(lat: String, lon: String) {
        if (CheckRuntimePermissions.checkMashMallowPermissions(
                activity as BaseActivity,
                PERMISSION_LOCATION, REQUEST_LOCATION_PERMISSIONS
            )
        ) {
            // Check GPS is on
            if (UtilsFunctions.checkGpsEnabled(baseActivity)) {
                val ctx = requireActivity()
                val intent = Intent(ctx, CurrentLocNewActivity::class.java)
                intent.putExtra("lat", lat)
                intent.putExtra("lon", lon)
                ctx.startActivity(intent)
            }
        }
    }

    private fun showGoogleMaps(lat: String, lon: String) {
        val lati = "31.3260"
        val longi = "75.5762"
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("http://maps.google.com/maps?daddr=$lat,$lon")
        )
        startActivity(intent)
    }


/*    companion object {
        private const val MY_BOOLEAN = "my_boolean"
        private const val MY_INT = "my_int"

        fun newInstance(aBoolean: Boolean, anInt: Int) = AssignedJobsFragment().apply {
            arguments = Bundle(1).apply {
                putBoolean(MY_BOOLEAN, aBoolean)
                putInt(MY_INT, anInt)
            }
        }
    }*/
}