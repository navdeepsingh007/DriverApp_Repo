package com.seasia.driverapp.views

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.callbacks.ChoiceCallBack
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.ActivityProfileNewBinding
import com.seasia.driverapp.utils.BaseActivity
import com.seasia.driverapp.utils.CheckRuntimePermissions
import com.seasia.driverapp.utils.DialogClass
import com.seasia.driverapp.viewmodel.ProfileVM
import com.seasia.driverapp.viewmodel.ProfileVM.Companion.bannerImage
import com.seasia.driverapp.viewmodel.ProfileVM.Companion.profileImage
import com.seasia.driverapp.viewmodel.ProfileVM.Companion.userIdProof
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileNewActivity : BaseActivity(), ChoiceCallBack {
    private val GALLERY_REQUEST_CODE = 1
    private lateinit var binding: ActivityProfileNewBinding
    private lateinit var profileVM: ProfileVM

    private val RESULT_LOAD_IMAGE = 100
    private val CAMERA_REQUEST = 1888

    val REQUEST_PERMISSIONS = 1
    val RC_CODE_PICKER_PROOF = 2001

    val RC_PICKER_BANNER = 3001
    val PERMISSION_READ_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    var proofImages: MutableList<com.esafirm.imagepicker.model.Image> = mutableListOf()
    var bannerImages: MutableList<com.esafirm.imagepicker.model.Image> = mutableListOf()
//    private var profileImage = ""

    var IMAGE_CROP_IDENTIFIER = ""
    val CROP_PROFILE_IMAGE = "profile"
    val CROP_BANNER_IMAGE = "banner"
    val CROP_PROOF_IMAGE = "proof"

    var IMAGE_SELECT_IDENTIFIER = ""
    val SELECT_PROFILE_IMAGE = "profileSelected"
    val SELECT_BANNER_IMAGE = "bannerSelected"
    val SELECT_PROOF_IMAGE = "proofSelected"


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
        onEditBannerClick()
        profileVM.onGetProfile()
        startProgressDialog()
//        initNonEditableViews()

        onUpdateClickObserver()
        initErrorMsgObserver()
        loadingObserver()
//        imageButtonDisable()
    }

    private fun imageButtonDisable() {
        binding.imgIdPreview.isEnabled = false
        binding.imgProfile.isEnabled = false

        binding.llEditProfile.isEnabled = false
        binding.llEditProfile.isClickable = false
        binding.llEditProfile.background.alpha = 160
    }

    private fun imageButtonEnable() {
        binding.imgIdPreview.isEnabled = true
        binding.imgProfile.isEnabled = true

        binding.llEditProfile.isEnabled = true
        binding.llEditProfile.isClickable = true
        binding.llEditProfile.background.alpha = 250
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
                    binding.etIdProof.setText(response.body!!.idProofName)


                    MyApplication.sharedPref.putObject(
                        applicationContext,
                        GlobalConstants.USER_IMAGE,
                        response.body!!.image!!
                    )
                    MyApplication.sharedPref.putObject(
                        applicationContext,
                        GlobalConstants.USER_BANNER,
                        response.body!!.coverImage
                    )

                    setImage(response.body!!.image!!)
                    setIdProofImage(response.body!!.idProof)
                    setBannerImage(response.body!!.coverImage)
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

                    MyApplication.sharedPref.putObject(
                        applicationContext,
                        GlobalConstants.USER_IMAGE,
                        response.body!!.image
                    )

                    MyApplication.sharedPref.putObject(
                        applicationContext,
                        GlobalConstants.USER_BANNER,
                        response.body!!.coverImage
                    )

                    val userName = "${response.body!!.firstName} ${response.body!!.lastName}"
                    MyApplication.sharedPref.putObject(
                        applicationContext,
                        GlobalConstants.USER_NAME,
                        userName
                    )
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

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            val selectedImage = data.data

            when(IMAGE_SELECT_IDENTIFIER) {
                SELECT_PROFILE_IMAGE -> { IMAGE_CROP_IDENTIFIER = CROP_PROFILE_IMAGE }
                SELECT_BANNER_IMAGE -> { IMAGE_CROP_IDENTIFIER = CROP_BANNER_IMAGE }
                SELECT_PROOF_IMAGE -> { IMAGE_CROP_IDENTIFIER = CROP_PROOF_IMAGE }
            }

            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(selectedImage!!)
                .start(this)


//            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//            val cursor = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
//            cursor?.moveToFirst()
//            val columnIndex = cursor!!.getColumnIndex(filePathColumn[0])
//            val picturePath = cursor.getString(columnIndex)
//            profileImage = picturePath
//            setImage(picturePath)
//            cursor.close()
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK /*&& null != data*/) {
            when(IMAGE_SELECT_IDENTIFIER) {
                SELECT_PROFILE_IMAGE -> { setImage(profileImage) }
                SELECT_BANNER_IMAGE -> { setBannerImage(bannerImage) }
                SELECT_PROOF_IMAGE -> { setIdProofImage(userIdProof) }
            }
        } else if (requestCode == RC_CODE_PICKER_PROOF && resultCode == RESULT_OK && data != null) {
            IMAGE_CROP_IDENTIFIER = CROP_PROOF_IMAGE

            proofImages = ImagePicker.getImages(data)
            binding!!.imgIdPreview.visibility = View.VISIBLE
            val path = proofImages.get(0).path

//            userIdProof = proofImages.get(0).path
//            val requestOptions = RequestOptions()
//            Glide
//                .with(this)
//                .load(proofImages.get(0).path)
//                .apply(requestOptions)
//                .into(binding!!.imgIdPreview)

            // start cropping activity for pre-acquired image saved on the device
//            CropImage.activity(selectedImage!!)
//                .start(this)

        } else if (requestCode == RC_PICKER_BANNER && resultCode == RESULT_OK && data != null) {
            bannerImages = ImagePicker.getImages(data)
            bannerImage = bannerImages.get(0).path
            val requestOptions = RequestOptions()

            Glide
                .with(this)
                .load(bannerImages.get(0).path)
                .apply(requestOptions)
                .placeholder(R.drawable.no_image_available)
                .into(binding!!.ivBanner)
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                Log.d("ProfileNewActivity --> ", "uri - ${result.uri}")

                val resultUri = result.uri
                if (IMAGE_CROP_IDENTIFIER.equals(CROP_PROFILE_IMAGE)) {
                    setcroppedProfileImage(resultUri)
                } else if (IMAGE_CROP_IDENTIFIER.equals(CROP_BANNER_IMAGE)) {
                    setcroppedBannerImage(resultUri)
                } else if (IMAGE_CROP_IDENTIFIER.equals(CROP_PROOF_IMAGE)) {
                    setcroppedProofImage(resultUri)
                }
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                UtilsFunctions.showToastWarning("Something went wrong while cropping image..retry!")
            }
        }
    }

    private fun setCroppedIdProofImage(data: Intent) {
        // ID proof image
        proofImages = ImagePicker.getImages(data)
        binding!!.imgIdPreview.visibility = View.VISIBLE
        userIdProof = proofImages.get(0).path
        val requestOptions = RequestOptions()
        Glide
            .with(this)
            .load(proofImages.get(0).path)
            .apply(requestOptions)
            .into(binding!!.imgIdPreview)
    }

    private fun setcroppedProfileImage(selectedImage: Uri) {
        val path = selectedImage.path!!
        profileImage = path
        setImage(path)
    }

    private fun setcroppedProofImage(selectedImage: Uri) {
        val path = selectedImage.path!!
        userIdProof = path
        setIdProofImage(path)
    }

    private fun setcroppedBannerImage(selectedImage: Uri) {
        val path = selectedImage.path!!
        bannerImage = path
        setBannerImage(path)
    }


    private fun setImage(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.user)
            .into(binding.imgProfile)
    }

    private fun setIdProofImage(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.ic_attachment)
            .into(binding.imgIdPreview)
    }

    private fun setBannerImage(path: String) {
        Glide.with(this)
            .load(path)
            .placeholder(R.drawable.no_image_available)
            .into(binding.ivBanner)
    }

    private fun initNonEditableViews() {
        binding.toolbarCommon.tvEdit.visibility = View.VISIBLE
        binding.toolbarCommon.tvEdit.setOnClickListener { view ->
            binding.toolbarCommon.tvEdit.visibility = View.GONE
            binding.btnContinue.visibility = View.VISIBLE

            binding.etFirstName.isEnabled = true
            binding.etLastName.isEnabled = true
            binding.etEmailAddress.isEnabled = true
//            binding.etPhoneNumber.isEnabled = true
            binding.etAddress.isEnabled = true
            binding.etIdProof.isEnabled = true

            imageButtonEnable()
        }
    }


    fun onUpdateClickObserver() {
        profileVM.isClick().observe(
            this, Observer<String>(
                function =
                fun(it: String?) {
                    when (it) {
                        "img_profile" -> {
                            if (checkAndRequestPermissions()) {
                                DialogClass().setUploadConfirmationDialog(this, this, "gallery")
                            }
                            IMAGE_SELECT_IDENTIFIER = SELECT_PROFILE_IMAGE
                        }
                        "img_id_preview" -> {
                            if (checkAndRequestPermissions()) {
                                DialogClass().setUploadConfirmationDialog(this, this, "gallery")
                            }
                            IMAGE_SELECT_IDENTIFIER = SELECT_PROOF_IMAGE
//                            if (CheckRuntimePermissions.checkMashMallowPermissions(
//                                    this,
//                                    PERMISSION_READ_STORAGE, REQUEST_PERMISSIONS
//                                )
//                            ) {
//                                pickImage(RC_CODE_PICKER_PROOF)
//                            }
                        }

                    }
                })
        )
    }

    override fun photoFromGallery(mKey: String) {
        val i = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(i, RESULT_LOAD_IMAGE)
    }

    override fun photoFromCamera(mKey: String) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri =
                        FileProvider.getUriForFile(this, packageName + ".fileprovider", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                }
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //currentPhotoPath = File(baseActivity?.cacheDir, fileName)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents

            when(IMAGE_SELECT_IDENTIFIER) {
                SELECT_PROFILE_IMAGE -> { profileImage = absolutePath }
                SELECT_BANNER_IMAGE -> { bannerImage = absolutePath }
                SELECT_PROOF_IMAGE -> { userIdProof = absolutePath }
            }
