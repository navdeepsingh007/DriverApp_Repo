package com.seasia.driverapp.views

import android.annotation.TargetApi
import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.ActivityOtpVerificationBinding
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.viewmodel.OTPVerificationVM
import com.seasia.isms.auth.FirebaseFunctions
import com.seasia.isms.auth.OtpReceivedInterface
import org.json.JSONException
import org.json.JSONObject

class OTPVerificationActivity : BaseActivity(),
    OtpReceivedInterface, GoogleApiClient.OnConnectionFailedListener {
    override fun onConnectionFailed(p0: ConnectionResult) {
        //To change body of created functions use File | Settings | File Templates.
    }

    override fun onOtpReceived(otp: String?) {
        activityOtpVerificationBinding.pinview.value = otp
    }

    override fun onOtpTimeout() {
        //To change body of created functions use File | Settings | File Templates.
    }

    private lateinit var otpVerificationVM: OTPVerificationVM
    private lateinit var activityOtpVerificationBinding: ActivityOtpVerificationBinding
    private var mAuth = FirebaseAuth.getInstance()
    private var mJsonObject = JSONObject()
    private var action = "login"
    private var mVerificationId: String? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var RESOLVE_HINT = 2

    override fun getLayoutId(): Int {
        return R.layout.activity_otp_verification
    }

    public override fun initViews() {
        activityOtpVerificationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_otp_verification)
//        activityOtpVerificationBinding = viewDataBinding as ActivityOtpVerificationBinding
//        activityOtpVerificationBinding.toolbarCommon.imgToolbarText.text =
//            resources.getString(R.string.otp_verify)
        otpVerificationVM = ViewModelProvider(this).get(OTPVerificationVM::class.java)
        activityOtpVerificationBinding.verifViewModel = otpVerificationVM

        try {
            mJsonObject = JSONObject(intent.extras!!.get("data").toString())
            activityOtpVerificationBinding.tvOtpSent.text =
                activityOtpVerificationBinding.tvOtpSent.text
            //  action = intent.extras.get("action").toString()
            // var mSmsBroadcastReceiver = SMSBroadcastReciever()
            //set google api client for hint request
//            mSmsBroadcastReceiver.setOnOtpListeners(this)
//
//            val intentFilter = IntentFilter()
//            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
//            LocalBroadcastManager.getInstance(this).registerReceiver(mSmsBroadcastReceiver, intentFilter)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        otpVerificationVM.loading.observe(this, Observer<Boolean> { aBoolean ->
            if (aBoolean!!) {
                startProgressDialog()
            } else {
                stopProgressDialog()
            }
        })

        otpVerificationVM.isClick().observe(
            this, Observer<String>(function =
            fun(it: String?) {
                val otp = activityOtpVerificationBinding.pinview.value.toString()
                when (it) {
                    "pinview" -> {
                        if (TextUtils.isEmpty(otp)) {
                            showToastError(getString(R.string.empty) + " " + getString(R.string.OTP))
                        } else if (otp.length < 6) {
                            showToastError(
                                getString(R.string.invalid) + " " + getString(
                                    R.string.OTP
                                )
                            )
                        } else {
                            try {
                                mVerificationId = SharedPrefClass().getPrefValue(
                                    MyApplication.instance,
                                    GlobalConstants.OTP_VERIFICATION_ID
                                ).toString()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            startProgressDialog()
                            verifyVerificationCode(otp)
                        }
                    }
                    "tv_resend" -> {
                        val mJsonObject1 = JsonObject()
                        mJsonObject1.addProperty(
                            "countryCode",
                            mJsonObject.get("countryCode").toString()
                        )
                        mJsonObject1.addProperty(
                            "phoneNumber",
                            mJsonObject.get("phoneNumber").toString()
                        )
                        FirebaseFunctions.sendOTP("resend", mJsonObject1, this)
                        startProgressDialog()

                        object : CountDownTimer(60000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                activityOtpVerificationBinding.resendOTP = 1
                                activityOtpVerificationBinding.tvResendTime.text =
                                    "00:" + millisUntilFinished / 1000
                                //here you can have your logic to set text to edittext
                            }

                            override fun onFinish() {
                                activityOtpVerificationBinding.resendOTP = 0
                                // mTextField.setText("done!")
                            }
                        }.start()

                        // Finish current Activity
                        finish()
                    }
                }
            })
        )
        otpVerificationVM.onOtpEnteredListener(activityOtpVerificationBinding.pinview)
        activityOtpVerificationBinding.incGenericToolbar.tvTitleVerificationCode.setText(
            MyApplication.instance.getString(R.string.verification_account)
        )

        activityOtpVerificationBinding.includeView.toolbatTitle.setText(
            MyApplication.instance.getString(R.string.verification_account)
        )
        activityOtpVerificationBinding.includeView.ivBack.setOnClickListener(onToolbarBack)

        val resendTv = activityOtpVerificationBinding.tvResend
        resendTv.setPaintFlags(resendTv.getPaintFlags() and Paint.UNDERLINE_TEXT_FLAG.inv())
    }

    private val onToolbarBack = object : View.OnClickListener {
        override fun onClick(p0: View?) {
            finish()
        }
    }

    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
            //Getting the code sent by SMS
            stopProgressDialog()
            val code = phoneAuthCredential.smsCode
            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                // verifying the code
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        override fun onVerificationFailed(e: FirebaseException) {
            if ((e as FirebaseAuthException).errorCode == "ERROR_INVALID_PHONE_NUMBER")
                showToastError(
                    e.message.toString().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()[0]
                )
            else
                showToastError(getString(R.string.server_not_reached))
        }
    }

    private fun verifyVerificationCode(code: String) {
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(mVerificationId!!, code)
        //signing the user
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                stopProgressDialog()
                if (task.isSuccessful) {
                    SharedPrefClass().putObject(
                        MyApplication.instance,
                        "isLogin",
                        true
                    )

                    val intent = Intent(this, DashboardNewActivity::class.java)
                    intent.putExtra("phoneNumber", mJsonObject.getString("phoneNumber"))
                    intent.putExtra("data", mJsonObject.toString())
                    intent.addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                    startActivity(intent)
                    finish()
                } else {
                    //verification unsuccessful.. display an error message
                    var message = getString(R.string.something_error)

                    if (task.exception is FirebaseAuthException) {
                        message = getString(R.string.invalid_otp)
                    }
                    showToastError(message)
                }
            }
    }
}
