package com.dukaancalculator.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dukaancalculator.R
import com.dukaancalculator.databinding.FragmentMaalBinding
import com.dukaancalculator.databinding.FragmentMoreBinding
import com.dukaancalculator.ui.activities.kharchamodule.KharchaActivity
import com.dukaancalculator.ui.activities.reports.ReportsActivity
import com.lymors.lycommons.utils.MyExtensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MoreFragment : Fragment() {

    private val binding by viewBinding(FragmentMoreBinding::inflate)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding.kharchaLinear.setOnClickListener {
            startActivity(Intent(requireActivity(),KharchaActivity::class.java))
        }

        binding.reportsLinear.setOnClickListener {
            startActivity(Intent(requireActivity(),ReportsActivity::class.java))
        }

        return binding.root
    }

}