package com.seasia.driverapp.repo

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.api.ApiClient
import com.seasia.driverapp.api.ApiResponse
import com.seasia.driverapp.api.ApiService
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.model.driverjob.ServicesListResponse
import com.seasia.driverapp.model.driverjob.VendorListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class ServicesRepository {
    private var data : MutableLiveData<OrderStatusResponse>? = null
    private var data1 : MutableLiveData<CommonModel>? = null
    private val gson = GsonBuilder().serializeNulls().create()
    private var data2 : MutableLiveData<VendorListResponse>? = null

    init {
        data = MutableLiveData()
        data1 = MutableLiveData()
        data2 = MutableLiveData()
    }

    fun getServicesList(serviceType : String, companyId: String) : MutableLiveData<OrderStatusResponse> {
        if (!TextUtils.isEmpty(serviceType)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<OrderStatusResponse>(
                                "" + mResponse.body(),
                                OrderStatusResponse::class.java
                            )
                        else {
                            gson.fromJson<OrderStatusResponse>(
                                mResponse.errorBody()!!.charStream(),
                                OrderStatusResponse::class.java
                            )
                        }
                        data!!.postValue(loginResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getAssignedOrCompletedJobs(serviceType, companyId)
            )
        }
        return data!!
    }

    fun updateService(jsonObject: JsonObject?, companyId: String) : MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CommonModel>(
                                "" + mResponse.body(),
                                CommonModel::class.java
                            )
                        else {
                            gson.fromJson<CommonModel>(
                                mResponse.errorBody()!!.charStream(),
                                CommonModel::class.java
                            )
                        }
                        data1!!.postValue(loginResponse)

                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data1!!.postValue(null)

                    }

                }, ApiClient.getApiInterface().updateJobStatus(jsonObject, companyId)
            )
        }
        return data1!!
    }

    fun getVendorList() : MutableLiveData<VendorListResponse> {
        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse : Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<VendorListResponse>(
                            "" + mResponse.body(),
                            VendorListResponse::class.java
                        )
                    else {
                        gson.fromJson<VendorListResponse>(
                            mResponse.errorBody()!!.charStream(),
                            VendorListResponse::class.java
                        )
                    }
                    data2!!.postValue(loginResponse)
                }

                override fun onError(mKey : String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    data2!!.postValue(null)
                }

            }, ApiClient.getApiInterface().getVendorList(1,1000)
        )
        return data2!!
    }
}