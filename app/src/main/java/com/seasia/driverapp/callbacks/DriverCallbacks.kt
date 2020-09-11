package com.seasia.driverapp.callbacks

interface DriverJobCallbacks {
    fun onJobStarted(orderId: String, orderStatus: String, lat: String, lon: String, flagYesNo: String)
    fun onJobCanceled(orderId: String, orderStatus: String, lat: String, lon: String)
    fun onJobDetails(
        orderId: String,
        orderStatus: String,
        currDate: String,
        orderDate: String
    )
    fun onShowGMaps(lat: String, lon: String)
}

interface DashboardAssignedJobs {
    fun assignedJobsCount(size: Int)
}

interface JobCancelReason {
    fun onJobCancelReasonSelected(reasonId: String, reasonName: String)
}