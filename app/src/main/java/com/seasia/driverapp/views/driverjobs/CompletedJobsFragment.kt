package com.seasia.driverapp.views.driverjobs

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.databinding.FragmentServicesBinding
import com.seasia.driverapp.model.OrderStatusResponse
//import com.seasia.driverapp.model.driverjob.ServicesListResponse
import com.seasia.driverapp.utils.BaseFragment
import com.seasia.driverapp.utils.pagenation.EndlessRecyclerViewScrollListenerImplementation
import com.seasia.driverapp.viewmodel.JobsViewModel
import com.uniongoods.adapters.CompletedJobsAdapter

class CompletedJobsFragment : BaseFragment(),    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private lateinit var jobsViewModel: JobsViewModel
    private var servicesList = ArrayList<OrderStatusResponse.Body>()
    private lateinit var fragmentServicesBinding: FragmentServicesBinding
    var pageSize: Int = 10
    var endlessScrollListener: EndlessRecyclerViewScrollListenerImplementation? = null
    var servicesListAdapter :CompletedJobsAdapter?=null

    private val mJsonObject = JsonObject()

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

        if (UtilsFunctions.isNetworkConnected()) {
           // val companyId = MyApplication.sharedPref.getPrefValue(requireContext(), ApiKeysConstants.COMPANY_ID) as String
            jobsViewModel.getServices("3", "","0",pageSize.toString())
        }
        //   servicesViewModel.getServicesList()
        jobsViewModel.getServicesList().observe(this,
            Observer<OrderStatusResponse> { response ->
                baseActivity.stopProgressDialog()

                if (response != null) {
                    val message = response.message
                    baseActivity.stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            if (!response.body.isEmpty()) {
                                servicesList.addAll(response.body)
                                fragmentServicesBinding.rvServices.visibility = View.VISIBLE
                                fragmentServicesBinding.tvNoRecord.visibility = View.GONE
                                initRecyclerView()
                            } else {
                                message.let {
//                                    UtilsFunctions.showToastError(it)
                                }
                            }
                        }
                        else -> message.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                }
            })
        // initRecyclerView()
    }

    private fun initRecyclerView() {
         servicesListAdapter =
            CompletedJobsAdapter(
                this@CompletedJobsFragment,
                servicesList,
                requireActivity()
            )
        val linearLayoutManager = LinearLayoutManager(this.baseActivity)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        fragmentServicesBinding.rvServices.layoutManager = linearLayoutManager

//        fragmentServicesBinding.rvServices.addOnScrollListener(object :
//            RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//
//            }
//        })


        endlessScrollListener = EndlessRecyclerViewScrollListenerImplementation(
            linearLayoutManager, this
        )
        endlessScrollListener?.setmLayoutManager(linearLayoutManager)
        fragmentServicesBinding.rvServices.addOnScrollListener(endlessScrollListener!!)
        fragmentServicesBinding.rvServices.adapter = servicesListAdapter
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
        if (UtilsFunctions.isNetworkConnected()) {
            jobsViewModel.getServices("3","",  servicesListAdapter!!.itemCount.toString(),pageSize.toString())
        }
    }
}