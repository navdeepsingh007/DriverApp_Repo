package com.seasia.driverapp.views.driverjobs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.seasia.driverapp.R
import com.seasia.driverapp.application.MyApplication
import com.seasia.driverapp.common.DialogUtil
import com.seasia.driverapp.common.DialogsInterface
import com.seasia.driverapp.common.UtilsFunctions
import com.seasia.driverapp.constants.ApiKeysConstants
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.FragmentSettingBinding
import com.seasia.driverapp.model.OfflineStatusInput
import com.seasia.driverapp.model.OfflineStatusResponse
import com.seasia.driverapp.sharedpreference.SharedPrefClass
import com.seasia.driverapp.utils.BaseFragment
import com.seasia.driverapp.viewmodel.JobsViewModel
import com.seasia.driverapp.viewmodel.LoginVM
import com.seasia.driverapp.views.LoginUserActivity
import com.seasia.driverapp.views.WalletHistoryActivity
import com.seasia.driverapp.views.WebActivity


class SettingFragment : BaseFragment(), View.OnClickListener, DialogsInterface {
    var binding: FragmentSettingBinding? = null
    private lateinit var dialog: Dialog
    private lateinit var loginVM: LoginVM
    var mStatus="0"
    private  var jobsViewModel: JobsViewModel?=null
    override fun getLayoutResId(): Int {
        return R.layout.fragment_setting
    }

    override fun initView() {
        binding = viewDataBinding as FragmentSettingBinding
        loginVM = ViewModelProvider(this).get(LoginVM::class.java)
        jobsViewModel = ViewModelProviders.of(this).get(JobsViewModel::class.java)
        binding!!.tvTerms.setOnClickListener(this)
        binding!!.tvPrivacy.setOnClickListener(this)
        binding!!.tvWallet.setOnClickListener(this)
        binding!!.tvRateUs.setOnClickListener(this)
        binding!!.tvShareApp.setOnClickListener(this)
        binding!!.tvLogout.setOnClickListener(this)
        onlineStatus()
    }


    fun onlineStatus(){
        mStatus=   SharedPrefClass().getPrefValue(MyApplication.instance.applicationContext, ApiKeysConstants.OFFLINE_STATUS).toString()
        if(mStatus.equals("1")){
            binding!!.btnOffline.setText(getString(R.string.online))
            binding!!.btnOffline.isChecked=true
        }
        binding!!.btnOffline.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding!!.btnOffline.setText(getString(R.string.online))
                offlineStatus("1")
                mStatus="1"
            } else {
                binding!!.btnOffline.setText(getString(R.string.offline))
                offlineStatus("0")
                mStatus="0"
            }
        })
    }


    fun offlineStatus(status: String) {
        var input= OfflineStatusInput()
        input.status=status
        baseActivity.startProgressDialog()
        jobsViewModel!!.offlineStatus(input)
        jobsViewModel!!.offlineResponse().observe(this,
            Observer<OfflineStatusResponse> { response ->
                if(MyApplication.callApi) {
                    MyApplication.callApi =false
                    baseActivity.stopProgressDialog()
                    if (response != null) {
                        val message = response.message
                        when {
                            response.code == 200 -> {
                                UtilsFunctions.showToastSuccess(response.message!!)

                                SharedPrefClass().putObject(
                                    MyApplication.instance,
                                    ApiKeysConstants.OFFLINE_STATUS,
                                    mStatus
                                )

                            }
                            else -> message?.let {
                                UtilsFunctions.showToastError(response.message!!)
                            }
                        }
                    }
                    jobsViewModel!!.rejectOrderResponse().removeObservers(this)
                }
            })

    }



    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tv_terms -> {
                val intent1 = Intent(requireActivity(), WebActivity::class.java)
                intent1.putExtra("title", getString(R.string.terms_condition))
                startActivity(intent1)

            }
            R.id.tv_privacy -> {
                val intent1 = Intent(requireActivity(), WebActivity::class.java)
                intent1.putExtra("title", getString(R.string.privacy_policy))
                startActivity(intent1)
            }
            R.id.tv_wallet -> {
                var intent = Intent(activity, WalletHistoryActivity::class.java)
                startActivity(intent)
            }
            R.id.tv_rate_us -> {
                showToastSuccess("Coming Soon")

            }
            R.id.tv_share_app -> {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Delicio App")
                    var shareMessage =
                        "\nLet me recommend you this application\n\n"
                    shareMessage = shareMessage + " Google.com\n"
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                    startActivity(Intent.createChooser(shareIntent, "choose one"))
                } catch (e: Exception) {
                    //e.toString();
                }
            }
            R.id.tv_logout -> {
                showLogoutAlert()
            }
        }
    }

    fun showLogoutAlert() {
        dialog = DialogUtil.showDialog(
            requireActivity(),
            this,
            "Logout!",
            getString(R.string.logout_confirmation)
        )
//        val intent1 = Intent(this, LoginUserActivity::class.java)
//        startActivity(intent1)
    }

    override fun onDialogConfirmAction(mView: View?, mKey: String) {
        startProgressDialog()
        when (mKey) {
            "Logout!" -> initLogoutResponseObserver()
        }
    }

    override fun onDialogCancelAction(mView: View?, mKey: String) {
        when (mKey) {
            "Logout!" -> dialog.dismiss()
        }
    }

    fun initLogoutResponseObserver() {
        loginVM.getLogoutData().observe(this, Observer { response ->
            stopProgressDialog()

            if (response != null) {
                val message = response.message!!
                if (response.code == 200) {
                    // Go to login after logout
                    startActivity(Intent(requireActivity(), LoginUserActivity::class.java))



//                    SharedPrefClass().putObject(
//                        MyApplication.instance.applicationContext,
//                        "isLogin",
//                        false
//                    )
//                    SharedPrefClass().putObject(
//                        MyApplication.instance,
//                        ApiKeysConstants.OFFLINE_STATUS,
//                        ""
//                    )
//                    SharedPrefClass().putObject(
//                        MyApplication.instance,
//                        ApiKeysConstants.COMPANY_ID,
//                        ""
//                    )
//                    // User detail
//                    SharedPrefClass().putObject(
//                        MyApplication.instance,
//                        GlobalConstants.USERID,
//                        ""
//                    )

                    val mPreferences = requireActivity().getSharedPreferences(GlobalConstants.SHARED_PREF, Context.MODE_PRIVATE)
                    mPreferences.edit().clear().commit()

                    UtilsFunctions.showToastSuccess(message)
                    requireActivity().finish()

                } else {
                    UtilsFunctions.showToastError(message)
                }
            } else {
                UtilsFunctions.showToastError(MyApplication.instance.getString(R.string.internal_server_error))
            }
        })
    }

}