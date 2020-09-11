package com.seasia.driverapp.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.model.WalletInput
import com.seasia.driverapp.model.WalletResponse
import com.seasia.driverapp.repo.WalletHistoryRepository

class WalletHistoryViewModel : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()
    private var response = MutableLiveData<WalletResponse>()
    private var walletRepo = WalletHistoryRepository()
    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return btnClick
    }

    override fun clickListener(v: View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun walletInputData(input: WalletInput) {
        if (UtilsFunctions.isNetworkConnected()) {
            response = walletRepo.walletHistory(input)
            mIsUpdating.postValue(true)
        }
    }

    fun walletResponse(): LiveData<WalletResponse> {
        return response
    }

}