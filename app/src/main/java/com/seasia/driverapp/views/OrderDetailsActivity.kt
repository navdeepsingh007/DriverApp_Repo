package com.seasia.driverapp.views

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seasia.driverapp.R
import com.seasia.driverapp.adapters.InstractionListAdapter
import com.seasia.driverapp.adapters.OrdersDetailsAdapter
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.databinding.ActivityJobDetailsBinding
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.model.OrderDetailResponse
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.utils.Utils
import com.seasia.driverapp.viewmodel.JobDetailsVM


class OrderDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityJobDetailsBinding
    private lateinit var jobDetailsVM: JobDetailsVM
    private lateinit var orderId: String
    private lateinit var orderStatus: String
    private lateinit var currDate: String
    private lateinit var orderDate: String
    private val ORDER_STATUS_COMPLETE = "4"
    var interactionList:ArrayList<String>?=null
    var adapter: InstractionListAdapter?=null

    override fun getLayoutId(): Int {
        return R.layout.activity_job_details
    }

    override fun initViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_job_details)
        jobDetailsVM = ViewModelProvider(this).get(JobDetailsVM::class.java)
        binding.jobDtlVM = jobDetailsVM

        interactionList= ArrayList()

        getOrderExtras()
        initToolbar()
//        setJobHistoryAdapter()
        initJobDetailsObserver()
        startProgressDialog()
        initClickListener()
    }

    private fun getOrderExtras() {
        orderId = intent.getStringExtra("orderId")!!
        if (intent.getStringExtra("orderStatus")!! != null) {
            orderStatus = intent.getStringExtra("orderStatus")!!
            currDate = intent.getStringExtra("currDate")!!
            orderDate = intent.getStringExtra("orderDate")!!
        }

    }

    private fun initToolbar() {
        binding.toolbarCommon.imgToolbarText.text =
            resources.getString(R.string.order_details)
        binding.toolbarCommon.imgToolbarText.setTextColor(
            resources.getColor(R.color.colorBlue)
        )
    }

    fun initJobDetailsObserver() {
        jobDetailsVM.getJobDtlData(orderId).observe(this, Observer { response ->
            stopProgressDialog()

            if (response != null) {
                val message = response.message
                if (response.code == 200) {
                    setOrderDetailsAdapter(response.body.currency, response.body.suborders)
                    initOrderDetails(response.body)
                } else {
                    showToastError(message)
                }
            } else {
                showToastError(MyApplication.instance.getString(R.string.internal_server_error))
            }
        })
    }

    private fun setOrderDetailsAdapter(
        currency: String,
        result: ArrayList<OrderDetailResponse.Suborder>
    ) {
//        val options = ArrayList<String>()
//        options.add("Not available")
//        options.add("B")
//        options.add("C")
//        options.add("Others")

        if (result.size > 0) {
            val rateListAdapter = OrdersDetailsAdapter(this, currency, result)
            val linearLayoutManager = LinearLayoutManager(this)
            linearLayoutManager.orientation = RecyclerView.VERTICAL
            binding.rvOrderDetails.layoutManager = linearLayoutManager
            binding.rvOrderDetails.adapter = rateListAdapter

//            binding.rvOrderDetails.visibility = View.VISIBLE
//            binding.tvNoRecord.visibility = View.GONE
        } else {
//            binding.rvOrderDetails.visibility = View.GONE
//            binding.tvNoRecord.visibility = View.VISIBLE
        }
    }

    private fun initOrderDetails(result: OrderDetailResponse.Body) {
        binding.tvOrderNo.text = result.orderNo
        binding.tvOrderDate.text = Utils(this).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            result.serviceDateTime,
            "dd MMM yyyy, hh:mm a"
        )

        if (result.address != null) {
            binding.tvDeliveryMethod.text = result.address.addressName
        }


        if (result.company!=null){
            Glide.with(this)
                .load(result.company.logo1)
                .placeholder(R.drawable.placeholder)
                .into(binding.restaurantImage)
            binding.name.setText(result.company.companyName)
            binding.rating.setText(result.company.rating+" Rating")
            binding.location.setText(result.company.address1)

        }

        if(result.deliveryInstructions!=null){
            interactionList= ArrayList()
            interactionList=result.deliveryInstructions
            if(interactionList!!.size>0){
                setINteractionAdapeter()
                binding.instractions.visibility=View.VISIBLE
            }
        }
        val price = "${result.currency}${result.orderPrice}"
        binding.tvTotalAmount.text = price

        if(!result.tip.isEmpty() || result.tip!=null){
            binding.tipParent.visibility=View.VISIBLE
            binding.tvTipAmount.text = result.tip
        }

        val items = "${result.suborders.size} items"
        binding.tvItemsCount.text = items
        val orderStatus = UtilsFunctions.getOrderDetailsStatus(result.progressStatus.toString())
        binding.tvItemStatus.text = orderStatus


        if (currDate.equals(orderDate)) {
            if ((result.progressStatus.toString().equals("8")) || (result.progressStatus.toString().equals("9"))) {
                binding.btnCompleteOrder.visibility = View.VISIBLE
            } else{
                binding.btnCompleteOrder.visibility = View.GONE
            }

        } else {
            binding.btnCompleteOrder.visibility = View.GONE
            if (orderStatus.equals("Delivered", true)) {
                binding.tvItemStatus.setTextColor(resources.getColor(R.color.colorGreen))
            } else {
                binding.tvItemStatus.setTextColor(resources.getColor(R.color.colorRed))
            }
        }


//        if (orderStatus.equals("Delivered", true)) {
//            binding.tvItemStatus.setTextColor(resources.getColor(R.color.colorGreen))
//            binding.btnCompleteOrder.visibility = View.GONE
//        } else {
//            binding.tvItemStatus.setTextColor(resources.getColor(R.color.colorRed))
//            binding.btnCompleteOrder.visibility = View.VISIBLE
//        }
    }


    private fun initClickListener() {
        jobDetailsVM.isClick().observe(this, Observer {
            when (it) {
                "btnCompleteOrder" -> {
                    initUpdateStatus()
                }
            }
        })
    }


    fun setINteractionAdapeter(){
         adapter = InstractionListAdapter(this,interactionList)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        binding.interactionRecyclerVIew.layoutManager = linearLayoutManager
        binding.interactionRecyclerVIew.adapter = adapter
    }

    private fun initUpdateStatus() {
        jobDetailsVM.updateOrderStatus(orderId, ORDER_STATUS_COMPLETE).observe(this,
            Observer<CommonModel> { response ->
                if (response != null) {
                    val message = response.message
                    stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            UtilsFunctions.showToastSuccess(message!!)

                            finish()
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                } else {
                    stopProgressDialog()
                }
            })
    }
}