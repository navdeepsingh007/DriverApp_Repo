package com.seasia.driverapp.constants

/*
 * Created by admin on 30-01-2018.
 */

object GlobalConstants {
    @JvmStatic
    val SEND_DATA = "DEMO_APP"

    @JvmStatic
    val ACCESS_TOKEN = "access_token"

    @JvmStatic
    val CUSTOMER_IAMGE = "customer_image"

    @JvmStatic
    val USER_IAMGE = "user_image"

    @JvmStatic
    val USERNAME = "username"

    @JvmStatic
    val USEREMAIL = "useremail"

    @JvmStatic
    val OUTLETEMAIL = "outletuseremail"

    @JvmStatic
    val USERDATA = "USERDATA"

    @JvmStatic
    val USERID = "USERID"

    @JvmStatic
    val JOBID = "JOBID"

    @JvmStatic
    val DEST_LAT = "DEST_LAT"

    @JvmStatic
    val DEST_LONG = "DEST_LONG"

    @JvmStatic
    val USER_LOCATION = "USERLOCATION"

    @JvmStatic
    var NOTIFICATION_TOKEN = "notification_token"

    @JvmStatic
    var SESSION_TOKEN = "session_token"

    @JvmStatic
    var JOB_STARTED = "job_started"
    //
    /* @JvmStatic
     val BASE_URL = "http://10.8.23.202:9062/"*/

    @JvmStatic
//    val BASE_URL = "http://51.79.40.224:9062/api/delivery/"  // lIVE
    val BASE_URL = "http://51.79.40.224:9075/api/delivery/"  // TESTING

    @JvmStatic
//    val BASE_SERVER = "http://51.79.40.224:9062/api/delivery/"  // LIVE
        val BASE_SERVER = "http://51.79.40.224:9072/api/delivery/"  // TESTING
    @JvmStatic
//    val SOCKET_URL = "http://51.79.40.224:9062"   // lIVE
    val SOCKET_URL = "http://51.79.40.224:9072"   // TESTING


    /* @JvmStatic
     val SOCKET_URL = "http://10.8.23.202:9062"*/
    const val PLATFORM = "android"

    @JvmStatic
    var LOGIN = "every"

    @JvmStatic
    val COMPANY = "Company"

    @JvmStatic
    val OUTLET = "Outlet"

    @JvmStatic
    val USER_REG_FEE = "USER_REG_FEE"

    @JvmStatic
    val YEARLYFEE = "YEARLY_FEE"

    @JvmStatic
    val MONTHLYFEE = "MONTHLYFEE"

    @JvmStatic
    val UPCOMING = "UPCOMING"

    @JvmStatic
    var selectedFragment = 2

    @JvmStatic
    var selectedCheckedFragment = 2

    @JvmStatic
    var GUEST_PASS_COUNT = "0"

    @JvmStatic
    var SHARED_PREF = "DEMOO_APP"

    @JvmStatic
    var OTP_VERIFICATION_ID = "OTP_VERIFICATION_ID"

    @JvmStatic
    var PROFILE_LOCAL_URL = "profileLocalUrl"

    @JvmStatic
    var USER_IMAGE = "userImage"

    @JvmStatic
    var USER_BANNER = "userBanner"

    @JvmStatic
    var USER_NAME = "userName"

    @JvmStatic
    var USER_EMAIL = "userEmail"

    // Driver Job started/completed
    @JvmStatic
    var JOB_STARTED_STATUS = "isJobStarted"

    @JvmStatic
    var JOB_ORDER_ID = "jobId"

    @JvmStatic
    var JOB_LAT = "jobLat"

    @JvmStatic
    var JOB_LON = "jobLon"


    @JvmStatic
    var JOB_COMPLETED_STATUS = "isJobCompleted"

    @JvmStatic
    var IS_JOB_STARTED = false

    @JvmStatic
    var IS_JOB_COMPLETED = false

    @JvmStatic
    var STARTED_JOB_ORDER_ID = "orderId"
}
