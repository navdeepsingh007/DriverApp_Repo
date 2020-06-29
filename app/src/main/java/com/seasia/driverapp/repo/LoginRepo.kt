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
import com.seasia.driverapp.model.LoginResponse
import retrofit2.Response

class LoginRepo {
    private var loginData: MutableLiveData<LoginResponse>
    private var logoutData: MutableLiveData<CommonModel>
    private val gson = MyApplication.gson

    init {
        loginData = MutableLiveData()
        logoutData = MutableLiveData()
    }

    fun getLoginResponse(jsonObject: JsonObject?): LiveData<LoginResponse> {
        if (jsonObject != null) {
            ApiService<JsonObject>().get(object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse: Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null) {
                        gson.fromJson<LoginResponse>(
                            "" + mResponse.body(),
                            LoginResponse::class.java
                        )
                    } else {
                        gson.fromJson<LoginResponse>(
                            mResponse.errorBody()!!.charStream(),
                            LoginResponse::class.java
                        )
                    }
                    loginData.postValue(loginResponse)
                }

                override fun onError(mKey: String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    loginData.postValue(null)
                }
            }, ApiClient.getApiInterface().callLogin(jsonObject))
        }
        return loginData
    }

    fun getLogoutResponse(jsonObject: JsonObject?): LiveData<CommonModel> {
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
                        logoutData.postValue(logoutResponse)
                    }

                    override fun onError(mKey: String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        logoutData.postValue(null)
                    }

                }, ApiClient.getApiInterface().callLogout()
            )
        }
        return logoutData
    }

    /*fun getLogoutResponse(jsonObject: JsonObject?): LiveData<CommonModel> {
        if (jsonObject != null) {
            val mApiService = ApiService<JsonObject>()
            mApiService.get(
                object : ApiResponse<JsonObject> {
                    override fun onResponse(mResponse : Response<JsonObject>) {
                        val logoutResponse = if (mResponse.body() != null)
                            gson.fromJson<CommonModel>("" + mResponse.body(), CommonModel::class.java)
                        else {
                            gson.fromJson<CommonModel>(
                                mResponse.errorBody()!!.charStream(),
                                CommonModel::class.java
                            )
                        }
                        logoutData.postValue(logoutResponse)
                    }

                    override fun onError(mKey : String) {
                        UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                        logoutData.postValue(null)
                    }

                }, ApiClient.getApiInterface().callLogout(jsonObject)
            )
        }
        return logoutData
    }*/
}