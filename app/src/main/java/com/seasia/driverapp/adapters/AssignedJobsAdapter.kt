package com.uniongoods.adapters

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.callbacks.DriverJobCallbacks
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.RowDriverJobsBinding
import com.seasia.driverapp.model.OrderStatusResponse
import com.seasia.driverapp.utils.CheckRuntimePermissions
import com.seasia.driverapp.utils.DialogClass
import com.seasia.driverapp.utils.DialogssInterface
import com.seasia.driverapp.utils.Utils
import com.seasia.driverapp.views.DriverJourneyActivity
import com.seasia.driverapp.views.driverjobs.AssignedJobsFragment
import kotlinx.android.synthetic.main.row_driver_jobs.view.*


class AssignedJobsAdapter(
    context: AssignedJobsFragment,
    var jobsList: ArrayList<OrderStatusResponse.Body>,
    var activity: Context,
    val driverJobCallbacks: DriverJobCallbacks
) :
    RecyclerView.Adapter<AssignedJobsAdapter.ViewHolder>(), DialogssInterface {
    private val mContext: AssignedJobsFragment

    //    private var servicesList: ArrayList<OrderStatusResponse.Body>
    val REQUEST_PERMISSIONS = 200
    private var confirmationDialog: Dialog? = null
    private var mDialogClass = DialogClass()
//    var orderId = ""
//    var trackStatus = ""
//    var lat = ""
//    var lon = ""

    init {
        this.mContext = context
//        this.servicesList = jobsList
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun setData(list: ArrayList<OrderStatusResponse.Body>){
        jobsList=list
        notifyDataSetChanged()
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_driver_jobs,
            parent,
            false
        ) as RowDriverJobsBinding
        return ViewHolder(binding.root, viewType, binding, mContext, jobsList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        val response = jobsList.get(position)
        var assignStatus= jobsList.get(position).assignedEmployees.get(0).jobStatus
        val formattedOrderDate = Utils(activity).getDate(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            response.serviceDateTime,
            "dd MMM yyyy, hh:mm a"
//            "dd-MMM,yyyy | hh:mm a"
        )
        holder.binding.tvAssignedDate.text = formattedOrderDate

        if (response.address != null) {
            val completeAddress =
                "${response.address.addressName} ${response.address.landmark} ${response.address.city}"
            holder.binding.deliveryAddress.text = completeAddress
        }

        if (response.company != null) {
            val deliveryAddress = "${response.company.address1}"
            holder.binding.tvAddress.text = deliveryAddress
        }



        val price = "${response.currency}${response.totalOrderPrice}"
        holder.binding.tvOrderPrice.text = price

        val orderId = response.id
        val userId = jobsList.get(position).assignedEmployees.get(0).id
        val trackStatus = response.trackStatus.toString()
        val progressStatus = response.progressStatus.toString()

        var lat = ""
        var lon = ""
        if (response.address != null) {
            lat = response.address.latitude
            lon = response.address.longitude
        }

//        val phoneNo = response.assignedEmployees[position].employee.phoneNumber
        val phoneNo = response.user.phoneNumber

        val trackingStatus = UtilsFunctions.getTrackingStatus(response.progressStatus.toString())

        holder.binding.btnCall.setOnClickListener { onCall(phoneNo) }
        val currDate = Utils(activity).currentDate()
        val orderDate = Utils(activity).formattedDate(
            formattedOrderDate,
            "dd MMM yyyy, hh:mm a", "dd-MMM-yyyy"
        )

        Log.d("AssignedJobs -===> ", "$currDate - $orderDate")


        holder.binding.btnStart.setOnClickListener {

            if(assignStatus.equals("0")){
//                Toast.makeText(activity,"comming soon", Toast.LENGTH_LONG).show()
                mContext.acceptOrder(orderId,userId)

            } else{
                onStart(orderId, progressStatus, lat, lon, trackingStatus,currDate,orderDate)
            }

        }
        holder.binding.btnCancel.setOnClickListener {
            if(assignStatus.equals("0")){
             //   Toast.makeText(activity,"conmming soon", Toast.LENGTH_LONG).show()
                mContext.rejectOrder(orderId,userId)

            } else{
                onCancel(orderId, "2", lat, lon)
            }
            }
        holder.binding.btnDetail.setOnClickListener { onDetail(orderId, trackStatus,currDate,orderDate) }
        holder.binding.tvOrderType.text = response.orderNo

//        holder.binding.btnStart.setText(trackingStatus)

        trackStatusVisiblity(trackingStatus, holder)

        // Row click listener
        holder.itemView.setOnClickListener {
            onDetail(orderId, trackStatus,currDate,orderDate)
        }

        holder.itemView.deliveryAddress.setOnClickListener {
            driverJobCallbacks.onShowGMaps(lat, lon)
        }

        // If job not of current date, disable set status button


        // Check already started job exist
        val isAlreadyStartedJobExist = MyApplication.sharedPref.getBoolPreference(
            MyApplication.instance,
            GlobalConstants.JOB_STARTED_STATUS
        )

//                || Utils(activity).compareIfPreviousDate(orderDate)


        if(assignStatus.equals("0")){
            holder.binding.btnStart.text="Take Order"
            holder.binding.btnCancel.text="Cancel"
            holder.binding.btnStart.setBackgroundResource(R.drawable.round_small_green)
            if (currDate.equals(orderDate)) {
                holder.binding.btnStart.isEnabled = true
                holder.binding.btnStart.background.alpha = 255
                holder.binding.btnCancel.isEnabled = true
                holder.binding.btnCancel.background.alpha = 255
            } else {
                holder.binding.btnStart.isEnabled = false
                holder.binding.btnStart.background.alpha = 100
            }
        } else{
            holder.binding.btnStart.text="Set Status"
            holder.binding.btnCancel.text="Cancel"
            holder.binding.btnStart.setBackgroundResource(R.drawable.round_small_blue)
            if (currDate.equals(orderDate)) {
                holder.binding.btnStart.isEnabled = true
                holder.binding.btnStart.background.alpha = 255
            } else {
                holder.binding.btnStart.isEnabled = false
                holder.binding.btnStart.background.alpha = 100
            }
            val alreadyExistingOrderId = MyApplication.sharedPref.getPrefValue(
                MyApplication.instance,
                GlobalConstants.JOB_ORDER_ID
            ) as String?

            if (alreadyExistingOrderId != null && !alreadyExistingOrderId.isEmpty()) {
                if (alreadyExistingOrderId.equals(orderId)) {
                    holder.binding.btnStart.isEnabled = true
                    holder.binding.btnStart.background.alpha = 255
                } else {
                    holder.binding.btnStart.isEnabled = false
                    holder.binding.btnStart.background.alpha = 100
                }
            }
        }
    }

    private fun trackStatusVisiblity(status: String, holder: ViewHolder) {
        when (status) {
            "On the way", "Reached", "Delivered" -> {
                holder.binding.btnCancel.isEnabled = false
                // Where the INT ranges from 0 (fully transparent) to 255 (fully opaque).
                holder.binding.btnCancel.background.alpha = 100
//                holder.binding.btnCancel.alpha =
//                    ColorUtils.setAlphaComponent(
//                        mContext.resources.getColor(R.color.colorMaroon),
//                        50
//                    ).toFloat()
            }
            else -> {
                holder.binding.btnCancel.isEnabled = true
                holder.binding.btnCancel.background.alpha = 255
            }
        }
    }

    override fun getItemCount(): Int {
        return jobsList.count()
    }

    inner class ViewHolder//This constructor would switch what to findViewBy according to the type of viewType
        (
        v: View, val viewType: Int, //These are the general elements in the RecyclerView
        val binding: RowDriverJobsBinding,
        mContext: AssignedJobsFragment,
        jobsList: ArrayList<OrderStatusResponse.Body>
    ) : RecyclerView.ViewHolder(v) {
        /* init {
             binding!!.linAddress.setOnClickListener {
                 mContext.deleteAddress(adapterPosition)
             }
         }*/
    }

    private fun onCall(phoneNo: String) {
        if (CheckRuntimePermissions.checkMashMallowPermissions(
                mContext.baseActivity,
                arrayOf(Manifest.permission.CALL_PHONE), REQUEST_PERMISSIONS
            )
        ) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo))
            mContext.startActivity(intent)
        }
    }

    private fun onStart(
        orderId: String,
        orderStatus: String,
        lat: String,
        lon: String,
        trackingStatus: String,
        currDate: String,
        orderDate: String
    ) {
        if (UtilsFunctions.checkGpsEnabled(activity)) {
            val intent = Intent(activity, DriverJourneyActivity::class.java)
            intent.putExtra("orderId", orderId)
            intent.putExtra("orderStatus", orderStatus)
            intent.putExtra("currDate", currDate)
            intent.putExtra("orderDate", orderDate)
            intent.putExtra("lat", lat)
            intent.putExtra("lon", lon)
            activity.startActivity(intent)
        }
        return

        val msg = when (trackingStatus) {
            "Start" -> mContext.resources.getString(R.string.job_on_way)
            "On the way" -> mContext.resources.getString(R.string.job_reached)
            "Reached" -> mContext.resources.getString(R.string.job_complete)
            else -> "DEVELOPER_MSG: TRACKING STATUS NOT VALID"
        }

//        confirmationDialog = mDialogClass.setDefaultDialog(
//            activity!!,
//            this,
//            "Start Job",
//            msg
//        )
//        confirmationDialog?.show()

        showStartDialog(msg, orderId, orderStatus, lat, lon)
    }

    private fun onCancel(orderId: String, orderStatus: String, lat: String, lon: String) {
//        confirmationDialog = mDialogClass.setDefaultDialog(
//            activity!!,
//            this,
//            "Cancel Job",
//            mContext.resources.getString(R.string.job_cancel)
//        )
//        confirmationDialog?.show()

        val msg = mContext.resources.getString(R.string.job_cancel)
        showCancelDialog(msg, orderId, orderStatus, lat, lon)
    }

    private fun onDetail(
        orderId: String,
        orderStatus: String,
        currDate: String,
        orderDate: String
    ) {
        driverJobCallbacks.onJobDetails(orderId, orderStatus,currDate,orderDate)
    }


    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        when (mKey) {
//            "Cancel Job" -> {
//                confirmationDialog?.dismiss()
//                driverJobCallbacks.onJobCanceled(orderId, trackStatus, lat, lon)
//            }
//
//            "Start Job" -> {
//                confirmationDialog?.dismiss()
//                driverJobCallbacks.onJobStarted(orderId, trackStatus, lat, lon)
//            }
        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            "Cancel Job" -> confirmationDialog?.dismiss()
            "Start Job" -> confirmationDialog?.dismiss()
        }
    }


    private fun showCancelDialog(
        msg: String,
        orderId: String,
        trackStatus: String,
        lat: String,
        lon: String
    ) {
        AlertDialog.Builder(activity)
//            .setTitle("Cancel")
            .setMessage(msg)
            .setPositiveButton(
                R.string.yes,
                { dialog, which ->
                    driverJobCallbacks.onJobCanceled(orderId, trackStatus, lat, lon)
                })
            .setNegativeButton(R.string.no, null)
            .setIcon(R.drawable.ic_alert)
            .show()
    }

    private fun showStartDialog(
        msg: String,
        orderId: String,
        trackStatus: String,
        lat: String,
        lon: String
    ) {
        AlertDialog.Builder(activity)
//            .setTitle("")
            .setMessage(msg)
            .setPositiveButton(
                R.string.yes,
                { dialog, which ->
                    val trackStatusNoUpdated = trackStatus.toInt() + 1
                    driverJobCallbacks.onJobStarted(
                        orderId,
                        trackStatusNoUpdated.toString(),
                        lat,
                        lon,
                        "Yes"
                    )
                })
            .setNegativeButton(R.string.no, { dialog, which ->
                val trackStatusNoUpdated = trackStatus.toInt() + 1
                driverJobCallbacks.onJobStarted(
                    orderId,
                    trackStatusNoUpdated.toString(),
                    lat,
                    lon,
                    "No"
                )
            })
            .setIcon(R.drawable.ic_alert)
            .show()
    }
}