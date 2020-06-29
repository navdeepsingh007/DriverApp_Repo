package com.seasia.driverapp.views

import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.databinding.ActivityDashboardBinding
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.viewmodel.LoginVM

class DashboardActivity : BaseActivity() {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var loginVM: LoginVM

    override fun getLayoutId(): Int {
        return R.layout.activity_dashboard
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityDashboardBinding
        loginVM = ViewModelProvider(this).get(LoginVM::class.java)
        binding.loginViewModel = loginVM
        initLogoutResponseObserver()
    }

    fun initLogoutResponseObserver() {
        loginVM.getLogoutData().observe(this, Observer { response ->
            stopProgressDialog()

            if (response != null) {
                val message = response.message!!
                if (response.code == 200) {
                    UtilsFunctions.showToastSuccess(message)
                    finish()
                } else {
                    UtilsFunctions.showToastError(message)
                }
            } else {
                UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
            }
        })
    }

    fun onProfileClick(v: View) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }
}