package com.seasia.driverapp.views.driverjobs

import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.seasia.driverapp.R
import com.seasia.driverapp.adapters.JobCancelReasonAdapter
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.callbacks.JobCancelReason
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.databinding.ActivityCancelReasonBinding
import com.seasia.driverapp.model.CancelOptionsResponse
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.viewmodel.CancelVM

class JobCancelReasonActivity : BaseActivity(), JobCancelReason {
    private lateinit var binding: ActivityCancelReasonBinding
    private lateinit var cancelVM: CancelVM

    override fun getLayoutId(): Int {
        return R.layout.activity_cancel_reason
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityCancelReasonBinding
        cancelVM = ViewModelProvider(this).get(CancelVM::class.java)
        binding.cancelVM = cancelVM

        initToolbar()
        initCancelOptionsObserver()
        getExtras()
    }

    private fun initToolbar() {
        binding.toolbarCommon.imgToolbarText.text =
            resources.getString(R.string.cancel_reason)
        binding.toolbarCommon.imgToolbarText.setTextColor(
            resources.getColor(R.color.colorBlue)
        )
    }

    private fun initCancelOptionsObserver() {
        cancelVM.getCancelOptionsList().observe(this,
            Observer<CancelOptionsResponse> { response ->
                if (response != null) {
                    val message = response.message
                    stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            if (!response.body.isEmpty()) {
                                setCancelOptionsAdapter(response.body)
                            } else {
                                message?.let {
//                                    UtilsFunctions.showToastError(it)
                                }
                            }
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

    private fun setCancelOptionsAdapter(response: ArrayList<CancelOptionsResponse.Body>) {
        if (response.size > 0) {
            binding.rvCancelReason.adapter = JobCancelReasonAdapter(this, response, this)
            binding.rvCancelReason.layoutManager = LinearLayoutManager(this)

            binding.rvCancelReason.visibility = View.VISIBLE
            binding.tvNoRecord.visibility = View.GONE
        } else {
            binding.rvCancelReason.visibility = View.GONE
            binding.tvNoRecord.visibility = View.VISIBLE
        }
    }

    private fun getExtras() {
        val orderId = intent.getStringExtra("orderId")
        cancelVM.selectedOrderId(orderId!!)
    }

    override fun onJobCancelReasonSelected(reasonId: String, reasonName: String) {
//        UtilsFunctions.showToastSuccess(reasonId)

        if (reasonName.contains("Other", true) || reasonName.contains("Others", true)) {
            binding.etParent.visibility = View.VISIBLE
            binding.btnCancel.visibility = View.VISIBLE
        } else {
            binding.etParent.visibility = View.GONE
            binding.btnCancel.visibility = View.GONE
            initOnJobCancelObserver(reasonId, "")
        }

        binding.btnCancel.setOnClickListener {
            if (binding.etReason.text.toString().trim().isEmpty()) {
                UtilsFunctions.showToastWarning("Please enter reason for cancellation")
                return@setOnClickListener
            }
            initOnJobCancelObserver(reasonId, binding.etReason.text.trim().toString())
        }
    }

    private fun initOnJobCancelObserver(reasonId: String, userReason: String) {
        cancelVM.onJobCancel(reasonId, userReason).observe(this, Observer { response ->
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

    /*    private fun setCartItems(response: CartListingResponse) {
        val cartList = response.body.data
        if (cartList.size > 0) {
            binding.rvCartItems.adapter = CartAdapter(this, cartList, onRemoveFromCartListener)
            binding.rvCartItems.layoutManager = LinearLayoutManager(this)

            binding.rvCartItems.visibility = View.VISIBLE
            binding.tvEmptyCartMsg.visibility = View.GONE
        } else {
            binding.rvCartItems.visibility = View.GONE
            binding.tvEmptyCartMsg.visibility = View.VISIBLE
        }
    }*/
}