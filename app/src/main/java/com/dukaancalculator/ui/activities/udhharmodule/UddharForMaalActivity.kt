package com.dukaancalculator.ui.activities.udhharmodule

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.getFormattedDateAndTime
import com.dukaancalculator.Utils.MyUtils.makeACall
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.showProgressDialog
import com.dukaancalculator.Utils.MyUtils.showSmsDialog
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityUddharForMaalBinding
import com.dukaancalculator.databinding.MaalRemainingPaymentSampleRowBinding
import com.dukaancalculator.ui.models.maalmodels.NewMaalModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UddharForMaalActivity : AppCompatActivity() {

    @Inject
    lateinit var auth:FirebaseAuth
    @Inject
    lateinit var mainViewModel: MainViewModel

    lateinit var binding:ActivityUddharForMaalBinding

    var path=""


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUddharForMaalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()
        path=MyConstants.getNewMaalPath(auth)

        binding.support.setOnClickListener {
            MyUtils.sendMessageToWhatsApp(this, MyConstants.adbulRaufPhoneNumber, "")
        }

        binding.backImg.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            val progress= showProgressDialog(this@UddharForMaalActivity,"Loading...")
            val list=mainViewModel.getMaalUddhar(path)
            progress.dismiss()
            val filteredList = list.filter { it.uddharOrDiscount == "Uddhar" }
            val againFiltered = filteredList.filter { it.uddharOrDiscount != "done" }

            binding.recyclerView.setData(againFiltered,MaalRemainingPaymentSampleRowBinding::inflate){binding, item, position ->

                binding.supplierName.text="Supplier Name: ${item.supplierName}"
                binding.supplierPhoneNumber.text="Supplier PhoneNumber: ${item.supplierPhoneNumber}"
                binding.amountToGive.text="Amount: ${item.uddharOrDiscountAmount} Rs"
                binding.time.text="Date: "+getFormattedDateAndTime(item.timeStamp,"dd/MM/yyyy hh:mm a")

                binding.whatsApp.setOnClickListener {
                    MyUtils.sendMessageToWhatsApp(this@UddharForMaalActivity, item.supplierPhoneNumber, "")
                }
                binding.sms.setOnClickListener {
                    showSmsDialog(item.supplierPhoneNumber)
                }
                binding.call.setOnClickListener {
                    makeACall(item.supplierName)
                }

            }

        }

    }
}