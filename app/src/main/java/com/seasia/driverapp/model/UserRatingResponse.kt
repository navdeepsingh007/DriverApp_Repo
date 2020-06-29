package com.seasia.driverapp.model

class UserRatingResponse {
    var code: Int? = null
    var message: String? = null
    var body: Body? = null

    inner class Body() {
        val msg: String? = null
    }
}