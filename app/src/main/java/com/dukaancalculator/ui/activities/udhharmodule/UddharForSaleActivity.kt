package com.dukaancalculator.ui.activities.udhharmodule

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.getFormattedDateAndTime
import com.dukaancalculator.Utils.MyUtils.makeACall
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.showSmsDialog
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityUddharForSaleBinding
import com.dukaancalculator.databinding.SaleRemainingPaymentSampleRowBinding
import com.dukaancalculator.ui.models.salemodels.NewSaleModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class UddharForSaleActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var databaseReference: DatabaseReference

    lateinit var binding: ActivityUddharForSaleBinding

    @Inject
    lateinit var mainViewModel: MainViewModel

    var path=""

    private val SMS_PERMISSION_REQUEST_CODE = 101
    private val CALL_PERMISSION_REQUEST_CODE = 102


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUddharForSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        path=MyConstants.getNewSalePath(auth)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_REQUEST_CODE)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE), CALL_PERMISSION_REQUEST_CODE)
        }

        binding.support.setOnClickListener {
            sendMessageToWhatsApp(this@UddharForSaleActivity,MyConstants.adbulRaufPhoneNumber,"")
        }

        binding.backImg.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            val progress= MyUtils.showProgressDialog(this@UddharForSaleActivity, "Loading...")
            val list=mainViewModel.getSaleUddhar(path)
            progress.dismiss()
            val filteredList = list.filter { it.uddharOrDiscount == "Uddhar" }
            val againFiltered = filteredList.filter { it.uddharOrDiscount != "done" }
            binding.recyclerView.setData(againFiltered,SaleRemainingPaymentSampleRowBinding::inflate){binding, item, position ->

                binding.customerName.text="Customer Name: ${item.customerName}"
                binding.amountToTake.text="Amount: ${item.uddharOrDiscountAmount} Rs"
                binding.customerPhoneNumber.text="Customer PhoneNumber: ${item.customerPhoneNumber}"
                binding.time.text="Date: "+getFormattedDateAndTime(item.timeStamp,"dd/MM/yyyy hh:mm a")

                binding.whatsApp.setOnClickListener {
                    sendMessageToWhatsApp(this@UddharForSaleActivity,item.customerPhoneNumber,"")
                }

                binding.sms.setOnClickListener {
                    showSmsDialog(item.customerPhoneNumber)
                }

                binding.call.setOnClickListener {
                    makeACall(item.customerPhoneNumber)
                }

            }
        }

    }
}