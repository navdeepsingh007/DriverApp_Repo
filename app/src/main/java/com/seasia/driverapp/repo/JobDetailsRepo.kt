package com.seasia.driverapp.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.api.ApiClient
import com.seasia.driverapp.api.ApiResponse
import com.seasia.driverapp.api.ApiService
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.OrderDetailResponse
import retrofit2.Response

class JobDetailsRepo {

    private var jobDetailsData: MutableLiveData<OrderDetailResponse>
    private val gson = MyApplication.gson
    private var data1 : MutableLiveData<CommonModel>? = null

    init {
        jobDetailsData = MutableLiveData()
        data1 = MutableLiveData()
    }


    fun getJobDetails(orderId: String?, companyId: String): LiveData<OrderDetailResponse> {
        if (orderId != null) {
            ApiService<JsonObject>().get(object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse: Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null) {
                        gson.fromJson<OrderDetailResponse>(
                            "" + mResponse.body(),
                            OrderDetailResponse::class.java
                        )
                    } else {
                        gson.fromJson<OrderDetailResponse>(
                            mResponse.errorBody()!!.charStream(),
                            OrderDetailResponse::class.java
                        )
                    }
                    jobDetailsData.postValue(loginResponse)
                }

                override fun onError(mKey: String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    jobDetailsData.postValue(null)
                }
            }, ApiClient.getApiInterface().getOrderDetail(orderId, companyId))
        }
        return jobDetailsData
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
}