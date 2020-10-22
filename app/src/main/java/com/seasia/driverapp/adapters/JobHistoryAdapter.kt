package com.seasia.driverapp.adapters

import android.content.Context
import android.content.Intent
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
import com.seasia.driverapp.databinding.RowJobsHistoryBinding
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.utils.Utils
import com.seasia.driverapp.views.OrderDetailsActivity


class JobHistoryAdapter(
    val context: Context,
    var jobHistoryList: ArrayList<OrderStatusResponse.Body>
) : RecyclerView.Adapter<JobHistoryAdapter.ViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_jobs_history,
            parent,
            false
        ) as RowJobsHistoryBinding
        return ViewHolder(binding.root, viewType, binding, context, jobHistoryList)
    }

    fun setData(list: ArrayList<OrderStatusResponse.Body>){
        jobHistoryList=list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        val response = jobHistoryList[position]
        holder.binding.tvAssignedDate.text = Utils(context).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            response.serviceDateTime,
            "dd MMM yyyy, hh:mm a"
        )

//        holder.binding.tvAddress.text = response.address.addressName
//        val completeAddress = "${response.address.addressName} ${response.address.landmark} ${response.address.city}"

        holder.binding.tvAddress.text = response.company.address1
        holder.binding.tvOrderType.text = response.orderNo

        print("currency=---- ${response.currency}")
        val price = "${response.currency}${response.totalOrderPrice}"
        holder.binding.tvOrderPrice.text = price

        setJobStatusColor(response.progressStatus, holder.binding.tvJobStatus)
        val orderStatus = UtilsFunctions.getTrackingStatus(response.progressStatus.toString())

        val formattedOrderDate = Utils(context).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            response.serviceDateTime,
            "dd MMM yyyy, hh:mm a"
//            "dd-MMM,yyyy | hh:mm a"
        )
        val currDate = Utils(context).currentDate()
        val orderDate = Utils(context).formattedDate(
            formattedOrderDate,
            "dd MMM yyyy, hh:mm a", "dd-MMM-yyyy"
        )



        holder.binding.ivOrderDetails.setOnClickListener { showOrderDetails(
            response.id,
            orderStatus,
            currDate,
            orderDate
        ) }

        // Restaurant image, name,
//        holder.binding.ivRestaurantImage
        holder.binding.tvRestaurantName.text = response.company.companyName

        itemsDeliveredByDriver(holder, response)

        UtilsFunctions.loadImage(
            context,
            response.company.logo1,
            RequestOptions(),
            R.drawable.no_image,
            holder.binding.ivRestaurantImage
        )
    }

    private fun itemsDeliveredByDriver(holder: ViewHolder, response: OrderStatusResponse.Body) {
        val orderedItemsList = response.suborders
        var orderedItems = ""
        for (i in 0..orderedItemsList.size-1) {
            orderedItems += "${orderedItemsList.get(i).quantity} X ${orderedItemsList.get(i).service.name}"
            if (i != orderedItemsList.size-1) orderedItems += ", "
        }
        holder.binding.tvItems.text = orderedItems
    }

    private fun showOrderDetails(
        orderId: String,
        orderStatus: String,
        currDate: String,
        orderDate: String
    ) {
        val intent = Intent(context, OrderDetailsActivity::class.java)
        intent.putExtra("orderId", orderId)
        intent.putExtra("orderStatus", orderStatus)
        intent.putExtra("currDate", currDate)
        intent.putExtra("orderDate", orderDate)

        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return jobHistoryList.count()
    }

    inner class ViewHolder
        (
        v: View, val viewType: Int,
        val binding: RowJobsHistoryBinding,
        context: Context,
        jobHistoryList: ArrayList<OrderStatusResponse.Body>
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
