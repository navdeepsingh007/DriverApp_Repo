package com.seasia.driverapp.views.driverjobs

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
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
import com.seasia.driverapp.databinding.FragmentActiveJobsBinding
import com.seasia.driverapp.databinding.FragmentServicesBinding
import com.seasia.driverapp.model.*
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.utils.BaseFragment
import com.seasia.driverapp.utils.CheckRuntimePermissions
import com.seasia.driverapp.viewmodel.JobsViewModel
import com.seasia.driverapp.views.CurrentLocNewActivity
import com.seasia.driverapp.views.DashboardNewActivity
import com.seasia.driverapp.views.OrderDetailsActivity
import com.uniongoods.adapters.ActiveJobsAdapter
import com.uniongoods.adapters.AssignedJobsAdapter
import kotlinx.android.synthetic.main.fragment_services.*


class ActiveJobsFragment : BaseFragment() {

    val REQUEST_CANCEL_REASON = 1
    private var servicesList = ArrayList<OrderStatusResponse.Body>()
    private  var jobsViewModel: JobsViewModel?=null
    private val mJsonObject = JsonObject()
    private var servicesListAdapter: ActiveJobsAdapter? = null
    var count=1
    var limit: Int = 10
    var mPastVisibleItems = 0
    var mVisibleItemCount = 0
    var mTotalItemCount = 0
    var mLoadMoreViewCheck = true
    var mStatus="0"

