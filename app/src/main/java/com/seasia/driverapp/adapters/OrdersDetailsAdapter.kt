package com.seasia.driverapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.seasia.driverapp.R
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.databinding.RowJobOrdersBinding
import com.seasia.driverapp.databinding.RowJobOrdersNewBinding
import com.seasia.driverapp.model.OrderDetailResponse
import com.seasia.driverapp.model.OrderStatusResponse


class OrdersDetailsAdapter(
    val context: Context,
    val currency: String,
    val jobHistoryList: ArrayList<OrderDetailResponse.Suborder>
) : RecyclerView.Adapter<OrdersDetailsAdapter.ViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_job_orders_new,
            parent,
            false
        ) as RowJobOrdersNewBinding
        return ViewHolder(binding.root, viewType, binding, context, jobHistoryList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        val response = jobHistoryList[position].service

        holder.binding.tvUserName.text = response.name

        val price = "$currency${response.price}"
        holder.binding.tvPriceTitle.text = price

        holder.binding.tvUnits.text = jobHistoryList[position].quantity

        UtilsFunctions.loadImage(
            context,
            response.thumbnail,
            RequestOptions(),
            R.drawable.no_image,
            holder.binding.ivCustImage
        )

/*        holder.binding.tvAssignedDate.text = Utils(context).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            response.serviceDateTime,
            "dd MMM yyyy, hh:mm a"
        )

        holder.binding.tvAddress.text = response.address.addressName

        val price = "${response.currency} ${response.totalOrderPrice}"
        holder.binding.tvOrderPrice.text = price

        setJobStatusColor(response.progressStatus, holder.binding.tvJobStatus)*/
    }

    override fun getItemCount(): Int {
        return jobHistoryList.count()
    }

    inner class ViewHolder
        (
        v: View, val viewType: Int,
        val binding: RowJobOrdersNewBinding,
        context: Context,
        jobHistoryList: ArrayList<OrderDetailResponse.Suborder>
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
