package com.dukaancalculator.ui.activities.salemodule

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.atta.dukaancalculator.databinding.ActivityReceiptBinding
import com.atta.dukaancalculator.databinding.SampleProductRowBinding
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils.createPDFReceipt
import com.dukaancalculator.Utils.MyUtils.requestWritePermission
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.shareFileWithOthersViaUri
import com.dukaancalculator.Utils.MyUtils.showProgressDialog
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.dukaancalculator.ui.viewmodel.SaleViewModel
import com.lymors.lycommons.utils.MyExtensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ReceiptActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var saleViewModel: SaleViewModel

    lateinit var binding: ActivityReceiptBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()
        requestWritePermission(this,1)

        val UddharOrDiscount=intent.getStringExtra("UddharOrDiscount")
        val amount=intent.getStringExtra("amount")

        val listOfProductModels=saleViewModel.productModelList
        val subTotal=saleViewModel.subTotalAmount.value
        val grandTotal=saleViewModel.grandTotalAmount.value
        val receiptNumber=intent.getStringExtra("receiptNumber")!!
        val currentDate=intent.getStringExtra("date")

        if (!UddharOrDiscount.isNullOrEmpty()){
            binding.cashDiscountLinear.visibility=View.VISIBLE
            binding.uddharOrDiscountTv.text=UddharOrDiscount
        }

        binding.givenAmountTv.text=amount

        binding.currentDateAndTime.text=currentDate
        binding.receiptNumberTv.text=receiptNumber

        binding.subTotal.text = subTotal
        binding.grandTotal.text = grandTotal

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.support.setOnClickListener {
            sendMessageToWhatsApp(this, MyConstants.attaMuhammadNumber,"")
        }

        lifecycleScope.launch {
            val showProgressDialog= showProgressDialog(this@ReceiptActivity,"Loading...")
            binding.recyclerview.setData(listOfProductModels, SampleProductRowBinding::inflate) { binding, item, position ->
                    binding.name.text = item.productName
                    binding.price.text = item.productPrice
                    binding.qty.text = item.productQuantity
                    binding.total.text = item.total
            }
            showProgressDialog.dismiss()
        }


        binding.shareImg.setOnClickListener {
            lifecycleScope.launch {
                val showDialog= showProgressDialog(this@ReceiptActivity,"Creating....")
                val uri=async { createPDFReceipt(this@ReceiptActivity,"Test","00923034805685",receiptNumber,currentDate!!,UddharOrDiscount!!,amount!!,subTotal,grandTotal,listOfProductModels) }.await()
                showDialog.dismiss()
                if (uri!=null){
                    shareFileWithOthersViaUri(this@ReceiptActivity,uri)
                }else{
                    showToast("File not found.")
                }
            }
        }

        binding.deleteBtn.setOnClickListener{
            finish()
        }

    }

}