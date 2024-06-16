package com.dukaancalculator.ui.activities.loginsignup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.Utils.MyUtils.showProgressDialog
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityMoreDetailsBinding
import com.dukaancalculator.ui.activities.salemodule.MainActivity
import com.dukaancalculator.ui.models.commonmodels.UserModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.lymors.lycommons.utils.MyExtensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MoreDetailsActivity : AppCompatActivity() {
    lateinit var binding:ActivityMoreDetailsBinding
    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMoreDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()
        val bundleEmail=intent.getStringExtra("email")!!
        val bundleName=intent.getStringExtra("fullName")!!

        binding.emailEdt.setText(bundleEmail)
        binding.nameEdt.setText(bundleName)

        val list= listOf("Select Role","SalesMan","Manager")
//        setSpinner(list,binding.spinner)

        binding.nextBtn.setOnClickListener {

            val email=binding.emailEdt.text.toString().trim()
            val name=binding.nameEdt.text.toString().trim()
            val phoneNumber=binding.phoneNumberEdt.text.toString().trim()

            if (email.isEmpty()){
                showToast("Enter Email")
            }else if (name.isEmpty()){
                showToast("Enter Name")
            }else if (phoneNumber.isEmpty()){
                showToast("Enter PhoneNumber")
            }else{
                val uid=auth.currentUser!!.uid
                val userModel= UserModel(uid,bundleEmail,bundleName,phoneNumber,"")

                lifecycleScope.launch {
                    val dialog= showProgressDialog(this@MoreDetailsActivity,"SignIn....")
                    val uploadResult=mainViewModel.uploadAnyModel("Users/${uid}",userModel)
                    uploadResult.whenSuccess {
                        startActivity(Intent(this@MoreDetailsActivity,MainActivity::class.java))
                        dialog.cancel()
                        finishAffinity()
                    }
                    uploadResult.whenError {
                        showToast(it.message.toString())
                        dialog.cancel()
                    }
                }

            }

        }

    }

    private fun setSpinner(items: List<String>, spinner: AppCompatSpinner) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                var selectedRole = items.get(position)
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
                //                blank
            }

        }
    }
}