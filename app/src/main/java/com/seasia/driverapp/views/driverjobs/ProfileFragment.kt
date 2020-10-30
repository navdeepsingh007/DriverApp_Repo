package com.seasia.driverapp.views.driverjobs


import android.Manifest
import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.google.android.material.appbar.AppBarLayout
import com.seasia.driverapp.R
import com.seasia.driverapp.adapters.CustomerFeedbackAdapter
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.callbacks.ChoiceCallBack
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.ActivityCustomerFeedbackBinding
import com.seasia.driverapp.databinding.ActivityProfileNewBinding
import com.seasia.driverapp.databinding.FragmentProfileBinding
import com.seasia.driverapp.model.CustomerFeedbackResponse
import com.seasia.driverapp.utils.BaseFragment
import com.seasia.driverapp.utils.DialogClass
import com.seasia.driverapp.viewmodel.CustFeedbackVM
import com.seasia.driverapp.viewmodel.ProfileVM
import com.seasia.driverapp.views.ProfileNewActivity
import com.theartofdev.edmodo.cropper.CropImage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : BaseFragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var custFeedbackVM: CustFeedbackVM

    override fun getLayoutResId(): Int {
        return R.layout.fragment_profile
    }

    override fun initView() {
        binding =viewDataBinding as FragmentProfileBinding
        custFeedbackVM = ViewModelProvider(this).get(CustFeedbackVM::class.java)

//        setSupportActionBar(binding.toolbar)

//        binding.tvTotalOrders.text = "10 total orders - "
//        binding.tvTotalFeedback.text = "4 overall feedbacks"

        initCustFeedbackObserver()
        startProgressDialog()
        //initToolbar()
        onEditProfileClick()

        onCollapseBarChanged()
    }

    override fun onResume() {
        super.onResume()

        userPicBackground()
        initDriverProfileDetail()
        updateBannerImage()
    }

    private fun userPicBackground() {
//        val color: Drawable = ColorDrawable(resources.getColor(R.color.colorWhite))
//        val image = resources.getDrawable(R.drawable.shape_round_corner)
//
//        val ld = LayerDrawable(arrayOf(color, image))
//        binding.ivDriverImage.setImageDrawable(ld)

        binding.ivDriverImage.setImageDrawable(ColorDrawable(resources.getColor(R.color.colorWhite)))
    }

    private fun onEditProfileClick() {
        binding.llEditProfile.setOnClickListener {
            startActivity(Intent(requireActivity(), ProfileNewActivity::class.java))
        }
    }

