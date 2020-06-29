package com.seasia.driverapp.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.OrderDetailResponse
import com.seasia.driverapp.repo.DriverJourneyRepo
import com.seasia.driverapp.repo.JobDetailsRepo
import com.seasia.driverapp.repo.LoginRepo

class DriverJourneyVM: BaseViewModel() {
    private var driverJourneyRepo: DriverJourneyRepo
    private var jobDetailsResponse: LiveData<OrderDetailResponse>
    private var isLoading: MutableLiveData<Boolean>
    private var isClicked: MutableLiveData<Boolean>
    private var onErrorMsg: MutableLiveData<String>
    private val btnClick = MutableLiveData<String>()
    private var updateJobStatus = MutableLiveData<CommonModel>()
    private var companyId = ""

    init {
        driverJourneyRepo = DriverJourneyRepo()
        isLoading = MutableLiveData()
        isClicked = MutableLiveData()
        onErrorMsg = MutableLiveData()
        isClicked.postValue(false)
        jobDetailsResponse = driverJourneyRepo.getJobDetails(null, "")
        updateJobStatus = driverJourneyRepo.updateJobStatus(null, "")
        isLoading.postValue(false)

        companyId = MyApplication.sharedPref.getPrefValue(
            MyApplication.instance,
            ApiKeysConstants.COMPANY_ID
        ) as String
    }


    override fun isLoading(): LiveData<Boolean> {
        return isLoading
    }

    override fun isClick(): LiveData<String> {
        return btnClick
    }

    override fun clickListener(v: View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun onJobDtlClick(orderID: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            jobDetailsResponse = driverJourneyRepo.getJobDetails(orderID, companyId)
            isLoading.postValue(true)
        } else {
            UtilsFunctions.showToastWarning(MyApplication.instance.getString(R.string.internet_connection))
        }
    }

    fun getJobDtlData(orderID: String): LiveData<OrderDetailResponse> {
        onJobDtlClick(orderID)
        return jobDetailsResponse
    }


    fun updateJobStatus(orderId: String, orderStatus: String): LiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val jObj = JsonObject()
            jObj.addProperty(ApiKeysConstants.ORDER_ID, orderId)
            jObj.addProperty(ApiKeysConstants.ORDER_STATUS, orderStatus)

            Log.d("DriverJourneyVM =====> ", "orderId: $orderId, orderStatus: $orderStatus")

            updateJobStatus = driverJourneyRepo.updateJobStatus(jObj, companyId)
            isLoading.postValue(true)
        } else {
            UtilsFunctions.showToastWarning(MyApplication.instance.getString(R.string.internet_connection))
        }
        return updateJobStatus
    }
}