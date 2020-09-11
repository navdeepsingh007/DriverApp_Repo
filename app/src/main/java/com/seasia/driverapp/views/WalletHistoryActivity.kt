package com.seasia.driverapp.views

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.FirebaseFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.ActivityWalletHistoryBinding
import com.seasia.driverapp.model.WalletInput
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.utils.DateTimeUtil
import com.seasia.driverapp.viewmodel.JobsViewModel
import com.seasia.driverapp.viewmodel.WalletHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

class WalletHistoryActivity : BaseActivity(), View.OnClickListener {
    var walletVIewModel:WalletHistoryViewModel?=null
    var binding:ActivityWalletHistoryBinding?=null
    private var date = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_wallet_history
    }

    override fun initViews() {
        binding=viewDataBinding as ActivityWalletHistoryBinding
        walletVIewModel = ViewModelProviders.of(this).get(WalletHistoryViewModel::class.java)
        binding!!.tvStartDate.setOnClickListener(this)
        binding!!.tvEndDate.setOnClickListener(this)

        binding!!.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.wallet_history)
//        val  input=WalletInput()
//
//        walletVIewModel!!.walletInputData(input)
//
//        walletVIewModel!!.walletResponse().observe(this, Observer { response ->
//            stopProgressDialog()
//
//            if (response != null) {
//                val message = response.message
//
//                if (response.code == 200) {
//                } else {
//                    showToastError(message)
//                }
//            } else {
//                showToastError(MyApplication.instance.getString(R.string.internal_server_error))
//            }
//        })

    }


    fun startDatePicker() {
        val calendar = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd/MM/yyyy"
            val myFormat2 = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
            if (DateTimeUtil.checkPastDay(calendar2, calendar)
            ) {
                showToastError("Please select future date")
            } else {
                //strDate = sdf.format(calendar.time)
                date = sdf2.format(calendar.time)
                binding!!.tvStartDate.setText(sdf.format(calendar.time))
                val mJsonObject = JsonObject()
//                mJsonObject.addProperty("subjectId", subjectId)
//                mJsonObject.addProperty("date", date)
//                bookingAddStudentViewModel.getSlotList(this, mJsonObject)
            }
        }
        val dpDialog = DatePickerDialog(
            this,R.style.DialogTheme, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpDialog.show()
    }

    fun endDatePicker() {
        val calendar = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd/MM/yyyy"
            val myFormat2 = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
            if (DateTimeUtil.checkPastDay(calendar2, calendar)
            ) {
                showToastError("Please select future date")
            } else {
                //strDate = sdf.format(calendar.time)
                date = sdf2.format(calendar.time)
                binding!!.tvEndDate.setText(sdf.format(calendar.time))
            }
        }
        val dpDialog = DatePickerDialog(
            this,R.style.DialogTheme, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpDialog.show()
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.tvStartDate->{
                startDatePicker()
            }
            R.id.tvEndDate->{
                endDatePicker()
            }
        }
    }
}