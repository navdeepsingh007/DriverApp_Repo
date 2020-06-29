package com.seasia.driverapp.model

data class OrderStatusNewResponse(
    val body: List<Body> = listOf(),
    val code: Int = 0,
    val message: String = ""
) {
    data class Body(
        val address: Address = Address(),
        val addressId: String = "",
        val assignedEmployees: List<AssignedEmployee> = listOf(),
        val cancellable: Boolean = false,
        val cancellationReason: String = "",
        val companyId: String = "",
        val createdAt: String = "",
        val currency: String = "",
        val id: String = "",
        val offerPrice: String = "",
        val orderNo: String = "",
        val orderPrice: String = "",
        val progressStatus: Int = 0,
        val promoCode: String = "",
        val serviceCharges: String = "",
        val serviceDateTime: String = "",
        val suborders: List<Suborder> = listOf(),
        val totalOrderPrice: String = "",
        val trackStatus: Int = 0,
        val trackingLatitude: String = "",
        val trackingLongitude: String = "",
        val updatedAt: String = "",
        val user: User = User(),
        val userId: String = ""
    )

    data class Address(
        val addressName: String = "",
        val addressType: String = "",
        val city: String = "",
        val houseNo: String = "",
        val id: String = "",
        val landmark: String = "",
        val latitude: String = "",
        val longitude: String = "",
        val town: String = ""
    )

    data class AssignedEmployee(
        val id: String = ""
    )

    data class Suborder(
        val id: String = "",
        val quantity: String = "",
        val service: Service = Service(),
        val serviceId: String = ""
    )

    data class User(
        val countryCode: String = "",
        val firstName: String = "",
        val id: String = "",
        val image: String = "",
        val lastName: String = "",
        val phoneNumber: String = ""
    )

    data class Service(
        val description: String = "",
        val duration: String = "",
        val icon: String = "",
        val id: String = "",
        val name: String = "",
        val price: String = "",
        val thumbnail: String = "",
        val type: String = ""
    )
}