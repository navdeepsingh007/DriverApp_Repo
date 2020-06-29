package com.seasia.driverapp.viewmodel.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.view.View
import com.google.gson.JsonObject
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.model.CommonModel
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.viewmodel.BaseViewModel

class TrackingViewModel {/* : BaseViewModel() {
    private val mIsUpdating = MutableLiveData<Boolean>()
    private val isClick = MutableLiveData<String>()
    private var completejob = MutableLiveData<CommonModel>()
    private var homeJobsRepository = HomeJobsRepository()

    init {
        if (UtilsFunctions.isNetworkConnectedWithoutToast()) {
            completejob = homeJobsRepository.startCompleteJob(null)
        }
    }

    override fun isLoading() : LiveData<Boolean> {
        return mIsUpdating
    }

    override fun isClick() : LiveData<String> {
        return isClick
    }

    override fun clickListener(v : View) {
        isClick.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    fun startCompleteJob() : LiveData<CommonModel> {
        return completejob
    }

    fun startJob(status : String, jobId : String) {
        if (UtilsFunctions.isNetworkConnected()) {
            var jsonObject = JsonObject()
            jsonObject.addProperty(
                "employeeId",
                SharedPrefClass()!!.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.USERID
                ) as String
            )
            jsonObject.addProperty("progressStatus", status)
            jsonObject.addProperty("id", jobId)
            completejob = homeJobsRepository.startCompleteJob(jsonObject)
            mIsUpdating.postValue(true)
        }

    }
*/
}