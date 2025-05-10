package com.dukaancalculator.ui.activities.intro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.atta.dukaancalculator.databinding.ActivityIntroBinding
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.activities.loginsignup.LoginActivity
import com.dukaancalculator.ui.adapters.ViewAdapter

class IntroActivity : AppCompatActivity() {
    lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        val viewAdapter=ViewAdapter(this@IntroActivity)
        binding.viewPager.adapter=viewAdapter
        binding.dots.setViewPager(binding.viewPager)
        binding.loginSignUpBtn.setOnClickListener{
            startActivity(Intent(this@IntroActivity,LoginActivity::class.java))
        }

    }
}
