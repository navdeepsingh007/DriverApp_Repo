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
import com.seasia.driverapp.model.LoginResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class ProfileRepo {
    private val gson = MyApplication.gson
    private var getProfileData: MutableLiveData<LoginResponse>
    private var updateProfileData: MutableLiveData<LoginResponse>

    init {
        getProfileData = MutableLiveData()
        updateProfileData = MutableLiveData()
    }

    fun getProfileResponse(jsonObject: JsonObject?): LiveData<LoginResponse> {
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
                    getProfileData.postValue(loginResponse)
                }

                override fun onError(mKey: String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    getProfileData.postValue(null)
                }
            }, ApiClient.getApiInterface().getProfileData())
        }
        return getProfileData
    }

    fun updateProfileResponse(map: Map<String, RequestBody>?,
                              file: MultipartBody.Part?,
                              proof: MultipartBody.Part?,
                              banner: MultipartBody.Part?
    ): LiveData<LoginResponse> {
        if (map != null) {
            val rawfile = file!!
            val rawProof = proof!!
            val rawBanner = banner!!
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
                    updateProfileData.postValue(loginResponse)
                }

                override fun onError(mKey: String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    updateProfileData.postValue(null)
                }
            }, ApiClient.getApiInterface().updateProfileData(map, rawfile, rawProof, rawBanner))
        }
        return updateProfileData
    }
}