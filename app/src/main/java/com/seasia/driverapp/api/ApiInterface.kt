package com.seasia.driverapp.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface ApiInterface {
    @POST("auth/login/")
    fun callLogin(@Body jsonObject: JsonObject): Call<JsonObject>

    @POST("auth/logout/")
    fun callLogout(): Call<JsonObject>

    @GET("profile/getprofile/")
    fun getProfileData(): Call<JsonObject>

    @Multipart
    @POST("profile/updateprofile/")
    fun updateProfileData(
        @PartMap partMap: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part file: MultipartBody.Part,
        @Part proof: MultipartBody.Part,
        @Part banner: MultipartBody.Part
    ): Call<JsonObject>

    // progressStatus = 1 [Pending Jobs], progressStatus = 5 [Completed Jobs]
    @Headers("Content-Type: application/json")
    @GET("orders/list")
    fun getAssignedOrCompletedJobs(@Query("progressStatus") progressStatus: String,
                                   @Header("companyId") companyId: String): Call<JsonObject>

    // status = 0 [Not Started], 1 [Started from location], 2 [Reached at destination]
    // status = 3 [Job Started], 4 [Job completed from driver side]
    @POST("orders/status/")
    fun updateJobStatus(@Body jsonObject: JsonObject,
                        @Header("companyId") companyId: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("orders/getCancelReasons/")
    fun getCancelReasons(@Header("companyId") companyId: String): Call<JsonObject>

    @POST("orders/submitCancel")
    fun cancelJob(@Header("companyId") companyId: String, @Body jsonObject: JsonObject): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("orders/feedbacklist/")
    fun getFeedbackList(@Header("companyId") companyId: String): Call<JsonObject>

    @GET("orders/detail/{id}")
    fun getOrderDetail(@Path("id") orderId: String,
                       @Header("companyId") companyId: String): Call<JsonObject>


    @Headers("Content-Type: application/json")
    @GET("notification/")
    fun getNotificationsList(@Header("companyId") companyId: String): Call<JsonObject>

    @DELETE("notification/clearAll/")
    fun deleteAllNotifications(): Call<JsonObject>



    @Multipart
    @PUT("api/employee/update")
    fun callUpdateProfile(
        @PartMap mHashMap: HashMap<String, RequestBody>,
        @Part image: MultipartBody.Part?
    ): Call<JsonObject>


//    @Headers("Content-Type: application/json")
//    @POST("api/loginEmployee")
//    fun callLogin(@Body jsonObject : JsonObject) : Call<JsonObject>

    /*@POST("login/")
    fun callLogin(@Body jsonObject : JsonObject) : Call<JsonObject>*/
    @Multipart
    @POST("register/")
    fun finishRegistartion(
        @PartMap mHashMap: HashMap<String,
                RequestBody>, @Part image: MultipartBody.Part?
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("checkPhoneNumber/")
    fun checkPhoneExistence(@Body jsonObject: JsonObject): Call<JsonObject>

//    @GET("api/logout")
//    fun callLogout(/*@Body mJsonObject : JsonObject*/) : Call<JsonObject>

    @POST("resetpassword/")
    fun resetPassword(@Body mJsonObject: JsonObject): Call<JsonObject>

    //@POST("resetpassword/")
    //fun getProfile(@Body mJsonObject : JsonObject) : Call<JsonObject>
    @POST("users/changepassword/")
    fun chnagePassword(@Body mJsonObject: JsonObject): Call<JsonObject>

    @GET("api/employee/get/{id}")
    fun getProfile(@Path("progressStatus") progressId: String): Call<JsonObject>


    @GET("api/vehicle/vehicleDriverlist")
    fun getVehicleList(): Call<JsonObject>

    @GET("api/service/list")
    fun getServicesList(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: String
    ): Call<JsonObject>//(@Query("status") status : String) : Call<JsonObject>

    @GET("api/fuel/getList")
    fun getFuelEntryList(): Call<JsonObject>

    @GET("api/notification/list/{id}")
    fun getNotificationList(@Path("id") id: String): Call<JsonObject>

    @DELETE("api/notification/delete/{id}")
    fun clearAllNotification(@Path("id") id: String): Call<JsonObject>

    @GET("api/vendor/list")
    fun getVendorList(@Query("page") page: Int, @Query("limit") limit: Int): Call<JsonObject>

    /* @GET("job/getDriverJob")
     fun getJobs(@Query("acceptStatus") userId : String) : Call<JsonObject>*/
    @POST("api/job/getDriverJobs")
    fun getJobs(@Body mJsonObject: JsonObject): Call<JsonObject>

    @GET("job/driver/jobsHistory")
    fun getJobsHistory(@Query("progressStatus") userId: String): Call<JsonObject>

    @GET("api/job/jobsHistory")
    fun getJobsHistory1(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("progressStatus") progressStatus: Int
    ): Call<JsonObject>

    @POST("api/job/changeJobStatus")
    fun startCompleteJob(@Body mJsonObject: JsonObject): Call<JsonObject>

    @POST("api/job/AcceptReject")
    fun acceptRejectJob(@Body mJsonObject: JsonObject): Call<JsonObject>

    @Multipart
    @POST("api/fuel/addFuel")
    fun callAddFuelEntry(
        @PartMap mHashMap: HashMap<String,
                RequestBody>, @Part image: MultipartBody.Part?
    ): Call<JsonObject>

    @Multipart
    @PUT("api/service/update")
    fun callUpdateService(
        @PartMap mHashMap: HashMap<String,
                RequestBody>, @Part image: MultipartBody.Part?
    ): Call<JsonObject>

}