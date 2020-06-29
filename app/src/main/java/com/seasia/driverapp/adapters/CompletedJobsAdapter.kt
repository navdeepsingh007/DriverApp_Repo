package com.uniongoods.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.seasia.driverapp.R
import com.seasia.driverapp.databinding.RowDriverJobsBinding
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.views.driverjobs.CompletedJobsFragment

class CompletedJobsAdapter(
    context : CompletedJobsFragment,
    jobsList : ArrayList<OrderStatusResponse.Body>,
    var activity : Context
) :
    RecyclerView.Adapter<CompletedJobsAdapter.ViewHolder>() {
    private val mContext : CompletedJobsFragment
    private var viewHolder : ViewHolder? = null
    private var servicesList : ArrayList<OrderStatusResponse.Body>

    init {
        this.mContext = context
        this.servicesList = jobsList
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent : ViewGroup, viewType : Int) : ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_driver_jobs,
            parent,
            false
        ) as RowDriverJobsBinding
        return ViewHolder(binding.root, viewType, binding, mContext, servicesList)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(@NonNull holder : ViewHolder, position : Int) {
        val response = servicesList.get(position)
        holder.binding.tvAssignedDate.text = response.serviceDateTime
        holder.binding.tvAddress.text = response.address.addressName
        holder.binding.tvOrderPrice.text = response.totalOrderPrice
        holder.binding.btnCall.setOnClickListener { onCall() }
        holder.binding.btnStart.setOnClickListener { onStart() }
        holder.binding.btnCancel.setOnClickListener { onCancel() }
    }

    override fun getItemCount() : Int {
        return servicesList.count()
    }

    //This constructor would switch what to findViewBy according to the type of viewType
    inner class ViewHolder
        (
        v : View, val viewType : Int, //These are the general elements in the RecyclerView
        val binding : RowDriverJobsBinding,
        mContext : CompletedJobsFragment,
        jobsList : ArrayList<OrderStatusResponse.Body>
    ) : RecyclerView.ViewHolder(v)

    private fun onCall() {

    }

    private fun onStart() {

    }

    private fun onCancel() {

    }
}