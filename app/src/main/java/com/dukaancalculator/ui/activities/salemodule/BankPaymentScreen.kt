package com.dukaancalculator.ui.activities.salemodule

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.R
import com.dukaancalculator.Utils.MyConstants.getSaleBankDetailsPath
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.sendSMS
import com.dukaancalculator.Utils.MyUtils.showProgressDialog
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityBankPaymentScreenBinding
import com.dukaancalculator.ui.models.salemodels.BankDetailsModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lymors.lycommons.utils.MyExtensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BankPaymentScreen : AppCompatActivity() {
    @Inject
    lateinit var mainViewModel: MainViewModel

    lateinit var binding:ActivityBankPaymentScreenBinding

    @Inject
    lateinit var auth:FirebaseAuth

    @Inject
    lateinit var databaseReference: DatabaseReference
    var listSize=0
    var path=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBankPaymentScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()
        path=getSaleBankDetailsPath(auth)

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.floatingActionButton.setOnClickListener {
            if (listSize==2){
                showToast("You can add only 2 bank details")
            }else{
                showDialogToAddBankDetail()
            }
        }

        lifecycleScope.launch {
            mainViewModel.collectAnyModel(path, BankDetailsModel::class.java)
        }

        lifecycleScope.launch {
            mainViewModel.bankDetailsFlow.collect{
                listSize=it.size
                binding.linearLayout12.removeAllViews()
                for (i in it){
                    addViewsInLinearLayout(i)
                }
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 101)
        }


    }

    @SuppressLint("SetTextI18n")
    private fun addViewsInLinearLayout(data: BankDetailsModel){
        val view = layoutInflater.inflate(R.layout.bank_detail_sample_row, binding.linearLayout12, false)
        val accountTitleTv=view.findViewById<TextView>(R.id.accountTitleTv)
        val ibanTv=view.findViewById<TextView>(R.id.ibanTv)
        val accountNumberTv=view.findViewById<TextView>(R.id.accountNumberTv)
        val editImg=view.findViewById<ImageView>(R.id.editImg)
        val whatsAppImg=view.findViewById<ImageView>(R.id.whatsAppImg)
        val smsImg=view.findViewById<ImageView>(R.id.smsImg)

        accountTitleTv.text="Account Title: ${data.accountTitle}"
        ibanTv.text="IBAN: ${data.ibanNumber}"
        accountNumberTv.text="Account#: ${data.accountNumber}"

        editImg.setOnClickListener {
            showEditDetailsDialog(data)
        }

        whatsAppImg.setOnClickListener {
            showPhoneNumberDialog("whatsapp",data)
        }
        smsImg.setOnClickListener {
            showPhoneNumberDialog("sms",data)
        }

        binding.linearLayout12.addView(view)

    }



    private fun showPhoneNumberDialog(from:String,data: BankDetailsModel){

        val dialog=AlertDialog.Builder(this@BankPaymentScreen).setView(R.layout.get_phone_number_dialog).show()

        val phoneNumberEdt=dialog.findViewById<EditText>(R.id.phoneNumberEdt)
        val cancelBtn=dialog.findViewById<Button>(R.id.cancelBtn)
        val send=dialog.findViewById<Button>(R.id.sendBtn)

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        send.setOnClickListener {
            val number=phoneNumberEdt.text.toString().trim()
            if (number.isEmpty()){
                showToast("Enter PhoneNumber")
            }else{
                val message="Account title: ${data.accountTitle}\nIBAN: ${data.ibanNumber}\nAccount Number: ${data.accountNumber}"
                if (from=="whatsapp"){
                    sendMessageToWhatsApp(this@BankPaymentScreen,number,message)
                    dialog.dismiss()
                }else{
                    sendSMS(number,message)
                    dialog.dismiss()
                }
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    private fun showEditDetailsDialog(data: BankDetailsModel){
        val dialog=AlertDialog.Builder(this@BankPaymentScreen).setView(R.layout.bank_detail_dialog).show()

        val accountTitleEdt=dialog.findViewById<EditText>(R.id.accountTitleEdt)
        val ibanEdt=dialog.findViewById<EditText>(R.id.ibanEdt)
        val accountNumberEdt=dialog.findViewById<EditText>(R.id.accountNumberEdt)

        val cancelBtn=dialog.findViewById<Button>(R.id.cancelBtn)
        val saveBtn=dialog.findViewById<Button>(R.id.saveBtn)

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        accountTitleEdt.setText(data.accountTitle)
        ibanEdt.setText(data.ibanNumber)
        accountNumberEdt.setText(data.accountNumber)

        saveBtn.setOnClickListener {

            val accountTitle=accountTitleEdt.text.toString().trim()
            val iban=ibanEdt.text.toString().trim()
            val accountNumber=accountNumberEdt.text.toString().trim()

            if (auth.currentUser!=null){

                val bankDetailsPath=path+data.key


                if (accountTitle.isEmpty()){
                    showToast("Enter Account Title")
                }else if (iban.isEmpty()){
                    showToast("Enter IBAN")
                }else if (accountNumber.isEmpty()){
                    showToast("Enter Account Number")
                }else{
                    val bankDetailModel= BankDetailsModel(data.key,accountTitle,iban,accountNumber)
                    val progressDialog=showProgressDialog(this@BankPaymentScreen,"Saving...")
                    lifecycleScope.launch {
                        val result=mainViewModel.uploadAnyModel(bankDetailsPath,bankDetailModel)
                        result.whenSuccess {
                            showToast("Saved")
                            progressDialog.dismiss()
                            dialog.dismiss()
                        }
                        result.whenError {
                            showToast(it.message.toString())
                            progressDialog.dismiss()
                        }
                    }
                }
            }else{
                showToast("Please Sign In")
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }



    private fun showDialogToAddBankDetail() {

        val dialog=AlertDialog.Builder(this@BankPaymentScreen).setView(R.layout.bank_detail_dialog).show()
        val accountTitleEdt=dialog.findViewById<EditText>(R.id.accountTitleEdt)
        val ibanEdt=dialog.findViewById<EditText>(R.id.ibanEdt)
        val accountNumberEdt=dialog.findViewById<EditText>(R.id.accountNumberEdt)
        val cancelBtn=dialog.findViewById<Button>(R.id.cancelBtn)
        val saveBtn=dialog.findViewById<Button>(R.id.saveBtn)

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {
            val accountTitle=accountTitleEdt.text.toString().trim()
            val iban=ibanEdt.text.toString().trim()
            val accountNumber=accountNumberEdt.text.toString().trim()

            if (auth.currentUser!=null){
                val key=databaseReference.push().key.toString()
                val bankDetailsPath=path+key
                if (accountTitle.isEmpty()){
                    showToast("Enter Account Title")
                }else if (iban.isEmpty()){
                    showToast("Enter IBAN")
                }else if (accountNumber.isEmpty()){
                    showToast("Enter Account Number")
                }else{
                    val bankDetailModel= BankDetailsModel(key,accountTitle,iban,accountNumber)
                    val progressDialog=showProgressDialog(this@BankPaymentScreen,"Saving...")
                    lifecycleScope.launch {
                        val result=mainViewModel.uploadAnyModel(bankDetailsPath,bankDetailModel)
                        result.whenSuccess {
                            showToast("Saved")
                            progressDialog.dismiss()
                            dialog.dismiss()
                        }
                        result.whenError {
                            showToast(it.message.toString())
                            progressDialog.dismiss()
                        }
                    }
                }
            }else{
                showToast("Please Sign In")
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }




}