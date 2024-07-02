package com.dukaancalculator.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atta.dukaancalculator.databinding.FragmentUddharBinding
import com.dukaancalculator.ui.activities.udhharmodule.UddharForMaalActivity
import com.dukaancalculator.ui.activities.udhharmodule.UddharForSaleActivity
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.lymors.lycommons.utils.MyExtensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UdhharFragment : Fragment() {

    val binding by viewBinding(FragmentUddharBinding::inflate)

    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var auth: FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding.saleUddhar.setOnClickListener {
            startActivity(Intent(requireActivity(),UddharForSaleActivity::class.java))
        }

        binding.maalUddhar.setOnClickListener {
            startActivity(Intent(requireActivity(),UddharForMaalActivity::class.java))
        }

        return binding.root
    }

}