package com.seasia.driverapp.views.driverjobs

import android.app.Activity
import android.app.Dialog
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seasia.driverapp.R
import com.seasia.driverapp.adapters.NotificationsAdapter
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.DialogUtil
import com.seasia.driverapp.common.DialogsInterface
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.databinding.FragmentNotificationBinding
import com.seasia.driverapp.model.NotificationResponse
import com.seasia.driverapp.utils.BaseFragment
import com.seasia.driverapp.viewmodel.NotificationlVM
import com.seasia.driverapp.views.DashboardNewActivity


class NotificationFragment : BaseFragment(), DialogsInterface {
    private lateinit var binding: FragmentNotificationBinding
    private lateinit var notificationVM: NotificationlVM
    private lateinit var companyId: String
    private lateinit var dialog: Dialog
    private var notificationsList = ArrayList<NotificationResponse.Body>()
    private val DELETE_NOTIFICATION = "Delete notifications!"
    override fun getLayoutResId(): Int {
        return R.layout.fragment_notification
    }

    override fun initView() {
        binding = viewDataBinding as FragmentNotificationBinding
        notificationVM = ViewModelProvider(this).get(NotificationlVM::class.java)

        setNotificationsAdapter()
//        initCompletedJobsObserver()

        companyId = MyApplication.sharedPref.getPrefValue(
            requireActivity(),
            ApiKeysConstants.COMPANY_ID
        ) as String

//        initToolbar()
        initNotificationListObserver()
        startProgressDialog()
       /// initDeleteButton()
    }


        fun deleteAllNotifications() {
                dialog = DialogUtil.showDialog(requireActivity(), this, DELETE_NOTIFICATION, getString(R.string.delete_notifications))

            }





//    private fun initDeleteButton() {
//        binding.commonToolBar.ivDeleteNotification.visibility = View.VISIBLE
//        binding.commonToolBar.ivDeleteNotification.setOnClickListener {
//
//            dialog = DialogUtil.showDialog(
//               requireActivity(),
//                this,
//                DELETE_NOTIFICATION,
//                getString(R.string.delete_notifications)
//            )
////            onNotificationDeleteObserver()
//        }
//    }

//    private fun initToolbar() {
//        binding.commonToolBar.imgToolbarText.text =
//            resources.getString(R.string.notifications)
//        binding.commonToolBar.imgToolbarText.setTextColor(
//            resources.getColor(R.color.colorBlue)
//        )
//    }

    private fun initNotificationListObserver() {
        notificationVM.getNotificationsList().observe(this,
            Observer<NotificationResponse> { response ->
                if (response != null) {
                    val message = response.message
                    stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            if (!response.body.isEmpty()) {
                                notificationsList.addAll(response.body)

                                setNotificationsAdapter()
                            } else {
                                message?.let {
//                                    UtilsFunctions.showToastError(it)
                                }
                            }
                        }
                        response.code == 204 -> {
                            // No record found
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                } else {
                    stopProgressDialog()
                }
            })
    }

    private fun onNotificationDeleteObserver() {
        notificationVM.onNotificationDelete().observe(this, Observer { response ->
            //stopProgressDialog()

            if (response != null) {
                val message = response.message!!
                if (response.code == 200) {
                    UtilsFunctions.showToastSuccess(message)
                    requireActivity().finish()
                } else {
                    UtilsFunctions.showToastError(message)
                }
            } else {
                UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
            }
        })
    }

    private fun setNotificationsAdapter() {
        if (notificationsList.size > 0) {
            val notificationAdapter = NotificationsAdapter(this, notificationsList)

            val linearLayoutManager = LinearLayoutManager(activity)
            linearLayoutManager.orientation = RecyclerView.VERTICAL
            binding.rvJobHistory.layoutManager = linearLayoutManager
            binding.rvJobHistory.adapter = notificationAdapter

            binding.rvJobHistory.visibility = View.VISIBLE
            binding.tvNoRecord.visibility = View.GONE
        } else {
            binding.rvJobHistory.visibility = View.GONE
            binding.tvNoRecord.visibility = View.VISIBLE
        }
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        startProgressDialog()
        when (mKey) {
            DELETE_NOTIFICATION -> onNotificationDeleteObserver()
        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            DELETE_NOTIFICATION -> dialog.dismiss()
        }
    }
}