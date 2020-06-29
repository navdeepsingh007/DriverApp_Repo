package com.seasia.driverapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.seasia.driverapp.R
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.databinding.RowEarningsBinding
import com.seasia.driverapp.databinding.RowJobCancelReasonBinding
import com.seasia.driverapp.databinding.RowJobsHistoryBinding
import com.seasia.driverapp.databinding.RowNotificationsBinding
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.utils.Utils
import com.seasia.driverapp.views.OrderDetailsActivity


class EarningsAdapter(
    val context: Context,
    val earningList: ArrayList<OrderStatusResponse.Body>
) : RecyclerView.Adapter<EarningsAdapter.ViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_earnings,
            parent,
            false
        ) as RowEarningsBinding
        return ViewHolder(binding.root, viewType, binding, context, earningList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
//        return earningList.count()
        return 5
    }

    inner class ViewHolder
        (
        v: View, val viewType: Int,
        val binding: RowEarningsBinding,
        context: Context,
        earningList: ArrayList<OrderStatusResponse.Body>
    ) : RecyclerView.ViewHolder(v)



    private fun setJobStatusColor(statusType: Int, text: TextView) {
        // Pending jobs - 1, Completed jobs - 5
        if (statusType == 1) {
            text.setTextColor(context.resources.getColor(R.color.colorRed))
            text.text = context.resources.getString(R.string.pending)
        } else {
            text.setTextColor(context.resources.getColor(R.color.colorGreen))
            text.text = context.resources.getString(R.string.completed)
        }
    }
}
