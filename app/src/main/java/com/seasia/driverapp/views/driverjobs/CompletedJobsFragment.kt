package com.seasia.driverapp.views.driverjobs

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.adapters.JobHistoryAdapter
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.databinding.ActivityJobHistoryBinding
import com.seasia.driverapp.databinding.FragmentCompleteBinding
import com.seasia.driverapp.databinding.FragmentServicesBinding
import com.seasia.driverapp.model.OrderStatusResponse
//import com.seasia.driverapp.model.driverjob.ServicesListResponse
import com.seasia.driverapp.utils.BaseFragment
import com.seasia.driverapp.utils.pagenation.EndlessRecyclerViewScrollListenerImplementation
import com.seasia.driverapp.viewmodel.JobsViewModel
import com.uniongoods.adapters.CompletedJobsAdapter

class CompletedJobsFragment : BaseFragment(),    EndlessRecyclerViewScrollListenerImplementation.OnScrollPageChangeListener {
    private lateinit var binding: FragmentCompleteBinding
    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var companyId: String
    private var jobHistoryList = ArrayList<OrderStatusResponse.Body>()
    var endlessScrollListener: EndlessRecyclerViewScrollListenerImplementation? = null
    var rateListAdapter: JobHistoryAdapter?=null
    var count=1
    var limit: Int = 10
    var mPastVisibleItems = 0
    var mVisibleItemCount = 0
    var mTotalItemCount = 0
    var mLoadMoreViewCheck = true
    override fun getLayoutResId(): Int {
        return R.layout.fragment_complete
    }

    override fun initView() {
        binding = viewDataBinding as FragmentCompleteBinding
        jobsViewModel = ViewModelProvider(this).get(JobsViewModel::class.java)
        setJobHistoryAdapter()
//        initActiveJobsObserver()
        initCompletedJobsObserver()

        companyId = MyApplication.sharedPref.getPrefValue(
            requireActivity(),
            ApiKeysConstants.COMPANY_ID
        ) as String
//        jobsViewModel.activeJobs("1", companyId)
        if (UtilsFunctions.isNetworkConnected()) {
            startProgressDialog()
            jobsViewModel.completedJobs("3","",count.toString(), "10")

        }


    }


    private fun initActiveJobsObserver() {
        jobsViewModel.getActiveJobs().observe(this,
            Observer<OrderStatusResponse> { response ->
                if (response != null) {
                    val message = response.message
                    stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            if (!response.body.isEmpty()) {
                                // Start fetching completed jobs
                                jobsViewModel.completedJobs("3","", "1","10")

                                jobHistoryList.addAll(response.body)
                            } else {
                                message?.let {
//                                    UtilsFunctions.showToastError(it)
                                }
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

    private fun initCompletedJobsObserver() {
        jobsViewModel.getCompletedJobs().observe(this,
            Observer<OrderStatusResponse> { response ->
                if (response != null) {
                    val message = response.message
                    stopProgressDialog()
                    when {
                        response.code == 200 -> {
//                            if (!response.body.isEmpty()) {
                            mLoadMoreViewCheck = true
                            jobHistoryList.addAll(response.body)
                            rateListAdapter!!.setData(jobHistoryList)


                            if(jobHistoryList.size>0){
                                binding.tvNoRecord.visibility = View.GONE
                            } else{
                                binding.tvNoRecord.visibility = View.VISIBLE
                            }


                            // setJobHistoryAdapter()
/*                            }
                            else {
                                message?.let {
//                                    UtilsFunctions.showToastError(it)
                                }
                            }*/
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                            binding.tvNoRecord.visibility = View.VISIBLE
                        }
                    }
                } else {
                    stopProgressDialog()
                }
            })
    }

    private fun setJobHistoryAdapter() {
//        val options = ArrayList<String>()
//        options.add("Not available")
//        options.add("B")
//        options.add("C")
//        options.add("Others")

        // if (jobHistoryList.size > 0) {
        rateListAdapter = JobHistoryAdapter(this, jobHistoryList)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvJobHistory.layoutManager = linearLayoutManager
        binding.rvJobHistory.adapter = rateListAdapter
        binding.rvJobHistory.addOnScrollListener(object :
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
                                jobsViewModel.completedJobs("3","", count.toString(),"10")
                            }
                        }
                    }
                }


            }
        })


        binding.rvJobHistory.visibility = View.VISIBLE
//        }
//        else {
//            binding.rvJobHistory.visibility = View.GONE
//            binding.tvNoRecord.visibility = View.VISIBLE
//        }
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
    }
}