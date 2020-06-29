package com.seasia.driverapp.repo

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.api.ApiClient
import com.seasia.driverapp.api.ApiResponse
import com.seasia.driverapp.api.ApiService
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.model.CustomerFeedbackResponse
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.model.driverjob.ServicesListResponse
import com.seasia.driverapp.model.driverjob.VendorListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class CustomerFeedbackRepo {
    private var data : MutableLiveData<CustomerFeedbackResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
    }

    fun getCustFeedbackList(companyId: String) : MutableLiveData<CustomerFeedbackResponse> {
        if (!TextUtils.isEmpty(companyId)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CustomerFeedbackResponse>(
                                "" + mResponse.body(),
                                CustomerFeedbackResponse::class.java
                            )
                        else {
                            gson.fromJson<CustomerFeedbackResponse>(
                                mResponse.errorBody()!!.charStream(),
                                CustomerFeedbackResponse::class.java
                            )
                        }
                        data!!.postValue(loginResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getFeedbackList(companyId)
            )
        }
        return data!!
    }
}