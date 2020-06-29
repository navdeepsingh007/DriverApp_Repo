package com.seasia.driverapp.model

data class NotificationResponse(
    val body: ArrayList<Body> = arrayListOf(),
    val code: Int = 0,
    val message: String = ""
) {
    data class Body(
        val createdAt: String = "",
        val id: String = "",
        val notificationDescription: String = "",
        val notificationTitle: String = "",
        val readStatus: Int = 0,
        val role: Int = 0,
        val status: Int = 0,
        val updatedAt: String = "",
        val userId: String = ""
    )
}