package com.dukaancalculator.ui.activities.salemodule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.atta.dukaancalculator.R
import com.atta.dukaancalculator.databinding.ActivityMainBinding
import com.dukaancalculator.Utils.MyConstants.attaMuhammadNumber
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ViewPagerAdapter
import com.dukaancalculator.ui.fragments.MaalFragment
import com.dukaancalculator.ui.fragments.MoreFragment
import com.dukaancalculator.ui.fragments.UdhharFragment
import com.dukaancalculator.ui.fragments.SalesFragment
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.lymors.lycommons.utils.Utils.shareText
import com.lymors.lycommons.utils.Utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModel: MainViewModel
    val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statusBarColor()
        val viewPager=binding.viewPager
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomBar.menu.getItem(position).isChecked = true
            }
        })

        binding.bottomBar.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.sales -> {
                    viewPager.currentItem = 0
                    true
                }
                R.id.maal -> {
                    viewPager.currentItem = 1
                    true
                }
                R.id.uddhar -> {
                    viewPager.currentItem = 2
                    true
                }
                R.id.more -> {
                    viewPager.currentItem = 3
                    true
                }
                else -> false
            }
        }

        binding.support.setOnClickListener {
            sendMessageToWhatsApp(this@MainActivity,attaMuhammadNumber,"")
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


//      drawer code
        binding.menuImg.setOnClickListener {
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            } else {
                binding.drawer.openDrawer(GravityCompat.START)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.navDrwer.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.support -> {
                    sendMessageToWhatsApp(this@MainActivity, attaMuhammadNumber,"")
                }
                R.id.help ->{
                    sendMessageToWhatsApp(this@MainActivity, attaMuhammadNumber,"")
                }
            }
            true
        }
    }


}

