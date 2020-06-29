package com.seasia.isms.auth

import android.util.Log
import androidx.annotation.NonNull
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.JsonObject
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.utils.BaseActivity


object FirebaseFunctions {
    const val TAG = "FirebaseFunctions"
    private val mPhoneList = ArrayList<String>()
    private var mOtpFirebase = OtpFirebaseActivity()

    private fun checkNumberExist() {
        val mDatabase = FirebaseDatabase.getInstance().reference.child("PhoneNumber")
        mDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    mPhoneList.add(data.key!!)
                }

                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })
    }

    @JvmStatic
    fun sendOTP(key: String, mJsonObject: JsonObject, baseActivity: BaseActivity) {
        checkNumberExist()
        var countryCode = "+" + mJsonObject.get("countryCode").toString().replace("\"", "")
        val phone = mJsonObject.get("phoneNumber").toString().replace("\"", "")
//        var countryCode = "+91"
//        val phone = mJsonObject.get("userName").toString().replace("\"", "")
        val phoneUtil = PhoneNumberUtil.getInstance()
        val isValid: Boolean
        var validNumber = false

        baseActivity.stopProgressDialog()

        try {
            val numberProto =
                phoneUtil.parse("$countryCode$phone", "RU") // Pass number & Country code
            //check whether the number is valid or no.
            isValid = phoneUtil.isValidNumber(numberProto)
            if (isValid) {
                if (mPhoneList.size > 0) {
                    for (i in mPhoneList.indices) {
                        if (mPhoneList[i] == phone) {
                            validNumber = true
                            UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.already_exist))
                        }
                    }
                    if (!validNumber) {
                        mOtpFirebase.otpValidation(
                            key,
                            phone,
                            baseActivity,
                            mJsonObject
                        )
                    }
                } else {
                    mOtpFirebase.otpValidation(
                        key,
//                        "+919465340220",
                        phone,
                        baseActivity,
                        mJsonObject
                    )
                }
            } else
                UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.invalid_number))
            println("Invalid Number")
        } catch (e: Exception) {
            UtilsFunctions.showToastError(e.localizedMessage)
            System.err.println("NumberParseException was thrown: $e")
        }
    }
}