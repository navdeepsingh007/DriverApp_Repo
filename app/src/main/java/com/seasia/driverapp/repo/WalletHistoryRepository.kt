package com.seasia.driverapp.repo

import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.api.ApiClient
import com.seasia.driverapp.api.ApiResponse
import com.seasia.driverapp.api.ApiService
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.model.OfflineStatusInput
import com.seasia.driverapp.model.WalletInput
import com.seasia.driverapp.model.WalletResponse
import retrofit2.Response

class WalletHistoryRepository {


    private var walletData: MutableLiveData<WalletResponse>? = null
    private val gson = GsonBuilder().serializeNulls().create()

    init {
        walletData = MutableLiveData()
    }

    fun walletHistory(input: WalletInput): MutableLiveData<WalletResponse> {

        val mApiService = ApiService<JsonObject>()
        mApiService.get(
            object : ApiResponse<JsonObject> {
                override fun onResponse(mResponse: Response<JsonObject>) {
                    val loginResponse = if (mResponse.body() != null)
                        gson.fromJson<WalletResponse>(
                            "" + mResponse.body(),
                            WalletResponse::class.java
                        )
                    else {
                        gson.fromJson<WalletResponse>(
                            mResponse.errorBody()!!.charStream(),
                            WalletResponse::class.java
                        )
                    }
                    walletData!!.postValue(loginResponse)
                    MyApplication.callApi = true
                }

                override fun onError(mKey: String) {
                    UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
                    walletData!!.postValue(null)
                    MyApplication.callApi = true
                }

            }, ApiClient.getApiInterface().walletData(input)
        )

        return walletData!!
    }

}