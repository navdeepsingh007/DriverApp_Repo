package com.seasia.driverapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.model.CustomerFeedbackResponse
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.model.driverjob.ServicesListResponse
import com.seasia.driverapp.model.driverjob.VendorListResponse
import com.seasia.driverapp.repo.CustomerFeedbackRepo
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CustFeedbackVM : BaseViewModel() {
    private var custFeedbackList = MutableLiveData<CustomerFeedbackResponse>()
    private var custFeedbackRepo = CustomerFeedbackRepo()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    init {
        custFeedbackList = custFeedbackRepo.getCustFeedbackList("")
    }

    fun customerFeedbacks(): LiveData<CustomerFeedbackResponse> {
        if (UtilsFunctions.isNetworkConnected()) {
            val companyId = MyApplication.sharedPref.getPrefValue(
                MyApplication.instance,
                ApiKeysConstants.COMPANY_ID
            ) as String
            custFeedbackList = custFeedbackRepo.getCustFeedbackList(companyId)
            mIsUpdating.postValue(true)
        }
        return custFeedbackList
    }

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return btnClick
    }

    override fun clickListener(v: View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }
}