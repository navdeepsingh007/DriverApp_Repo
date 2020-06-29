package com.seasia.isms.auth

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.views.OTPVerificationActivity
import java.util.concurrent.TimeUnit

class OtpFirebaseActivity {
    private val TAG = "OtpFirebaseActivity"
    private var mJsonObject: JsonObject? = null
    private var mBaseActivity: BaseActivity? = null
    private var otpAction = ""
    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            mBaseActivity!!.stopProgressDialog()
            //Getting the code sent by SMS
            val code = phoneAuthCredential.smsCode
            if (code != null) {
                //verifying the code
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onVerificationFailed(e: FirebaseException) {
            Log.d(TAG, e.toString())
            try {
                if ((e as FirebaseAuthException).errorCode == "ERROR_INVALID_PHONE_NUMBER")
                    UtilsFunctions.showToastError(
                        e.message.toString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()[0]
                    )
                else
                    UtilsFunctions.showToastError(mBaseActivity!!.getString(R.string.server_not_reached))
            } catch (e1: java.lang.Exception) {
                UtilsFunctions.showToastError(
                    e.localizedMessage
                )
            }
            mBaseActivity!!.stopProgressDialog()
        }

        override fun onCodeSent(
            s: String,
            forceResendingToken: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(s, forceResendingToken)
            mBaseActivity!!.stopProgressDialog()
            SharedPrefClass().putObject(
                mBaseActivity!!,
                GlobalConstants.OTP_VERIFICATION_ID,
                s
            )
            //storing the verification id that is sent to the user
            //if (otpAction == "signup" || otpAction == "forgot") {
            val intent = Intent(mBaseActivity, OTPVerificationActivity::class.java)
            intent.putExtra("data", mJsonObject!!.toString())
            intent.putExtra("action", otpAction)
            mBaseActivity!!.startActivity(intent)
            //  }
            if (otpAction == "resend") {
                UtilsFunctions.showToastSuccess(mBaseActivity!!.getString(R.string.resend_otp_message))
            }

        }
    }

    fun otpValidation(
        otpAction: String,
        phoneNumber: String,
        mBaseActivity: BaseActivity,
        mJsonObject: JsonObject
    ) {
        this.mBaseActivity = mBaseActivity
        this.mJsonObject = mJsonObject
        this.otpAction = otpAction
        if (UtilsFunctions.isNetworkConnected())
            sendVerificationCode(phoneNumber)
    }

    private fun sendVerificationCode(mobile: String) {
//        val phone = "+" +
        val phone = mJsonObject!!.get("countryCode").toString().replace("\"", "") + mobile

//        val phone = "+91" + mobile

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phone,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallbacks
        )
//        mBaseActivity!!.startProgressDialog()
    }
}