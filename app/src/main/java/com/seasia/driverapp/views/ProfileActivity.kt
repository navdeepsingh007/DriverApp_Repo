package com.seasia.driverapp.views

import android.content.Intent
import android.provider.MediaStore
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.ActivityProfileNewBinding
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.viewmodel.ProfileVM

class ProfileActivity : BaseActivity() {
    private val GALLERY_REQUEST_CODE = 1
    private lateinit var binding: ActivityProfileNewBinding
    private lateinit var profileVM: ProfileVM

    override fun getLayoutId(): Int {
        return R.layout.activity_profile_new
    }

    override fun initViews() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_new)
        profileVM = ViewModelProvider(this).get(ProfileVM::class.java)
        binding.profileViewModel = profileVM

        initToolbar()
        initGetProfileObserver()
        initUpdateProfileObserver()
        profileVM.onGetProfile()
        startProgressDialog()
        initNonEditableViews()
    }

    private fun initToolbar() {
        binding.toolbarCommon.imgToolbarText.text =
            resources.getString(R.string.profile)
        binding.toolbarCommon.imgToolbarText.setTextColor(
            resources.getColor(R.color.colorBlue)
        )
    }

    fun initGetProfileObserver() {
        profileVM.getProfileData().observe(this, Observer { response ->
            stopProgressDialog()

            if (response != null) {
                val message = response.message
                if (response.code == 200) {
                    binding.etFirstName.setText(response.body!!.firstName)
                    binding.etLastName.setText(response.body!!.lastName)
                    binding.etEmailAddress.setText(response.body!!.email)
                    binding.etAddress.setText(response.body!!.address)
                    binding.etPhoneNumber.setText(response.body!!.phoneNumber)
                    setImage(response.body!!.image!!)
                } else {
                    showToastError(message)
                }
            }
        })
    }

    fun initUpdateProfileObserver() {
        profileVM.updateProfileData().observe(this, Observer { response ->
            stopProgressDialog()

            if (response != null) {
                val message = response.message
                if (response.code == 200) {
                    showToastSuccess(message)
                    finish()
                } else {
                    showToastError(message)
                }
            }
        })
    }

    fun photoFromGallery(v: View) {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(
            i,
            GALLERY_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val columnIndex = cursor!!.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            ProfileVM.profileImage = picturePath

            MyApplication.sharedPref.putObject(this, GlobalConstants.PROFILE_LOCAL_URL, picturePath)
            UtilsFunctions.setLocalImage(this, picturePath, binding.imgProfile)
            cursor.close()
        }
    }

    private fun setImage(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.user)
            .into(binding.imgProfile)
    }

    private fun initNonEditableViews() {
        binding.toolbarCommon.tvEdit.visibility = View.VISIBLE
        binding.toolbarCommon.tvEdit.setOnClickListener { view ->
            binding.btnContinue.visibility = View.VISIBLE

            binding.etFirstName.isEnabled = true
            binding.etLastName.isEnabled = true
            binding.etEmailAddress.isEnabled = true
            binding.etPhoneNumber.isEnabled = true
            binding.etAddress.isEnabled = true
        }
    }
}