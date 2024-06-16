package com.dukaancalculator.ui.activities.maalmodule

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.R
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityMaalReceiptBinding
import com.dukaancalculator.databinding.ActivityReceiptBinding
import com.dukaancalculator.databinding.SampleProductRowBinding
import com.dukaancalculator.ui.activities.salemodule.MainActivity
import com.dukaancalculator.ui.viewmodel.MaalViewModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.lymors.lycommons.utils.MyExtensions.showToast
import com.lymors.lycommons.utils.MyExtensions.toIntOrDefault
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MaalReceiptActivity : AppCompatActivity() {
    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var maalViewModel: MaalViewModel

    lateinit var binding: ActivityMaalReceiptBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMaalReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()
        MyUtils.requestWritePermission(this, 2)

        val deliveryModel=maalViewModel.deliveryModel.value
        val listOfProductModels=maalViewModel.productModelList
        val grandTotal=maalViewModel.grandTotalAmount.value


        val cashOrUdhhar=intent.getStringExtra("UddharOrDiscount")
        val givenAmount=intent.getStringExtra("amount")
        val date=intent.getStringExtra("date")
        val receiptNumber=intent.getStringExtra("receiptNumber")
        val currentDate="Date: $date"

        binding.cashOrUdhharTv.text=cashOrUdhhar
        binding.givenAmountTv.text=givenAmount

        binding.currentDateAndTime.text=currentDate
        binding.receiptNumberTv.text=receiptNumber

        binding.grandTotal.text = grandTotal


        if (deliveryModel.distributorCompanyName.isNotEmpty()){
            binding.distributorLinear.visibility=View.VISIBLE
            binding.distributorLine.visibility=View.VISIBLE

            binding.supplierNameLinear.visibility=View.VISIBLE
            binding.supplierLine.visibility=View.VISIBLE

            binding.supplierNumberLinear.visibility=View.VISIBLE
            binding.supplierNumberLine.visibility=View.VISIBLE
            binding.receiptImg.visibility=View.VISIBLE
        }

        val distributorName=deliveryModel.distributorCompanyName
        val supplierName=deliveryModel.supplierName
        val supplierNumber=deliveryModel.supplierNumber

        binding.distributorOrCompanyNameTv.text=distributorName
        binding.supplierNameTv.text=supplierName
        binding.supplierNumberTv.text=supplierNumber
        binding.receiptImg.setImageURI(Uri.parse(deliveryModel.imageUrl))

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.support.setOnClickListener {
            MyUtils.sendMessageToWhatsApp(this, MyConstants.adbulRaufPhoneNumber, "")
        }

        lifecycleScope.launch {
            val showProgressDialog= MyUtils.showProgressDialog(this@MaalReceiptActivity, "Loading...")
            for ((i,model)in listOfProductModels.withIndex()){
                model.total=(model.productPrice.toFloat() * model.productQuantity.toFloat()).toString()
            }
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
                val showDialog= MyUtils.showProgressDialog(this@MaalReceiptActivity, "Creating....")
                val uri=async {
                    MyUtils.createPDFReceipt(
                        this@MaalReceiptActivity,
                        "Test",
                        "00923034805685",
                        receiptNumber!!,
                        currentDate,
                        cashOrUdhhar!!,
                        givenAmount!!,
                        "",
                        grandTotal,
                        listOfProductModels,
                        distributorName,
                        supplierName,
                        supplierNumber
                    )
                }.await()
                showDialog.dismiss()
                if (uri!=null){
                    MyUtils.shareFileWithOthersViaUri(this@MaalReceiptActivity, uri)
                }else{
                    showToast("File not found.")
                }
            }
        }

        binding.deleteBtn.setOnClickListener{
            val i = Intent(this, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }

    }

}