package com.seasia.driverapp.views.driverjobs

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.google.gson.GsonBuilder
import com.seasia.driverapp.R
import com.seasia.driverapp.constants.GlobalConstants
import com.seasia.driverapp.databinding.FragmentHome2Binding
import com.seasia.driverapp.utils.BaseFragment


class HomeFragment : BaseFragment() {
    private var fragment: Fragment? = null
    var binding:FragmentHome2Binding?=null
    private val gson = GsonBuilder().serializeNulls().create()

    override fun getLayoutResId(): Int {
      return R.layout.fragment_home2
    }


    override fun initView() {
        binding = viewDataBinding as FragmentHome2Binding
        initContentMain()
    }

    private fun initContentMain() {
        fragment = AssignedJobsFragment()
        callFragments(fragment, childFragmentManager, false, "send_data", "")

        binding!!.tablayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var fragment: Fragment? = null

                when (tab!!.position) {
                    0 -> fragment = AssignedJobsFragment()
                    1 -> fragment = ActiveJobsFragment()
                    2 -> fragment = CompletedJobsFragment()
                }
                callFragments(fragment, childFragmentManager, false, "send_data", "")
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //var fragment : Fragment? = null
                //Not In use
            }
        })
    }

    fun callFragments(
        fragment: androidx.fragment.app.Fragment?,
        mFragmentManager: androidx.fragment.app.FragmentManager,
        mSendDataCheck: Boolean,
        key: String?,
        mObject: Any
    ) {
        val mFragmentTransaction = mFragmentManager.beginTransaction()
        if (fragment != null) {
            //mFragmentTransaction.addToBackStack(null)
            mFragmentTransaction.replace(R.id.frame_layout12, fragment)
            // mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            mFragmentTransaction.commit()
        }
    }
}