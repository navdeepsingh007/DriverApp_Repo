package com.seasia.driverapp.model

data class OrderDetailResponse(
    val body: Body = Body(),
    val code: Int = 0,
    val message: String = ""
) {
    data class Body(
        val address: Address = Address(),
        val company: Company = Company(),
        val orderStatus: OrderStatus = OrderStatus(),
        val addressId: String = "",
        val assignedEmployees: ArrayList<AssignedEmployee> = arrayListOf(),
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
        val suborders: ArrayList<Suborder> = arrayListOf(),
        val totalOrderPrice: String = "",
        val trackStatus: Int = 0,
        val trackingLatitude: String = "",
        val trackingLongitude: String = "",
        val tip: String = "",
        val updatedAt: String = "",
        val userId: String = "",
        var deliveryInstructions: ArrayList<String>? = arrayListOf()
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
        val employee: Employee = Employee(),
        val id: String = ""
    )

    data class Suborder(
        val id: String = "",
        val quantity: String = "",
        val service: Service = Service(),
        val serviceId: String = ""
    )

    data class Employee(
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

    data class Company(
        val logo1: String? = "",

        val address1: String? = "",

        val companyName: String? = "",

        val totalRatings: String? = "",

        val latitude: String? = "",

        val rating: String? = "",

        val longitude: String? = ""
    )

    data class OrderStatus(
        val parentStatus: String? = "",

        val statusName: String? = "",

        val status: String? = ""
    )
}