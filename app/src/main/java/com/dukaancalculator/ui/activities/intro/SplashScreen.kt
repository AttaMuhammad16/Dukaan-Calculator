package com.dukaancalculator.ui.activities.intro

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.atta.dukaancalculator.R
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.activities.salemodule.MainActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        statusBarColor()

        Handler().postDelayed({
            if (auth.currentUser!=null){
                Log.i("TAG", "onCreate user uid :${auth.currentUser!!.uid} ")
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@SplashScreen, IntroActivity::class.java))
                finish()
            }
        },3000)


    }
}