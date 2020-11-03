package com.seasia.driverapp.views

import android.app.DatePickerDialog
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.seasia.driverapp.R
import com.seasia.driverapp.adapters.WalletAdapter
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.databinding.ActivityWalletHistoryBinding
import com.seasia.driverapp.model.WalletInput
import com.seasia.driverapp.model.WalletResponse
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.viewmodel.WalletHistoryViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WalletHistoryActivity : BaseActivity(), View.OnClickListener {
    var walletVIewModel:WalletHistoryViewModel?=null
    var binding:ActivityWalletHistoryBinding?=null
    var adapter:WalletAdapter?=null
    private var date = ""
    private var startDate = ""
    private var endtDate = ""
    var arrayList:ArrayList<WalletResponse.Body>?=null
    override fun getLayoutId(): Int {
        return R.layout.activity_wallet_history
    }

    override fun initViews() {
        binding=viewDataBinding as ActivityWalletHistoryBinding
        walletVIewModel = ViewModelProviders.of(this).get(WalletHistoryViewModel::class.java)
        binding!!.tvStartDate.setOnClickListener(this)
        binding!!.startDateCalender.setOnClickListener(this)
        binding!!.tvEndDate.setOnClickListener(this)
        binding!!.endDateCalender.setOnClickListener(this)
        arrayList= ArrayList()
        walletAdapter()

        binding!!.btnSubmit.setOnClickListener(this)
        binding!!.ivBack.setOnClickListener(this)

        binding!!.commonToolBar.imgToolbarText.text = resources.getString(R.string.wallet_history)
        val df: DateFormat = SimpleDateFormat("MM-dd-yyyy")
        val dateobj = Date()
        binding!!.tvStartDate.setText(df.format(dateobj))
        binding!!.tvEndDate.setText(df.format(dateobj))
        startDate=df.format(dateobj)
        endtDate=df.format(dateobj)
        hitApi()
    }

    fun walletAdapter(){
        adapter= WalletAdapter(this,arrayList)
        var layputManager=LinearLayoutManager(this)
        binding!!.rvRecycler.adapter=adapter
        binding!!.rvRecycler.layoutManager=layputManager
    }


    fun startDatePicker() {
        val calendar = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "MM-dd-yyyy"
            val myFormat2 = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
//            if (DateTimeUtil.checkPastDay(calendar2, calendar)
//            ) {
//                showToastError("Please select future date")
//            } else {
                date = sdf2.format(calendar.time)
                binding!!.tvStartDate.setText(sdf.format(calendar.time))
                startDate=sdf.format(calendar.time)
//            }
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
            val myFormat = "MM-dd-yyyy"
            val myFormat2 = "yyyy-MM-dd"
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            val sdf2 = SimpleDateFormat(myFormat2, Locale.US)
//            if (DateTimeUtil.checkPastDay(calendar2, calendar)
//            ) {
//                showToastError("Please select future date")
//            } else {
                date = sdf2.format(calendar.time)
                binding!!.tvEndDate.setText(sdf.format(calendar.time))
                endtDate=sdf.format(calendar.time)
//            }
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
            R.id.startDateCalender->{
                startDatePicker()
            }
            R.id.tvEndDate->{
                endDatePicker()
            }
            R.id.endDateCalender->{
                endDatePicker()
            }
            R.id.iv_back->{
                finish()
            }
            R.id.btnSubmit->{
                hitApi()

            }
        }
    }
    fun hitApi(){
        startProgressDialog()
        val  input=WalletInput()
        input.fromDate=startDate
        input.toDate=endtDate
        input.page="1"
        input.limit="30"
//                input.payType="0"
        walletVIewModel!!.walletInputData(input)
        walletVIewModel!!.walletResponse().observe(this, Observer { response ->
            stopProgressDialog()
            if (response != null) {
                val message = response.message
                if (response.code == 200) {
                    arrayList=response.body
                    adapter!!.setData(arrayList)
                    if(arrayList!!.size>0){
                        binding!!.noRecordFound.visibility=View.GONE
                    } else{
                        binding!!.noRecordFound.visibility=View.VISIBLE
                    }
                } else {
                    showToastError(message)
                }
            } else {
                showToastError(MyApplication.instance.getString(R.string.internal_server_error))
            }
        })
    }


}