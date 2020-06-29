package com.seasia.driverapp.views

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.FirebaseFunctions
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.ActivityLoginUserBinding
import com.seasia.driverapp.fcm.FcmUtils
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.viewmodel.LoginVM

class LoginUserActivity : BaseActivity() {
    private lateinit var loginUserBinding: ActivityLoginUserBinding
    private lateinit var loginViewModel: LoginVM

    override fun getLayoutId(): Int {
        return R.layout.activity_login_user
    }

    override fun initViews() {
//        loginUserBinding = DataBindingUtil.setContentView(this, R.layout.activity_login_user)
        loginUserBinding = viewDataBinding as ActivityLoginUserBinding

        val instanceId = FcmUtils.getInstanceId()
        MyApplication.sharedPref.putObject(this, ApiKeysConstants.DEVICE_TOKEN, instanceId)

//        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginViewModel = ViewModelProvider(this).get(LoginVM::class.java)
        loginUserBinding.loginViewModel = loginViewModel

//        loginUserBinding.etPhoneNo.setText("9992364445")     // Mohit
//        loginUserBinding.etPhoneNo.setText("7973015382")   // Naval

        initLoginResponseObserver()
        initLoadingObserver()
        initErrorMsgObserver()
        checkAndRequestPermissions()

        FcmUtils.isGooglePlayServicesAvailable(this)
    }

    fun initLoginResponseObserver() {
        loginViewModel.getLoginData().observe(this, Observer { response ->
            stopProgressDialog()

            if (response != null) {
                val message = response.message

                if (response.code == 200) {
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        GlobalConstants.ACCESS_TOKEN,
                        response.body!!.sessionToken
                    )
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        ApiKeysConstants.PHONE_NO,
                        response.body!!.phoneNumber
                    )
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        ApiKeysConstants.COUNTRY_CODE,
                        response.body!!.countryCode
                    )
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        ApiKeysConstants.COMPANY_ID,
                        response.body!!.companyId
                    )
                    // User detail
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        GlobalConstants.USERID,
                        response.body!!.id
                    )
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        GlobalConstants.USER_IMAGE,
                        response.body!!.image
                    )
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        GlobalConstants.USER_BANNER,
                        response.body!!.coverImage
                    )
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        GlobalConstants.USER_NAME,
                        "${response.body!!.firstName} ${response.body!!.lastName}"
                    )
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        GlobalConstants.USER_EMAIL,
                        response.body!!.email
                    )

                    val jObj = JsonObject()
                    jObj.addProperty(
                        "countryCode",
                        "+${response.body!!.countryCode}"
                    )
                    jObj.addProperty(
                        "phoneNumber",
                        response.body!!.phoneNumber
                    )

                    if (message.equals("Login Successfully")) {
                        // Temporary code below
                        startActivity(Intent(this, DashboardNewActivity::class.java))
//                        showToastSuccess(message)
                        SharedPrefClass().putObject(
                            MyApplication.instance,
                            "isLogin",
                            true
                        )

                        // Final production, 1 liner
//                        FirebaseFunctions.sendOTP("login", jObj, this)

//                        loginUserBinding.etPhoneNo.setText("")
                    } else {
                        showToastError(message)
                    }
                } else {
/*                    val jObj = JsonObject()
                    jObj.addProperty(
                        "countryCode",
                        "+${loginUserBinding.btnCcp.selectedCountryCode}"
                    )
                    jObj.addProperty(
                        "phoneNumber",
                        loginUserBinding.etPhoneNo.text.toString()
                    )
                    FirebaseFunctions.sendOTP("login", jObj, this)*/

                    showToastError(message)
                }
            } else {
                showToastError(MyApplication.instance.getString(R.string.internal_server_error))
            }
        })
    }

    fun initLoadingObserver() {
        loginViewModel.isLoading().observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })
    }

    fun initErrorMsgObserver() {
        loginViewModel.getErrorMsg().observe(this, Observer<String> {
            when (it) {
                "internetConnection" -> {
                    UtilsFunctions.showToastWarning(MyApplication.instance.getString(R.string.internet_connection))
                }
                "invalidNumber" -> {
                    UtilsFunctions.showToastWarning(MyApplication.instance.getString(R.string.invalid_phone_number))
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()

        FcmUtils.isGooglePlayServicesAvailable(this)
        FcmUtils.getInstanceId()
    }
}