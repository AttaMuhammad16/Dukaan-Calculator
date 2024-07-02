package com.dukaancalculator.ui.activities.loginsignup

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.lifecycleScope
import com.atta.dukaancalculator.databinding.ActivitySignUpBinding
import com.dukaancalculator.Utils.MyUtils.showProgressDialog
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.activities.salemodule.MainActivity
import com.dukaancalculator.ui.models.commonmodels.UserModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.lymors.lycommons.utils.MyExtensions.showToast
import com.lymors.lycommons.utils.Utils.isValidEmail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
//    var selectedRole=""

    @Inject
    lateinit var auth:FirebaseAuth
    @Inject
    lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()
        binding.loginTv.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        }
        val list= listOf("Select Role","SalesMan","Manager")
//        setSpinner(list,binding.spinner)

        binding.signUpBtn.setOnClickListener {

            val userName=binding.userNameEdt.text.toString().trim()
            val email=binding.emailEdt.text.toString().trim()
            val phoneNumber=binding.phoneNumberEdt.text.toString().trim()
            val password=binding.passwordEdt.text.toString().trim()
            val conformPassword=binding.conformEdt.text.toString().trim()

            if (userName.isEmpty()){
                showToast("Enter Name")
            }else if (email.isEmpty()){
                showToast("Enter Email")
            }else if (!email.isValidEmail()){
                showToast("Enter correct Email")
            } else if (phoneNumber.isEmpty()){
                showToast("Enter PhoneNumber")
            }else if (password.isEmpty()){
                showToast("Enter Password")
            }else if (conformPassword.isEmpty()){
                showToast("Enter Conform Password")
            }else if (password.length<6||conformPassword.length<6){
                showToast("Password length must be greater then 6.")
            } else if (password != conformPassword){
                showToast("Passwords doesn't match")
            } else{

                lifecycleScope.launch {

                   val dialog= showProgressDialog(this@SignUpActivity,"SignUp....")
                   val registerResult= mainViewModel.registerUserWithEmailPassword(email, password)

                   registerResult.whenSuccess {

                       val uid=auth.currentUser!!.uid
                       val userModel= UserModel(uid,email, userName, phoneNumber,"",password)

                       lifecycleScope.launch {
                           val uploadResult=mainViewModel.uploadAnyModel("Users/${uid}",userModel)
                           uploadResult.whenSuccess {
                               dialog.dismiss()
                               startActivity(Intent(this@SignUpActivity,MainActivity::class.java))
                               finishAffinity()
                           }
                           uploadResult.whenError {
                               showToast(it.message.toString())
                               dialog.dismiss()
                           }
                       }
                   }

                   registerResult.whenError {
                       showToast(it.message.toString())
                       dialog.dismiss()
                   }
                }
            }

        }


    }

    private fun setSpinner(items: List<String>, spinner: AppCompatSpinner) {
        val adapter = ArrayAdapter(this@SignUpActivity, android.R.layout.simple_spinner_item, items)
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