package com.seasia.driverapp.constants

/**
 * Created by Navdeep Singh on 23/03/2020.
 */
object ApiKeysConstants {
    const val AUTHORIZATION = "Authorization"

    // lOGIN API
    const val PHONE_NO = "phoneNumber"
    const val OFFLINE_STATUS = "offlineStatus"
    const val COUNTRY_CODE = "countryCode"
    const val COMPANY_ID = "companyId"
    const val DEVICE_TOKEN = "deviceToken"
    const val PLATFORM = "platform"

    // Preofile API
    const val FIRST_NAME = "firstName"
    const val LAST_NAME = "lastName"
    const val EMAIL = "email"
    const val ADDRESS = "address"
    const val PROFILE_IMAGE = "profileImage"
    const val PROFILE_PROOF_NAME = "idProofName"
    const val PROFILE_PROOF_IMAGE = "idProof"
    const val PROFILE_BANNER_IMAGE = "coverImage"

    // Update order status
    const val ORDER_ID = "id"
    const val ORDER_STATUS = "status"

    // Cancel Order
    const val CANCEL_ORDER_ID = "orderId"
    const val CANCEL_REASON_ID = "reasonId"
    const val CANCEL_OTHER_REASON = "otherReason"
}