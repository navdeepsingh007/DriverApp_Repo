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
import com.seasia.driverapp.model.NotificationResponse
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.model.driverjob.ServicesListResponse
import com.seasia.driverapp.model.driverjob.VendorListResponse
import com.seasia.driverapp.repo.CancelOptionsRepo
import com.seasia.driverapp.repo.NotificationsRepo
import okhttp3.MultipartBody
import okhttp3.RequestBody

class NotificationlVM : BaseViewModel() {
    private var notificationsList = MutableLiveData<NotificationResponse>()
    private var notificationDelete = MutableLiveData<CommonModel>()
    private var notificationsRepo = NotificationsRepo()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var orderId = ""

    init {
        val companyId = MyApplication.sharedPref.getPrefValue(
            MyApplication.instance,
            ApiKeysConstants.COMPANY_ID
        ) as String
        notificationsList = notificationsRepo.getNotificationsList(companyId)
    }
    

    fun onNotificationDelete(): LiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected()) {
//            val companyId = MyApplication.sharedPref.getPrefValue(
//                MyApplication.instance,
//                ApiKeysConstants.COMPANY_ID
//            ) as String

            notificationDelete = notificationsRepo.getDeleteNotificationResponse()
            mIsUpdating.postValue(true)
        }
        return notificationDelete
    }

    fun selectedOrderId(orderId: String) {
        this.orderId = orderId
    }

    fun getNotificationsList(): LiveData<NotificationResponse> {
        return notificationsList
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