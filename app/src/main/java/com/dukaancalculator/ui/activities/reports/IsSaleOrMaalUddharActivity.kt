package com.dukaancalculator.ui.activities.reports

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dukaancalculator.R
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityIsSaleOrMaalUddharBinding
import com.dukaancalculator.ui.activities.udhharmodule.UddharForMaalActivity
import com.dukaancalculator.ui.activities.udhharmodule.UddharForSaleActivity
import com.dukaancalculator.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class IsSaleOrMaalUddharActivity : AppCompatActivity() {
    @Inject
    lateinit var mainViewModel: MainViewModel
    lateinit var binding:ActivityIsSaleOrMaalUddharBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityIsSaleOrMaalUddharBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        binding.saleUddhar.setOnClickListener {
            startActivity(Intent(this@IsSaleOrMaalUddharActivity, UddharForSaleActivity::class.java))
        }

        binding.maalUddhar.setOnClickListener {
            startActivity(Intent(this, UddharForMaalActivity::class.java))
        }

    }
}