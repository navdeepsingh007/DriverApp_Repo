package com.seasia.driverapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.seasia.driverapp.R
import com.seasia.driverapp.databinding.RowFeedbackListingBinding
import com.seasia.driverapp.model.CustomerFeedbackResponse
import com.seasia.driverapp.utils.Utils
import com.seasia.driverapp.views.CustomerFeedbackActivity
import com.seasia.driverapp.views.OrderDetailsActivity


class CustomerFeedbackAdapter(
    var context: CustomerFeedbackActivity,
    val ratingList: ArrayList<CustomerFeedbackResponse.Rating>,
    var activity: Context
) : RecyclerView.Adapter<CustomerFeedbackAdapter.ViewHolder>() {

    private val mContext: CustomerFeedbackActivity

    init {
        this.mContext = context
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_feedback_listing,
            parent,
            false
        ) as RowFeedbackListingBinding
        return ViewHolder(binding.root, viewType, binding, mContext, ratingList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        val result = ratingList[position]
        holder.binding.tvReview.text = result.review
        holder.binding.tvUserName.text = result.user.firstName
        holder.binding.ratingBar.rating = result.rating.toFloat()
        holder.binding.tvFeedbackDate.text = Utils(activity).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            result.createdAt,
            "dd MMM yyyy, hh:mm a"
        )

        holder.binding.orderDetails.setOnClickListener {

            if (result.order == null) {
                context.showToastError(context.getString(R.string.order_is_already_deleted))
            } else {
                val formattedOrderDate = Utils(context).getDate(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                    result.order.serviceDateTime,
                    "dd MMM yyyy, hh:mm a"
//            "dd-MMM,yyyy | hh:mm a"
                )
                val currDate = Utils(context).currentDate()
                val orderDate = Utils(context).formattedDate(
                    formattedOrderDate,
                    "dd MMM yyyy, hh:mm a", "dd-MMM-yyyy"
                )
                showOrderDetails(result.order.id, currDate, orderDate)
            }
        }

        setImage(result.user.image, holder)
    }

    private fun showOrderDetails(
        orderId: String,
        currDate: String,
        orderDate: String
    ) {
        val intent = Intent(mContext, OrderDetailsActivity::class.java)
        intent.putExtra("orderId", orderId)
        intent.putExtra("currDate", currDate)
        intent.putExtra("orderDate", orderDate)
        mContext.startActivity(intent)
    }

    private fun setImage(path: String, holder: ViewHolder) {
        Glide.with(mContext)
            .load(path)
            .placeholder(R.drawable.user)
            .into(holder.binding.ivCustImage)
    }

    override fun getItemCount(): Int {
//        return 10
        return ratingList.count()
    }

    inner class ViewHolder
        (
        v: View, val viewType: Int,
        val binding: RowFeedbackListingBinding,
        mContext: CustomerFeedbackActivity,
        ratingList: ArrayList<CustomerFeedbackResponse.Rating>?
    ) : RecyclerView.ViewHolder(v)
}
