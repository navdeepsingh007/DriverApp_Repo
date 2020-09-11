package com.seasia.driverapp.views

import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seasia.driverapp.R
import com.seasia.driverapp.adapters.EarningsAdapter
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.databinding.ActivityEarningsBinding
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.viewmodel.JobsViewModel

class DriverEarningsActivity : BaseActivity() {

    private lateinit var binding: ActivityEarningsBinding
    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var companyId: String
    private var earningsList = ArrayList<OrderStatusResponse.Body>()

    override fun getLayoutId(): Int {
        return R.layout.activity_earnings
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityEarningsBinding
        jobsViewModel = ViewModelProvider(this).get(JobsViewModel::class.java)

        setEarningsAdapter()
        initCompletedJobsObserver()

        companyId = MyApplication.sharedPref.getPrefValue(
            this,
            ApiKeysConstants.COMPANY_ID
        ) as String
//        jobsViewModel.activeJobs("1", companyId)
//        jobsViewModel.completedJobs("5", companyId)

        initToolbar()
    }

    private fun initToolbar() {
        binding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.earnings)
        binding.commonToolBar.imgToolbarText.setTextColor(
            resources.getColor(R.color.colorBlue)
        )
    }

    private fun initCompletedJobsObserver() {
        jobsViewModel.getCompletedJobs().observe(this,
            Observer<OrderStatusResponse> { response ->
                if (response != null) {
                    val message = response.message
                    stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            if (!response.body.isEmpty()) {
                                earningsList.addAll(response.body)

                                setEarningsAdapter()
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

    private fun setEarningsAdapter() {
//        if (earningsList.size > 0) {
        val earningsAdapter = EarningsAdapter(this, earningsList)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.rvJobHistory.layoutManager = linearLayoutManager
        binding.rvJobHistory.adapter = earningsAdapter

        binding.rvJobHistory.visibility = View.VISIBLE
        binding.tvNoRecord.visibility = View.GONE
//        } else {
//            binding.rvJobHistory.visibility = View.GONE
//            binding.tvNoRecord.visibility = View.VISIBLE
//        }
    }
}