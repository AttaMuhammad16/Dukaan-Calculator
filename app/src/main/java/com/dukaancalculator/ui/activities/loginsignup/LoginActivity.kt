package com.dukaancalculator.ui.activities.loginsignup

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.atta.dukaancalculator.R
import com.atta.dukaancalculator.databinding.ActivityLoginBinding
import com.dukaancalculator.Utils.MyUtils.showProgressDialog
import com.dukaancalculator.Utils.MyUtils.signInWithGoogle
import com.dukaancalculator.Utils.MyUtils.signInWithGoogleFirebase
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.activities.salemodule.MainActivity
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.lymors.lycommons.utils.MyExtensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var mGoogleSignInClient:GoogleSignInClient
    val GOOGLEREQUESTCODE=2

    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        // google signIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.webclientId)).requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.createAccountTv.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SignUpActivity::class.java))
        }


        binding.signInButton.setOnClickListener {
            signInWithGoogle(this@LoginActivity,GOOGLEREQUESTCODE,mGoogleSignInClient)
        }

        binding.signInBtn.setOnClickListener {
            val email=binding.emailEdt.text.toString().trim()
            val password=binding.passwordEdt.text.toString().trim()

            if (email.isEmpty()){
                showToast("Enter Email")
            }else if (password.isEmpty()){
                showToast("Enter Password")
            }else{

                val dialog= showProgressDialog(this@LoginActivity,"Login...")
                lifecycleScope.launch {
                    val loginResult=mainViewModel.loginUserWithEmailPassword(email, password)

                    loginResult.whenSuccess {
                        dialog.dismiss()
                        startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                        finish()
                    }

                    loginResult.whenError {
                        showToast(it.message.toString())
                        dialog.cancel()
                    }

                }

            }

        }









    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){

            GOOGLEREQUESTCODE->{
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    if (account.idToken!=null){
                        lifecycleScope.launch {
                            val result=signInWithGoogleFirebase(this@LoginActivity,account.idToken!!,auth)

                            result.whenSuccess {
                                showToast(it)
                                val intent=Intent(this@LoginActivity,MoreDetailsActivity::class.java)
                                intent.putExtra("email",account.email)
                                intent.putExtra("fullName",account.displayName)
                                startActivity(intent)
                            }

                            result.whenError {
                                showToast(it.message.toString())
                            }

                        }
                    }
                } catch (e: ApiException) {
                    showToast(e.message.toString())
                }
            }
        }
    }


}