package com.seasia.driverapp.viewmodel

import android.view.View
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.hbb20.CountryCodePicker.PhoneNumberValidityChangeListener
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.fcm.FcmUtils
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.LoginResponse
import com.seasia.driverapp.repo.LoginRepo
import kotlinx.android.synthetic.main.layout_toast.view.*


class LoginVM : BaseViewModel() {
    private var loginRepo: LoginRepo
    private var loginResponse: LiveData<LoginResponse>
    private var logoutResponse: LiveData<CommonModel>
    private var isLoading: MutableLiveData<Boolean>
    private var isClicked: MutableLiveData<Boolean>
    private var onErrorMsg: MutableLiveData<String>

    init {
        loginRepo = LoginRepo()
        loginResponse = loginRepo.getLoginResponse(null)
        logoutResponse = loginRepo.getLogoutResponse(null)
        isLoading = MutableLiveData()
        isClicked = MutableLiveData()
        onErrorMsg = MutableLiveData()
        isClicked.postValue(false)
    }

    fun onSubmitClick(v: View, ccp: CountryCodePicker, phoneNo: EditText) {
//        ccp.registerCarrierNumberEditText(phoneNo)
//        ccp.setPhoneNumberValidityChangeListener(PhoneNumberValidityChangeListener {
//            // your code
//        })
        val phone = phoneNo.text.toString()
        if (phone.length < 10) {
            UtilsFunctions.showToastWarning(MyApplication.instance.getString(R.string.invalid_phone_number))
            return
        }

//        if (ccp.isValidFullNumber) {
        if (UtilsFunctions.isNetworkConnected()) {
            val jObj = JsonObject()
            jObj.addProperty(ApiKeysConstants.PHONE_NO, phone)
            jObj.addProperty(ApiKeysConstants.COUNTRY_CODE, ccp.selectedCountryCode)
//            jObj.addProperty(ApiKeysConstants.DEVICE_TOKEN, UtilsFunctions.getAndroidID())

            val fcmToken = MyApplication.sharedPref.getPrefValue(
                MyApplication.instance,
                ApiKeysConstants.DEVICE_TOKEN
            ) as String?
            jObj.addProperty(ApiKeysConstants.DEVICE_TOKEN, fcmToken ?: "")
            jObj.addProperty(ApiKeysConstants.PLATFORM, GlobalConstants.PLATFORM)

            loginResponse = loginRepo.getLoginResponse(jObj)
            isLoading.postValue(true)
        } else {
            onErrorMsg.value = "internetConnection"
        }
//    } else
//    {
//        onErrorMsg.value = "invalidNumber"
//    }
    }

    fun onLogoutClick() {
        if (UtilsFunctions.isNetworkConnected()) {
            logoutResponse = loginRepo.getLogoutResponse(JsonObject())
            isLoading.postValue(true)
        } else {
            UtilsFunctions.showToastWarning(MyApplication.instance.getString(R.string.internet_connection))
        }
    }

    fun getLogoutData(): LiveData<CommonModel> {
        onLogoutClick()
        return logoutResponse
    }

    fun getLoginData(): LiveData<LoginResponse> {
        return loginResponse
    }

    fun getErrorMsg(): LiveData<String> {
        return onErrorMsg
    }

    override fun isLoading(): LiveData<Boolean> {
        return isLoading
    }

    override fun isClick(): LiveData<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clickListener(v: View) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}