package com.seasia.driverapp.model


class WalletResponse {
    var code: Int? = null

    var message: String? = null

    var body: ArrayList<Body>?=null
    public class Body{
        var empId: String? = null

        var createdAt: String? = null

        var amount: String? = null

        var companyId: String? = null

        var payType: String? = null

        var orderId: String? = null

        var id: String? = null

        var updatedAt: String? = null

        var order: Order? = null

        public class Order{
            var createdAt: String? = null

            var orderNo: String? = null

            var id: String? = null

        }

    }
}