package com.seasia.driverapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.seasia.driverapp.R
import com.seasia.driverapp.callbacks.JobCancelReason
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.databinding.RowJobCancelReasonBinding
import com.seasia.driverapp.model.CancelOptionsResponse


class JobCancelReasonAdapter(
    val context: Context,
    val reasonList: ArrayList<CancelOptionsResponse.Body>,
    val cancelReason: JobCancelReason
) : RecyclerView.Adapter<JobCancelReasonAdapter.ViewHolder>() {

    private var lastCheckedRB: RadioButton? = null
    private var checkedRadioButtonPos: Int = 0

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_job_cancel_reason,
            parent,
            false
        ) as RowJobCancelReasonBinding
        return ViewHolder(binding.root, viewType, binding, context, reasonList)
    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        holder.binding.rbCancelReason.text = reasonList[position].reason

        holder.binding.rgCancelOption.setOnCheckedChangeListener { group, checkedId ->
            val checked_rb =
                group.findViewById<View>(checkedId) as RadioButton

            val lastCheckedBtn = lastCheckedRB
            if (lastCheckedBtn != null) {
                lastCheckedBtn.setChecked(false)
            }
            //store the clicked radiobutton
            lastCheckedRB = checked_rb

            val reasonName = holder.binding.rbCancelReason.text.toString()

            cancelReason.onJobCancelReasonSelected(reasonList[position].id, reasonName)
        }
    }

    fun getCheckedRadioBtnPos(): Int {
        return checkedRadioButtonPos
    }

    override fun getItemCount(): Int {
//        return 3
        return reasonList.count()
    }

    inner class ViewHolder
        (
        v: View, val viewType: Int,
        val binding: RowJobCancelReasonBinding,
        context: Context,
        reasonList: ArrayList<CancelOptionsResponse.Body>
    ) : RecyclerView.ViewHolder(v)
}
