package com.dukaancalculator.ui.activities.reports

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityReportsBinding
import com.dukaancalculator.ui.activities.reports.maalreport.SuppliersActivity
import com.dukaancalculator.ui.activities.reports.salereport.CustomersActivity
import com.dukaancalculator.ui.activities.reports.salereport.SaleReportActivity
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ReportsActivity : AppCompatActivity() {

    lateinit var binding:ActivityReportsBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var auth:FirebaseAuth

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()
        val salePath=MyConstants.getNewSalePath(auth)
        val maalPath=MyConstants.getNewMaalPath(auth)

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.saleReportCard.setOnClickListener {
            startNewActivity(CustomersActivity::class.java)
        }

        binding.maalReportCard.setOnClickListener {
            startNewActivity(SuppliersActivity::class.java)
        }

        lifecycleScope.launch {
            val totalSaleUddhar=mainViewModel.getTotalSaleUddhar(salePath)
            binding.totalSaleUddhar.text="Sale: $totalSaleUddhar Rs"
        }

        lifecycleScope.launch {
            val totalMaalUddhar=mainViewModel.getTotalMaalUddhar(maalPath)
            binding.totalMaalUddhar.text="Maal: $totalMaalUddhar Rs"
        }

        binding.udhaarReportCard.setOnClickListener {
            startActivity(Intent(this@ReportsActivity,IsSaleOrMaalUddharActivity::class.java))
        }

    }

    fun startNewActivity(clazz: Class<*>){
        startActivity(Intent(this,clazz))
    }
}