//            profileImage = absolutePath
        }
    }


    override fun videoFromCamera(mKey: String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun videoFromGallery(mKey: String) {
        //("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun pickImage(RC_CODE_PICKER_: Int) {
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
            .folderMode(true) // set folder mode (false by default)
            .single()
            .limit(1)
            .toolbarFolderTitle(getString(R.string.folder)) // folder selection title
            .toolbarImageTitle(getString(R.string.gallery_select_title_msg))
            .start(RC_CODE_PICKER_)
    }

    private fun initErrorMsgObserver() {
        profileVM.getErrorMsg().observe(this, Observer {
            showToastError(it)
        })
    }

    private fun loadingObserver() {
        profileVM.isLoading().observe(this, Observer {
            if (it) {
                startProgressDialog()
            }
        })
    }

    private fun onEditBannerClick() {
        binding.llEditProfile.setOnClickListener {
//            if (CheckRuntimePermissions.checkMashMallowPermissions(
//                    this,
//                    PERMISSION_READ_STORAGE, REQUEST_PERMISSIONS
//                )
//            ) {
//                pickImage(RC_PICKER_BANNER)
//            }

            if (checkAndRequestPermissions()) {
                DialogClass().setUploadConfirmationDialog(this, this, "gallery")
            }
            IMAGE_SELECT_IDENTIFIER = SELECT_BANNER_IMAGE
        }
    }

/*    override fun onResume() {
        super.onResume()
//        updateBannerImage()
    }

    private fun updateBannerImage() {
        var picturePath =
            MyApplication.sharedPref.getPrefValue(
                this,
                GlobalConstants.USER_BANNER
            ) as String?

        if (picturePath == null) picturePath = ""

        UtilsFunctions.loadImage(
            this,
            picturePath,
            RequestOptions(),
            R.drawable.no_image_available,
            binding.ivBanner
        )
    }*/
}