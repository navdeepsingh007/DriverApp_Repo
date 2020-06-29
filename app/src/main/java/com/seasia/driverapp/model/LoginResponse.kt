package com.seasia.driverapp.model

class LoginResponse {
    var code: Int? = null
    var message: String? = null
    var body: Data? = null

    inner class Data {
        var image: String? = null
        var coverImage: String = ""
        var idProof: String = ""
        var idProofName: String = ""
        var assignedServices: ArrayList<String>?= null
        var id: String? = null
        var companyId: String? = null
        var firstName: String? = null
        var lastName: String? = null
        var email: String? = null
        var phoneNumber: String? = null
        var countryCode: String? = null
        var dob: String? = null
        var address: String? = null
        var sessionToken: String? = null
        var platform: String? = null
        var status: Int? = null
    }
}