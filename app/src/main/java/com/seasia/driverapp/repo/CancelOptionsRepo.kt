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
import com.seasia.driverapp.model.CancelOptionsResponse
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.model.driverjob.ServicesListResponse
import com.seasia.driverapp.model.driverjob.VendorListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class CancelOptionsRepo {
    private var data : MutableLiveData<CancelOptionsResponse>? = null
    private var cancelData: MutableLiveData<CommonModel>
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        cancelData = MutableLiveData()
    }

    fun getCancelOptionsList(companyId: String) : MutableLiveData<CancelOptionsResponse> {
        if (!TextUtils.isEmpty(companyId)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<CancelOptionsResponse>(
                                "" + mResponse.body(),
                                CancelOptionsResponse::class.java
                            )
                        else {
                            gson.fromJson<CancelOptionsResponse>(
                                mResponse.errorBody()!!.charStream(),
                                CancelOptionsResponse::class.java
                            )
                        }

                        // Others option
                        var cancelOption = CancelOptionsResponse.Body()
                        cancelOption.reason = "Others"
                        cancelOption.id = "0"
                        loginResponse.body.add(cancelOption)

                        data!!.postValue(loginResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getCancelReasons(companyId)
            )
        }
        return data!!
    }

    fun getCancelResponse(companyId: String, jsonObject: JsonObject?): MutableLiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse: Response<JsonObject>) {
                        val logoutResponse = if (mResponse.body() != null)
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
                        cancelData.postValue(logoutResponse)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        cancelData.postValue(null)
                    }

                }, ApiClient.getApiInterface().cancelJob(companyId, jsonObject)
            )
        }
        return cancelData
    }
}