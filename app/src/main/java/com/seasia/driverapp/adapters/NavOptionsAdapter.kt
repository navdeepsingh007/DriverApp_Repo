package com.seasia.driverapp.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.seasia.driverapp.R
import com.seasia.driverapp.databinding.RowNavItemsBinding
import com.seasia.driverapp.utils.NavOptionsClick
import com.seasia.driverapp.views.DashboardNewActivity

class NavOptionsAdapter(
    val context: DashboardNewActivity
) : RecyclerView.Adapter<NavOptionsAdapter.SubServicesVH>() {
    private val TAG = "CartAdapter"
    private var optionsList = ArrayList<String>()

    init {
        optionsList.add("Home")
//        optionsList.add("Profile")
        optionsList.add("My Profile")
        optionsList.add("Job History")
//        optionsList.add("Earnings")
        optionsList.add("Notifications")
//        optionsList.add("Help")
        optionsList.add("Terms and Conditions")
        optionsList.add("Privacy Policy")
    }

    override fun getItemCount(): Int {
        return optionsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubServicesVH {
        val binding = DataBindingUtil.inflate<RowNavItemsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.row_nav_items,
            parent,
            false
        )
        return SubServicesVH(binding)
    }

    override fun onBindViewHolder(holder: SubServicesVH, position: Int) {
        val cartListData = optionsList.get(position)
        Log.d(TAG, "======> " + cartListData.toString())

        holder.binding.tvOptionName.text = optionsList.get(position)

//        UtilsFunctions.loadImage(
//            context,
//            cartListData.service.icon,
//            RequestOptions(),
//            R.drawable.no_image_available,
//            holder.binding.ivServiceImage
//        )
        holder.binding.root.setOnClickListener {
            context.binding.drawerLayout.closeDrawers()
            NavOptionsClick(context).onOptionClick(optionsList[position])
        }
    }

    inner class SubServicesVH(val binding: RowNavItemsBinding) :
        RecyclerView.ViewHolder(binding.root)
}