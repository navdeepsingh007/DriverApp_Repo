package com.seasia.driverapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.model.CancelOptionsResponse
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.model.driverjob.ServicesListResponse
import com.seasia.driverapp.model.driverjob.VendorListResponse
import com.seasia.driverapp.repo.CancelOptionsRepo
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CancelVM : BaseViewModel() {
    private var cancelOptionsList = MutableLiveData<CancelOptionsResponse>()
    private var cancelJob = MutableLiveData<CommonModel>()
    private var cancelOptionsRepo = CancelOptionsRepo()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var orderId = ""

    init {
        val companyId = MyApplication.sharedPref.getPrefValue(
            MyApplication.instance,
            ApiKeysConstants.COMPANY_ID
        ) as String
        cancelOptionsList = cancelOptionsRepo.getCancelOptionsList(companyId)
    }

    fun cancelOptions() {
        if (UtilsFunctions.isNetworkConnected()) {
            val companyId = MyApplication.sharedPref.getPrefValue(
                MyApplication.instance,
                ApiKeysConstants.COMPANY_ID
            ) as String
            cancelOptionsList = cancelOptionsRepo.getCancelOptionsList(companyId)
            mIsUpdating.postValue(true)
        }
    }

    fun onJobCancel(reasonId: String, cancelReason: String): LiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val companyId = MyApplication.sharedPref.getPrefValue(
                MyApplication.instance,
                ApiKeysConstants.COMPANY_ID
            ) as String

            val jObj = JsonObject()
            jObj.addProperty(ApiKeysConstants.CANCEL_ORDER_ID, orderId)
            jObj.addProperty(ApiKeysConstants.CANCEL_REASON_ID, reasonId)
            jObj.addProperty(ApiKeysConstants.CANCEL_OTHER_REASON, cancelReason)
            cancelJob = cancelOptionsRepo.getCancelResponse(companyId, jObj)
            mIsUpdating.postValue(true)
        }
        return cancelJob
    }

    fun selectedOrderId(orderId: String) {
        this.orderId = orderId
    }

    fun getCancelOptionsList(): LiveData<CancelOptionsResponse> {
        return cancelOptionsList
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