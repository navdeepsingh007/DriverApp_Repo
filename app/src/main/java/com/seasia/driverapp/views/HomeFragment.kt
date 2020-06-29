package com.seasia.driverapp.views

import com.seasia.driverapp.R
import com.seasia.driverapp.databinding.FragmentHomeBinding
import com.seasia.driverapp.utils.BaseFragment

class HomeFragment : BaseFragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun getLayoutResId(): Int {
        return R.layout.fragment_home
    }

    override fun initView() {
        binding = viewDataBinding as FragmentHomeBinding
    }
}