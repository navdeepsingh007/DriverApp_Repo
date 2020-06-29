package com.seasia.driverapp.model

data class CancelOptionsResponse(
    var body: ArrayList<Body> = arrayListOf(),
    val code: Int = 0,
    val message: String = ""
) {
    data class Body(
        val companyId: String = "",
        val createdAt: String = "",
        var id: String = "",
        var reason: String = "",
        val status: Int = 0
    )
}