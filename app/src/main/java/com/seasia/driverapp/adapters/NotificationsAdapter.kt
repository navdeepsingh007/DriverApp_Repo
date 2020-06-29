package com.seasia.driverapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.seasia.driverapp.R
import com.seasia.driverapp.databinding.RowNotificationsBinding
import com.seasia.driverapp.model.NotificationResponse
import com.seasia.driverapp.utils.Utils


class NotificationsAdapter(
    val context: Context,
    val notificationList: ArrayList<NotificationResponse.Body>
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_notifications,
            parent,
            false
        ) as RowNotificationsBinding
        return ViewHolder(binding.root, viewType, binding, context, notificationList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        val result = notificationList[position]

        holder.binding.tvNotificationName.text = result.notificationTitle
        holder.binding.tvAssignedDate.text = Utils(context).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            result.createdAt,
            "dd MMM yyyy, hh:mm a"
        )
        holder.binding.tvNotificationDetails.text = result.notificationDescription
    }

    override fun getItemCount(): Int {
        return notificationList.count()
//        return 5
    }

    inner class ViewHolder
        (
        v: View, val viewType: Int,
        val binding: RowNotificationsBinding,
        context: Context,
        notificationList: ArrayList<NotificationResponse.Body>
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
