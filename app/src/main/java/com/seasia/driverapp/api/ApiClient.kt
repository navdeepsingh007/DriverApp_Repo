package com.seasia.driverapp.api
/*
 Created by Saira on 03/07/2019.
*/

import android.content.Intent
import android.icu.util.TimeUnit
import android.text.TextUtils
import androidx.annotation.NonNull
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.views.LoginUserActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ApiClient {
    @JvmStatic
    private val BASE_URL = GlobalConstants.BASE_URL
    private val sharedPrefClass = SharedPrefClass()

    @JvmStatic
    private var mApiInterface: ApiInterface? = null

    @JvmStatic
    fun getApiInterface(): ApiInterface {
        return setApiInterface()
    }

    @JvmStatic
    private fun setApiInterface(): ApiInterface {
        val lang = "en"
        var mAuthToken = GlobalConstants.SESSION_TOKEN

        if (mAuthToken == "session_token" && UtilsFunctions.checkObjectNull(
                SharedPrefClass().getPrefValue(
                    MyApplication.instance.applicationContext,
                    GlobalConstants.ACCESS_TOKEN
                )
            )
        ) {
            mAuthToken = sharedPrefClass.getPrefValue(
                MyApplication.instance,
                GlobalConstants.ACCESS_TOKEN
            ).toString()
        }

        val httpClient = OkHttpClient.Builder()
        .connectTimeout(1, java.util.concurrent.TimeUnit.MINUTES)
         .readTimeout(1, java.util.concurrent.TimeUnit.MINUTES)
         .writeTimeout(1, java.util.concurrent.TimeUnit.MINUTES)

        //Print Api Logs
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient.interceptors().add(logging)

        val mBuilder = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val isLogin = sharedPrefClass.getPrefValue(
            MyApplication.instance,
            "isLogin"
        ).toString()
        if (!TextUtils.isEmpty(mAuthToken) && mAuthToken == "session_token") {
            mAuthToken = ""
        }
        if (isLogin == "false") {
            mAuthToken = ""
        }
        /*  mAuthToken =
              "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwaG9uZV9udW1iZXIiOiI5NTMwNjA2MDA2IiwiY291bnRyeV9jb2RlIjoiKzkxIiwidHlwZSI6MiwiaWQiOjEsImlhdCI6MTU4NDAwODAyOSwiZXhwIjoxNTg0MTgwODI5fQ.hSkvHRBHlHlwf1Drg2dtPaMamRg27aI48H4ZOgWTilY"*/
        if (!TextUtils.isEmpty(mAuthToken)) {
            val finalMAuthToken = mAuthToken
            val interceptor: Interceptor = object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(@NonNull chain: Interceptor.Chain): Response {
                    val original = chain.request()
                    val builder = original.newBuilder()
                        .header("Authorization", finalMAuthToken)
                        .header("lang", lang)
                        .header("Content-Type", "application/json")
                    print("authToken=----- $finalMAuthToken")
                    print("lang=----- $lang")
                    val request = builder.build()
                    val response = chain.proceed(request)
                    return if (response.code() == 401) {
                        SharedPrefClass().putObject(
                            MyApplication.instance.applicationContext,
                            "isLogin",
                            false
                        )
                        val i = Intent(
                            MyApplication.instance.applicationContext,
                            LoginUserActivity::class.java
                        )
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        MyApplication.instance.applicationContext.startActivity(i)
                        response
                    } else response
                }
            }

            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor)
                mBuilder.client(httpClient.build())
                mApiInterface = mBuilder.build().create(ApiInterface::class.java)
            }
        } else
            mApiInterface = mBuilder.build().create(ApiInterface::class.java)

        return mApiInterface!!
    }

}
