package com.seasia.driverapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.model.*
import com.seasia.driverapp.model.driverjob.ServicesListResponse
import com.seasia.driverapp.model.driverjob.VendorListResponse
import com.seasia.driverapp.repo.ServicesRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class JobsViewModel : BaseViewModel() {
    private lateinit var serviceslist: LiveData<OrderStatusResponse>
    private var activeJobsList = MutableLiveData<OrderStatusResponse>()
    private var completedJobsList = MutableLiveData<OrderStatusResponse>()
    private var acceptOrder = MutableLiveData<AcceptOrderResponse>()
    private var rejectOrder = MutableLiveData<RejectOrderResponse>()
    private var offlineStatus = MutableLiveData<OfflineStatusResponse>()
    private var updateService = MutableLiveData<CommonModel>()
    private var servicesRepository = ServicesRepository()
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var vendorList = MutableLiveData<VendorListResponse>()
    private var companyId = ""

    init {
        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            serviceslist = servicesRepository.getServicesList("", "","","")
            activeJobsList = servicesRepository.getServicesList("", "","","")
            completedJobsList = servicesRepository.getServicesList("", "","","")
            updateService = servicesRepository.updateService(null, "")
//            vendorList = servicesRepository.getVendorList()

            companyId = MyApplication.sharedPref.getPrefValue(
                MyApplication.instance,
                ApiKeysConstants.COMPANY_ID
            ) as String
        }
    }

    fun getServicesList(): LiveData<OrderStatusResponse> {
        return serviceslist
    }

    fun updateServiceStatus(): LiveData<CommonModel> {
        return updateService
    }

    fun getVendorList(): LiveData<VendorListResponse> {
        return vendorList
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

    fun getServices(jobStatus: String,progressStatus: String, page: String,limit:String) {
        if (UtilsFunctions.isNetworkConnected()) {
            serviceslist = servicesRepository.getServicesList(jobStatus,progressStatus, page,limit)

            Log.d("JobsVM ----> ", serviceslist.value.toString())
            mIsUpdating.postValue(true)
        }
    }

    fun activeJobs(progressStatus: String, companyId: String) {
        if (UtilsFunctions.isNetworkConnected()) {
            activeJobsList = servicesRepository.getServicesList(progressStatus,"", companyId,"")
            mIsUpdating.postValue(true)
        }
    }

    fun completedJobs(jobStatus: String,progressStatus: String, page: String,limit:String) {
        if (UtilsFunctions.isNetworkConnected()) {
            completedJobsList = servicesRepository.getServicesList(jobStatus,progressStatus, page,limit)
            mIsUpdating.postValue(true)
        }
    }



    fun acceptOrderJobs(input:AcceptOrderInput) {
        if (UtilsFunctions.isNetworkConnected()) {
            acceptOrder = servicesRepository.acceptOrder(input)
            mIsUpdating.postValue(true)
        }
    }

    fun acceptOrderResponse(): LiveData<AcceptOrderResponse> {
        return acceptOrder
    }


    fun rejectOrderJobs(input:RejectOrderInput) {
        if (UtilsFunctions.isNetworkConnected()) {
            rejectOrder = servicesRepository.rejectOrder(input)
            mIsUpdating.postValue(true)
        }
    }

    fun rejectOrderResponse(): LiveData<RejectOrderResponse> {
        return rejectOrder
    }
 fun offlineStatus(input:OfflineStatusInput) {
        if (UtilsFunctions.isNetworkConnected()) {
            offlineStatus = servicesRepository.offlineStatus(input)
            mIsUpdating.postValue(true)
        }
    }

    fun offlineResponse(): LiveData<OfflineStatusResponse> {
        return offlineStatus
    }




    fun getActiveJobs(): LiveData<OrderStatusResponse> {
        return activeJobsList
    }

    fun getCompletedJobs(): LiveData<OrderStatusResponse> {
        return completedJobsList
    }


    fun updateOrderStatus(orderId: String, orderStatus: String): LiveData<CommonModel> {
        if (UtilsFunctions.isNetworkConnected()) {
            val jObj = JsonObject()
            jObj.addProperty(ApiKeysConstants.ORDER_ID, orderId)
            jObj.addProperty(ApiKeysConstants.ORDER_STATUS, orderStatus)

            Log.d("JobsViewModel =====> ", "orderId: $orderId, orderStatus: $orderStatus")

            updateService = servicesRepository.updateService(jObj, companyId)
            mIsUpdating.postValue(true)
        } else {
            UtilsFunctions.showToastWarning(MyApplication.instance.getString(R.string.internet_connection))
        }
        return updateService
    }
}