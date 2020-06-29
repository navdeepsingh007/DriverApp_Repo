package com.seasia.driverapp.application

import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.utils.FontStyle


class MyApplication : MultiDexApplication() {
    private var customFontFamily: FontStyle? = null
    override fun onCreate() {
        MultiDex.install(this)
        super.onCreate()

        instance = this
        gson = GsonBuilder().serializeNulls().create()
        sharedPref = SharedPrefClass()

        MultiDex.install(this)
        customFontFamily = FontStyle.instance
        customFontFamily!!.addFont("regular", "Montserrat-Regular_0.ttf")
        customFontFamily!!.addFont("semibold", "Montserrat-Medium_0.ttf")
        customFontFamily!!.addFont("bold", "Montserrat-SemiBold_0.ttf")
    }


    companion object {
        /**
         * @return ApplicationController singleton instance
         */
        @get:Synchronized
        lateinit var instance: MyApplication
        lateinit var gson: Gson
        lateinit var sharedPref: SharedPrefClass
    }
}