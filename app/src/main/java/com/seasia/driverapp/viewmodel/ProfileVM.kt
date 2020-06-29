package com.seasia.driverapp.viewmodel

import android.view.View
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.model.LoginResponse
import com.seasia.driverapp.repo.ProfileRepo

import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileVM : BaseViewModel() {
    private var profileRepo: ProfileRepo
    private var onClicked: MutableLiveData<String>
    private var isLoading: MutableLiveData<Boolean>
    private var getProfileResponse: LiveData<LoginResponse>
    private var updateProfileResponse: LiveData<LoginResponse>
    private var postErrorMsg: MutableLiveData<String>

    init {
        profileRepo = ProfileRepo()
        onClicked = MutableLiveData()
        isLoading = MutableLiveData()
        getProfileResponse = profileRepo.getProfileResponse(null)
        updateProfileResponse = profileRepo.updateProfileResponse(null, null, null, null)
        postErrorMsg = MutableLiveData()
    }

    fun getProfileData(): LiveData<LoginResponse> {
        return getProfileResponse
    }

    fun updateProfileData(): LiveData<LoginResponse> {
        return updateProfileResponse
    }

    fun onUpdateProfileClick(
        v: View, fName: EditText, lName: EditText, phoneNo: EditText,
        mail: EditText, addr: EditText, profileImg: CircleImageView, etIdProof: EditText
    ) {
        val firstName = fName.text.toString()
        val lastName = lName.text.toString()
        val phoneNumber = phoneNo.text.toString()
        val emailAddress = mail.text.toString()
        val address = addr.text.toString()
        val idProofName = etIdProof.text.toString()

        val allFields = listOf(firstName, lastName, phoneNumber, emailAddress, address, idProofName)
        val allMsg = listOf("First Name", "Last Name", "Phone Number", "Email", "Address", "ID Proof")

        for ((index, field) in allFields.withIndex()) {
            if (field.isBlank()) {
                postErrorMsg.value = "Please enter ${allMsg[index]}"
                return
            }
        }

        if (UtilsFunctions.isNetworkConnected()) {
            createMultipartRequest(firstName, lastName, emailAddress, address, idProofName)
        } else {
            UtilsFunctions.showToastWarning(MyApplication.instance.getString(R.string.internet_connection))
        }
    }

    fun createMultipartRequest(
        firstName: String,
        lastName: String,
        email: String,
        address: String,
        idProofName: String
    ) {
        val reqFirstName: RequestBody = createPartFromString(firstName)
        val reqLastName: RequestBody = createPartFromString(lastName)
        val reqEmail: RequestBody = createPartFromString(email)
        val reqAddress: RequestBody = createPartFromString(address)
        val reqIdProof: RequestBody = createPartFromString(idProofName)

        val map: HashMap<String, RequestBody> = HashMap()
        map[ApiKeysConstants.FIRST_NAME] = reqFirstName
        map[ApiKeysConstants.LAST_NAME] = reqLastName
        map[ApiKeysConstants.EMAIL] = reqEmail
        map[ApiKeysConstants.ADDRESS] = reqAddress
        map[ApiKeysConstants.PROFILE_PROOF_NAME] = reqIdProof

        val proofImage: MultipartBody.Part = prepareFilePart(ApiKeysConstants.PROFILE_PROOF_IMAGE, userIdProof)
        val bannerImage: MultipartBody.Part = prepareFilePart(ApiKeysConstants.PROFILE_BANNER_IMAGE, bannerImage)
        val file: MultipartBody.Part = prepareFilePart(ApiKeysConstants.PROFILE_IMAGE, profileImage)

        updateProfileResponse = profileRepo.updateProfileResponse(map, file, proofImage, bannerImage)
        isLoading.postValue(true)
    }

    // This method  converts String to RequestBody
    fun createPartFromString(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), value)
    }

    fun prepareFilePart(partName: String, realPath: String): MultipartBody.Part {
        var fileToUpload: MultipartBody.Part? = null

        if (!realPath.isEmpty()) {
            try {
                val file = File(realPath)
                if (file.exists()) {
                    val requestBody: RequestBody = RequestBody.create(MediaType.parse("*/*"), file)
                    fileToUpload =
                        MultipartBody.Part.createFormData(partName, file.name, requestBody)
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        } else {
            val attachmentEmpty = RequestBody.create(MediaType.parse("text/plain"), "")
            fileToUpload = MultipartBody.Part.createFormData(partName, "", attachmentEmpty)
        }
        return fileToUpload!!
    }

    fun onGetProfile() {
        if (UtilsFunctions.isNetworkConnected()) {
            val jObj = JsonObject()
            getProfileResponse = profileRepo.getProfileResponse(jObj)
            isLoading.postValue(true)
        } else {
            postErrorMsg.postValue(MyApplication.instance.getString(R.string.internet_connection))
        }
    }

    fun profilePicUpdate(v: View) {
        onClicked.postValue("updateProfilePic")
    }

    fun getErrorMsg(): LiveData<String> {
        return postErrorMsg
    }


    ////////////

    override fun isLoading(): LiveData<Boolean> {
        return isLoading
    }

    override fun isClick(): LiveData<String> {
        return onClicked
    }

    override fun clickListener(v: View) {
        onClicked.value = v.resources.getResourceName(v.id).split("/")[1]
    }

    companion object {
        var profileImage = ""
        var userIdProof = ""
        var bannerImage = ""
    }
}