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
import com.seasia.driverapp.databinding.RoeInterationListBinding


class InstractionListAdapter(
    val context: Context,
    val instration: ArrayList<String>?
) : RecyclerView.Adapter<InstractionListAdapter.ViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.roe_interation_list,
            parent,
            false
        ) as RoeInterationListBinding
        return ViewHolder(binding.root, viewType, binding)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        val response = instration!!.get(position)
        holder.binding.tvInstration.setText(response)

    }

    override fun getItemCount(): Int {
        return instration!!.count()
    }

    inner class ViewHolder
        (
        v: View, val viewType: Int,
        val binding: RoeInterationListBinding
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
