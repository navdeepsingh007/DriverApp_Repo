package com.seasia.isms.auth

interface OtpReceivedInterface {
    fun onOtpReceived(otp: String?)
    fun onOtpTimeout()
}