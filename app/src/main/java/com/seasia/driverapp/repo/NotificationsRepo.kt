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
import com.seasia.driverapp.model.NotificationResponse
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.model.driverjob.ServicesListResponse
import com.seasia.driverapp.model.driverjob.VendorListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.util.HashMap

class NotificationsRepo {
    private var data : MutableLiveData<NotificationResponse>? = null
    private var deleteData: MutableLiveData<CommonModel>
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        data = MutableLiveData()
        deleteData = MutableLiveData()
    }

    fun getNotificationsList(companyId: String) : MutableLiveData<NotificationResponse> {
        if (!TextUtils.isEmpty(companyId)) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val loginResponse = if (mResponse.body() != null)
                            gson.fromJson<NotificationResponse>(
                                "" + mResponse.body(),
                                NotificationResponse::class.java
                            )
                        else {
                            gson.fromJson<NotificationResponse>(
                                mResponse.errorBody()!!.charStream(),
                                NotificationResponse::class.java
                            )
                        }

                        data!!.postValue(loginResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        data!!.postValue(null)
                    }

                }, ApiClient.getApiInterface().getNotificationsList(companyId)
            )
        }
        return data!!
    }

    fun getDeleteNotificationResponse(): MutableLiveData<CommonModel> {
//        if (jsonObject != null) {
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
                        deleteData.postValue(logoutResponse)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        deleteData.postValue(null)
                    }

                }, ApiClient.getApiInterface().deleteAllNotifications()
            )
//        }
        return deleteData
    }
}