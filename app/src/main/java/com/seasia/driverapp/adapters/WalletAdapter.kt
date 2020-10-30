package com.seasia.driverapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.seasia.driverapp.R
import com.seasia.driverapp.databinding.RowWalletAdapterBinding
import com.seasia.driverapp.model.WalletResponse

class WalletAdapter(
    val context: Context,
    var arrayList: ArrayList<WalletResponse.Body>?
) : RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_wallet_adapter,
            parent,
            false
        ) as RowWalletAdapterBinding
        return ViewHolder(binding.root, viewType, binding)
    }

    fun setData(list: ArrayList<WalletResponse.Body>?) {
        arrayList = list
        notifyDataSetChanged()

    }

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        //  val response = jobHistoryList[position].service

        holder.binding.tvDate.text = arrayList!!.get(position).createdAt
        holder.binding.tvOrderId.text = arrayList!!.get(position).order!!.orderNo
        holder.binding.tvAmount.text = "$" + arrayList!!.get(position).amount
        if (arrayList!!.get(position).payType.equals("0")) {
            holder.binding.tvType.text = "Debit"

        } else {
            holder.binding.tvType.text = "Credit"

        }
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    inner class ViewHolder
        (
        v: View, val viewType: Int,
        val binding: RowWalletAdapterBinding
    ) : RecyclerView.ViewHolder(v)

}
