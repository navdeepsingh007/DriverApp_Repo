package com.seasia.driverapp.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.goodiebag.pinview.Pinview
import com.goodiebag.pinview.Pinview.PinViewEventListener


class OTPVerificationVM : BaseViewModel() {

    override fun isLoading(): LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick(): LiveData<String> {
        return btnClick
    }

    override fun clickListener(v: View) {
        btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    private val mIsUpdating = MutableLiveData<Boolean>()
    private val btnClick = MutableLiveData<String>()

    val loading: LiveData<Boolean>
        get() = mIsUpdating

    fun onOtpEnteredListener(pinview: Pinview) {
        pinview.setPinViewEventListener(PinViewEventListener { v, fromUser ->
            if (pinview.pinLength == 6) {
                btnClick.value = v.resources.getResourceName(v.id).split("/")[1]
            }
        })
    }
}