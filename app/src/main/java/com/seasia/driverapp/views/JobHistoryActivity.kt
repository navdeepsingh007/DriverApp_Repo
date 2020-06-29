package com.seasia.driverapp.views

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seasia.driverapp.R
import com.seasia.driverapp.adapters.JobHistoryAdapter
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.databinding.ActivityJobHistoryBinding
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.viewmodel.JobsViewModel

class JobHistoryActivity : BaseActivity() {

    private lateinit var binding: ActivityJobHistoryBinding
    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var companyId: String
    private var jobHistoryList = ArrayList<OrderStatusResponse.Body>()

    override fun getLayoutId(): Int {
        return R.layout.activity_job_history
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityJobHistoryBinding
        jobsViewModel = ViewModelProvider(this).get(JobsViewModel::class.java)

//        initActiveJobsObserver()
        initCompletedJobsObserver()

        companyId = MyApplication.sharedPref.getPrefValue(
            this,
            ApiKeysConstants.COMPANY_ID
        ) as String
//        jobsViewModel.activeJobs("1", companyId)
        jobsViewModel.completedJobs("5", companyId)

        initToolbar()
    }

    private fun initToolbar() {
        binding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.job_history)
        binding.commonToolBar.imgToolbarText.setTextColor(
            resources.getColor(R.color.colorBlue)
        )
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
                                jobsViewModel.completedJobs("5", companyId)

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
                                jobHistoryList.addAll(response.body)

                                setJobHistoryAdapter()
/*                            }
                            else {
                                message?.let {
//                                    UtilsFunctions.showToastError(it)
                                }
                            }*/
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

    private fun setJobHistoryAdapter() {
//        val options = ArrayList<String>()
//        options.add("Not available")
//        options.add("B")
//        options.add("C")
//        options.add("Others")

        if (jobHistoryList.size > 0) {
            val rateListAdapter = JobHistoryAdapter(this, jobHistoryList)
            val linearLayoutManager = LinearLayoutManager(this)
            linearLayoutManager.orientation = RecyclerView.VERTICAL
            binding.rvJobHistory.layoutManager = linearLayoutManager
            binding.rvJobHistory.adapter = rateListAdapter

            binding.rvJobHistory.visibility = View.VISIBLE
            binding.tvNoRecord.visibility = View.GONE
        } else {
            binding.rvJobHistory.visibility = View.GONE
            binding.tvNoRecord.visibility = View.VISIBLE
        }
    }
}