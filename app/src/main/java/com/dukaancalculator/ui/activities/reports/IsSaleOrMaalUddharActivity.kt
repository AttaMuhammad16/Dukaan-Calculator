package com.dukaancalculator.ui.activities.reports

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atta.dukaancalculator.databinding.ActivityIsSaleOrMaalUddharBinding
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.activities.udhharmodule.UddharForMaalActivity
import com.dukaancalculator.ui.activities.udhharmodule.UddharForSaleActivity
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.lymors.lycommons.utils.Utils.shareText
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class IsSaleOrMaalUddharActivity : AppCompatActivity() {
    @Inject
    lateinit var mainViewModel: MainViewModel
    lateinit var binding: ActivityIsSaleOrMaalUddharBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityIsSaleOrMaalUddharBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        binding.saleUddhar.setOnClickListener {
            startActivity(Intent(this@IsSaleOrMaalUddharActivity, UddharForSaleActivity::class.java))
        }

        binding.maalUddhar.setOnClickListener {
            startActivity(Intent(this, UddharForMaalActivity::class.java))
        }

        binding.support.setOnClickListener {
            sendMessageToWhatsApp(this, MyConstants.attaMuhammadNumber,"")
        }

        binding.shareImg.setOnClickListener {
            shareText("\uD83D\uDED2 Welcome to Dukaan Calculator! \uD83D\uDCF1\uD83D\uDCBC\n" +
                    "\n" +
                    "Discover the ultimate solution for managing your store efficiently! \uD83D\uDE80\n" +
                    "\n" +
                    "\uD83D\uDCCA Track sales, manage inventory, and create beautiful receipts effortlessly with Dukaan Calculator. \uD83D\uDCDD\uD83D\uDCBC\n" +
                    "\n" +
                    "\uD83D\uDD0D Never miss a sale again with our powerful inventory management features! \uD83D\uDCA1\n" +
                    "\n" +
                    "\uD83D\uDCA1 Download Dukaan Calculator now for a hassle-free store management experience! \uD83D\uDCF2\n" +
                    "\n" +
                    "https://play.google.com/store/apps/details?id=com.dukaancalculator\n\n" +
                    "Join thousands of satisfied users who trust Dukaan Calculator for their store management needs! \uD83D\uDCBC✨","Dukaan Calculator")
        }




    }
}