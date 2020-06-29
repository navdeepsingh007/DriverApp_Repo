package com.seasia.driverapp.utils

import android.content.Intent
import com.seasia.driverapp.views.*

class NavOptionsClick(private val context: BaseActivity) {

    fun onOptionClick(option: String) {
        when (option) {
            "Home" -> {
//                context.startActivity(Intent(context, ServicesListActivity::class.java))
            }
//            "Profile" -> {
//                context.startActivity(Intent(context, ProfileActivity::class.java))
//            }
            "Job History" -> {
                context.startActivity(Intent(context, JobHistoryActivity::class.java))
            }
            "My Profile" -> {
                context.startActivity(Intent(context, CustomerFeedbackActivity::class.java))
            }
            "Help" -> {
//                context.startActivity(Intent(context, DriverJourneyActivity::class.java))
            }
            "Terms and Conditions" -> {
                context.startActivity(Intent(context, TermsAndConditionsActivity::class.java))
//                context.startActivity(Intent(context, WebviewDemo::class.java))
            }
            "Privacy Policy" -> {
//                context.startActivity(Intent(context, WebviewDemo::class.java))
                context.startActivity(Intent(context, PrivacyPolicyActivity::class.java))
            }
            "Notifications" -> {
                context.startActivity(Intent(context, NotificationActivity::class.java))
            }
//            "Earnings" -> {
//                context.startActivity(Intent(context, DriverEarningsActivity::class.java))
//            }
        }
    }
}