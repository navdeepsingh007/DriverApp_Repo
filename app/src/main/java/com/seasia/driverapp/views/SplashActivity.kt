package com.seasia.driverapp.views

import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.ActivitySplashBinding
import com.seasia.driverapp.fcm.FcmUtils
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.utils.BaseActivity

import java.util.*

class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var sharedPrefClass: SharedPrefClass? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun initViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        // FCM instance ID
        FcmUtils.getInstanceId()

        sharedPrefClass = SharedPrefClass()
        val token: String? = "sd"

        if (token != null) {
            sharedPrefClass!!.putObject(
                applicationContext,
                GlobalConstants.NOTIFICATION_TOKEN,
                token
            )
        }

        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    checkScreenType()
                }
            }
        }, 3000)
    }


    private fun checkScreenType() {
        var login = ""
        if (checkObjectNull(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance,
                    "isLogin"
                )
            )
        )
            login = sharedPrefClass!!.getPrefValue(this, "isLogin").toString()

        val intent = if (login == "true") {
            Intent(this, DashboardNewActivity::class.java)
        } else {
            Intent(this, LoginUserActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}
