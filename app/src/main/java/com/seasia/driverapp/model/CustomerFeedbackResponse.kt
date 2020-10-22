package com.seasia.driverapp.model

data class CustomerFeedbackResponse(
    val body: Body = Body(),
    val code: Int = 0,
    val message: String = ""
) {
    data class Body(
        val avgRating: Double = 0.0,
        val totalRating: Int = 0,
        val totalOrders: Int = 0,
        val ratings: ArrayList<Rating> = arrayListOf()
    )

    data class Rating(
        val id: String = "",
        val order: Order? = null,
        val rating: String = "",
        val ratingOn: String = "",
        val review: String = "",
        val createdAt: String = "",
        val user: User = User()
    )

    data class Order(
        val id: String = "",
        val serviceDateTime: String = ""
    )

    data class User(
        val firstName: String = "",
        val id: String = "",
        val image: String = "",
        val lastName: String = ""
    )
}