    // lOCATION
    val REQUEST_LOCATION_PERMISSIONS = 91
    val PERMISSION_LOCATION = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    )


    var binding:FragmentActiveJobsBinding?=null
    override fun getLayoutResId(): Int {
      return R.layout.fragment_active_jobs
    }

    override fun initView() {
        binding= viewDataBinding as FragmentActiveJobsBinding
        jobsViewModel = ViewModelProviders.of(this).get(JobsViewModel::class.java)
        baseActivity.startProgressDialog()
        //acceptStatus
        mJsonObject.addProperty(
            "acceptStatus", "1"/* sharedPrefClass!!.getPrefValue(
                MyApplication.instance,
                GlobalConstants.USERID
            ).toString()*/
        )



        binding!!.swipeContainer.setColorSchemeResources(R.color.colorBlue,R.color.colorBlue,R.color.colorBlue);

        binding!!.swipeContainer.setOnRefreshListener { // Your code to refresh the list here.

            if (UtilsFunctions.isNetworkConnected()) {
//            baseActivity.startProgressDialog()
                servicesList.clear()
                servicesListAdapter?.notifyDataSetChanged()
                val companyId = MyApplication.sharedPref.getPrefValue(
                    requireContext(),
                    ApiKeysConstants.COMPANY_ID
                ) as String
                //  baseActivity.startProgressDialog()
                jobsViewModel!!. getServices("1","", "1",limit.toString())
                //   fragmentServicesBinding.swipeContainer.isRefreshing=false

            } else{
                binding!!.swipeContainer.isRefreshing=false
            }
        }

        initRecyclerView()
//        mStatus=   SharedPrefClass().getPrefValue(MyApplication.instance.applicationContext, ApiKeysConstants.OFFLINE_STATUS).toString()
//        if(mStatus.equals("1")){
//            btnOffline.setText(getString(R.string.online))
//            btnOffline.isChecked=true
//        }
//        btnOffline.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//                btnOffline.setText(getString(R.string.online))
//                offlineStatus("1")
//                mStatus="1"
//            } else {
//                btnOffline.setText(getString(R.string.offline))
//                offlineStatus("0")
//                mStatus="0"
//            }
//        })

        //   servicesViewModel.getServicesList()
        jobsViewModel!!.getServicesList().observe(this,
            Observer<OrderStatusResponse> { response ->
                if (response != null) {
                    val message = response.message
                    baseActivity.stopProgressDialog()
                    binding!!.swipeContainer.isRefreshing=false
                    when {
                        response.code == 200 -> {
                            mLoadMoreViewCheck = true
                            if (response.body!=null) {

                                servicesList.addAll(response.body!!)
                                binding!!.rvServices.visibility = View.VISIBLE
                                binding!!.tvNoRecord.visibility = View.GONE
                                //   initRecyclerView()
                                servicesListAdapter!!.setData(servicesList)

                                // TO DO clear service list
                                //  servicesList.clear()
                                //     servicesListAdapter?.notifyDataSetChanged()

                                (activity as DashboardNewActivity).assignedJobsCount(response.body.size)

//                                totalAssignedJobs.assignedJobsCount(response.body.size)

                                if(servicesList.size>0){
                                    binding!!.tvNoRecord.visibility = View.GONE
                                } else{
                                    binding!!.tvNoRecord.visibility = View.VISIBLE
                                }
                            } else {
                                message?.let {
//                                    UtilsFunctions.showToastError(it)
                                    binding!!.rvServices.visibility = View.GONE
                                    binding!!.tvNoRecord.visibility = View.VISIBLE

                                    (activity as DashboardNewActivity).assignedJobsCount(response.body.size)
//                                    totalAssignedJobs.assignedJobsCount(response.body.size)
                                }
                            }
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            servicesList.clear()
                            binding!!.rvServices.visibility = View.GONE
                            binding!!.tvNoRecord.visibility = View.VISIBLE
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
            ActiveJobsAdapter(
                this@ActiveJobsFragment,
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

                    override fun onJobDetails(
                        orderId: String,
                        orderStatus: String,
                        currDate: String,
                        orderDate: String
                    ) {
                        onJobDetailsClick(orderId, orderStatus,currDate,orderDate)
                    }

                    override fun onShowGMaps(lat: String, lon: String) {
                        showGMaps(lat, lon)
                    }
                }
            )

        val linearLayoutManager = LinearLayoutManager(this.baseActivity,
            LinearLayoutManager.VERTICAL,false)
        binding!!.rvServices.layoutManager = linearLayoutManager
        binding!!.rvServices.adapter = servicesListAdapter
        binding!!.rvServices.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy > 0) //check for scroll down
                {
                    mVisibleItemCount = linearLayoutManager.childCount
                    mTotalItemCount = linearLayoutManager.itemCount
                    mPastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (mLoadMoreViewCheck) {
                        if ((mVisibleItemCount + mPastVisibleItems) >= mTotalItemCount) {
                            mLoadMoreViewCheck = false
                            count++
                            //reviewsViewModel.getReviewsList(serviceId, count.toString())
                            if (UtilsFunctions.isNetworkConnected()) {
                                jobsViewModel!!.getServices("1","", count.toString(),limit.toString())
                            }
                        }
                    }
                }


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
            jobsViewModel!!. getServices("1","", "1",limit.toString())
        }
    }

    private fun onJobStartClick(
        orderId: String,
        orderStatus: String,
        lat: String,
        lon: String
    ) {
        jobsViewModel!!.updateOrderStatus(orderId, orderStatus).observe(this,
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

    private fun onJobDetailsClick(
        orderId: String,
        orderStatus: String,
        currDate: String,
        orderDate: String
    ) {
        val intent = Intent(context, OrderDetailsActivity::class.java)
        intent.putExtra("orderId", orderId)
        intent.putExtra("orderStatus", orderStatus)
        intent.putExtra("currDate", currDate)
        intent.putExtra("orderDate", orderDate)
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

    fun acceptOrder(orderId: String, id: String) {

        AlertDialog.Builder(baseActivity)
//            .setTitle("Cancel")
            .setMessage("Are you sure want to accept order?")
            .setPositiveButton(
                R.string.yes,
                { dialog, which ->

                    jobsViewModel = ViewModelProviders.of(this).get(JobsViewModel::class.java)
                    val input= AcceptOrderInput()
                    input.orderId=orderId
                    input.id=id
                    baseActivity.startProgressDialog()
                    jobsViewModel!!.acceptOrderJobs(input)
                    jobsViewModel!!.acceptOrderResponse().observe(this,
                        Observer<AcceptOrderResponse> { response ->
                            if(MyApplication.callApi) {
                                MyApplication.callApi = false
                                baseActivity.stopProgressDialog()
                                if (response != null) {
                                    val message = response.message
                                    when {
                                        response.code == 200 -> {
                                            UtilsFunctions.showToastSuccess(response.message!!)
                                            servicesList.clear()
                                            jobsViewModel!!.getServices(
                                                "1",
                                                "",
                                                "1",
                                                limit.toString()
                                            )

                                        }
                                        else -> message?.let {
                                            UtilsFunctions.showToastError(response.message!!)
                                        }
                                    }
                                }
                            }
                        })
                })
            .setNegativeButton(R.string.no, null)
            .setIcon(R.drawable.ic_alert)
            .show()
    }
    fun rejectOrder(orderId: String, id: String) {
        AlertDialog.Builder(baseActivity)
//            .setTitle("Cancel")
            .setMessage("Are you sure want to reject order?")
            .setPositiveButton(
                R.string.yes,
                { dialog, which ->
                    val input= RejectOrderInput()
                    input.orderId=orderId
                    input.id=id
                    baseActivity.startProgressDialog()
                    jobsViewModel!!.rejectOrderJobs(input)
                    jobsViewModel!!.rejectOrderResponse().observe(this,
                        Observer<RejectOrderResponse> { response ->
                            if(MyApplication.callApi) {
                                MyApplication.callApi = false

                                baseActivity.stopProgressDialog()
                                if (response != null) {
                                    val message = response.message
                                    when {
                                        response.code == 200 -> {
                                            UtilsFunctions.showToastSuccess(response.message!!)
                                            servicesList.clear()
                                            jobsViewModel!!.getServices(
                                                "1",
                                                "",
                                                "1",
                                                limit.toString()
                                            )
                                        }
                                        else -> message?.let {
                                            UtilsFunctions.showToastError(response.message!!)
                                        }
                                    }
                                }
                                jobsViewModel!!.rejectOrderResponse().removeObservers(this)
                            }
                        })
                })
            .setNegativeButton(R.string.no, null)
            .setIcon(R.drawable.ic_alert)
            .show()
    }

    fun offlineStatus(status: String) {
        var input= OfflineStatusInput()
        input.status=status
        baseActivity.startProgressDialog()
        jobsViewModel!!.offlineStatus(input)
        jobsViewModel!!.offlineResponse().observe(this,
            Observer<OfflineStatusResponse> { response ->
                if(MyApplication.callApi) {
                    MyApplication.callApi =false
                    baseActivity.stopProgressDialog()
                    if (response != null) {
                        val message = response.message
                        when {
                            response.code == 200 -> {
                                UtilsFunctions.showToastSuccess(response.message!!)

                                SharedPrefClass().putObject(
                                    MyApplication.instance,
                                    ApiKeysConstants.OFFLINE_STATUS,
                                    mStatus
                                )

                            }
                            else -> message?.let {
                                UtilsFunctions.showToastError(response.message!!)
                            }
                        }
                    }
                    jobsViewModel!!.rejectOrderResponse().removeObservers(this)
                }
            })

    }


}