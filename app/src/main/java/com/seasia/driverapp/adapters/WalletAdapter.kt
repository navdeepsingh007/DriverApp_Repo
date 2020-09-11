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
import com.seasia.driverapp.databinding.RowJobOrdersNewBinding
import com.seasia.driverapp.databinding.RowWalletAdapterBinding
import com.seasia.driverapp.model.OrderDetailResponse

class WalletAdapter (
    val context: Context) : RecyclerView.Adapter<WalletAdapter.ViewHolder>() {

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

    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
      //  val response = jobHistoryList[position].service
    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ViewHolder
        (
        v: View, val viewType: Int,
        val binding: RowWalletAdapterBinding
    ) : RecyclerView.ViewHolder(v)

}