//    private fun initToolbar() {
//        binding.commonToolBar.imgToolbarText.text =
//            resources.getString(R.string.customer_feedback)
//        binding.commonToolBar.imgToolbarText.setTextColor(
//            resources.getColor(R.color.colorBlue)
//        )
//    }

    private fun initCustFeedbackObserver() {
        custFeedbackVM.customerFeedbacks().observe(this,
            Observer<CustomerFeedbackResponse> { response ->
                if (response != null) {
                    val message = response.message
                    stopProgressDialog()
                    when {
                        response.code == 200 -> {
                            if (!response.body.ratings.isEmpty()) {
                                setCustFeedbackAdapter(response.body.ratings)
                                binding.ratingBar.rating =
                                    response.body.avgRating.toFloat()

                                val orders = "${response.body.totalOrders} total orders - "
                                binding.tvTotalOrders.text = orders
                                val feedback = "${response.body.totalRating} overall feedbacks"
                                binding.tvTotalFeedback.text = feedback

                            } else {
                                message?.let {
//                                    UtilsFunctions.showToastError(it)
                                }
                            }
                        }
                        response.code == 204 -> {
                            binding.myProfile.rvRateListing.visibility = View.GONE
                            binding.myProfile.tvNoRecord.visibility = View.VISIBLE
                        }
                        else -> message?.let {
                            UtilsFunctions.showToastError(it)
                        }
                    }
                } else {
                    stopProgressDialog()
                }
            })
    }

    private fun initDriverProfileDetail() {
        val picturePath =
            MyApplication.sharedPref.getPrefValue(
                requireActivity(),
                GlobalConstants.USER_IMAGE
            ) as String?

        UtilsFunctions.loadImage(
            requireActivity(),
            picturePath!!,
            RequestOptions(),
            R.drawable.user,
            binding.ivDriverImage
        )

        val userName = MyApplication.sharedPref.getPrefValue(
            requireActivity(),
            GlobalConstants.USER_NAME
        ) as String?
        binding.tvName.text = userName
    }

    private fun setCustFeedbackAdapter(response: ArrayList<CustomerFeedbackResponse.Rating>) {
        if (response.size > 0) {
            val rateListAdapter = CustomerFeedbackAdapter(this, response, requireActivity())
            val linearLayoutManager = LinearLayoutManager(requireActivity())
            linearLayoutManager.orientation = RecyclerView.VERTICAL
            binding.myProfile.rvRateListing.layoutManager = linearLayoutManager
            binding.myProfile.rvRateListing.adapter = rateListAdapter

            binding.myProfile.rvRateListing.visibility = View.VISIBLE
            binding.myProfile.tvNoRecord.visibility = View.GONE
        } else {
            binding.myProfile.rvRateListing.visibility = View.GONE
            binding.myProfile.tvNoRecord.visibility = View.VISIBLE
        }
    }

    private fun updateBannerImage() {
        var picturePath =
            MyApplication.sharedPref.getPrefValue(
                requireActivity(),
                GlobalConstants.USER_BANNER
            ) as String?

        if (picturePath == null) picturePath = ""

        UtilsFunctions.loadImage(
            requireActivity(),
            picturePath,
            RequestOptions(),
            R.drawable.no_image_available,
            binding.ivFeedback
        )
    }

    private fun onCollapseBarChanged() {
        val appBar = binding.appBar
        val collapseToolbar = binding.toolbarLayout
        val collapsedTitle = "My Profile"
        val expandedTitle = ""

/*        appBarLayout.addOnOffsetChangedListener(OnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (verticalOffset == 0 || verticalOffset <= toolbar.getHeight() && !toolbar.getTitle()
                    .equals(collapsedTitle)
            ) {
                collapseToolbar.setTitle(collapsedTitle)
            } else if (!toolbar.getTitle().equals(expandedTitle)) {
                collapseToolbar.setTitle(expandedTitle)
            }
        })*/

        appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            Log.d("offset --> ", "${Math.abs(verticalOffset)} - ${appBarLayout.totalScrollRange}")
            if (Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0) {
                // Collapsed
                collapseToolbar.setTitle(expandedTitle)
              //  binding.commonToolBar.root.visibility = View.VISIBLE
                binding.ivDriverImage.background.alpha = 50
                binding.ivDriverImage.visibility = View.INVISIBLE
            } else if (Math.abs(verticalOffset) >= 300) {
                binding.ivDriverImage.background.alpha = 50
                binding.ivDriverImage.visibility = View.INVISIBLE
            }  else if (Math.abs(verticalOffset) >= 300) {
                binding.ivDriverImage.background.alpha = 50
                binding.ivDriverImage.visibility = View.INVISIBLE
            } else if (verticalOffset == 0) {
                // Expanded
                collapseToolbar.setTitle(expandedTitle)
            //    binding.commonToolBar.root.visibility = View.VISIBLE
                binding.ivDriverImage.visibility = View.VISIBLE
                binding.ivDriverImage.background.alpha = 255
            } else {
                // Idle
                collapseToolbar.setTitle(expandedTitle)
               // binding.commonToolBar.root.visibility = View.VISIBLE
                binding.ivDriverImage.visibility = View.VISIBLE
                binding.ivDriverImage.background.alpha = 255
            }
        }
        )
    }
}