package com.dukaancalculator.ui.activities.reports.salereport

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityViewSaleReportBinding
import com.dukaancalculator.databinding.SampleProductRowBinding
import com.dukaancalculator.ui.models.salemodels.SaleReportModel
import com.lymors.lycommons.utils.MyExtensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ViewSaleReportActivity : AppCompatActivity() {

    lateinit var binding:ActivityViewSaleReportBinding
    var bundleSaleReportModel:SaleReportModel?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityViewSaleReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()
        bundleSaleReportModel = intent.getParcelableExtra("saleReportModel")

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.support.setOnClickListener {
            MyUtils.sendMessageToWhatsApp(this, MyConstants.attaMuhammadNumber, "")
        }


        bundleSaleReportModel?.apply {

            binding.receiptNumberTv.text=receiptNumber

            binding.currentDateAndTime.text=date

            binding.recyclerview.setData(productModelList, SampleProductRowBinding::inflate){binding, item, position ->
                binding.name.text = item.productName
                binding.price.text = item.productPrice
                binding.qty.text = item.productQuantity
                binding.total.text = item.total
            }

            binding.subTotal.text = subtotal
            binding.grandTotal.text = grandTotal.toString()

            if (uddharOrDiscount.isNotEmpty()&&uddharOrDiscount!="done"){
                binding.cashDiscountLinear.visibility= View.VISIBLE
                binding.uddharOrDiscountTv.text=uddharOrDiscount
                binding.uddharOrDiscountAmount.text=uddharOrDiscountAmount
            }
        }



        binding.shareImg.setOnClickListener {
            lifecycleScope.launch {
                val showDialog= MyUtils.showProgressDialog(this@ViewSaleReportActivity, "Creating....")
                val uri=async {
                    MyUtils.createPDFReceipt(
                        this@ViewSaleReportActivity,
                        "Test",
                        "00923034805685",
                        bundleSaleReportModel!!.receiptNumber,
                        bundleSaleReportModel!!.date,
                        bundleSaleReportModel!!.uddharOrDiscount,
                        bundleSaleReportModel!!.uddharOrDiscountAmount,
                        bundleSaleReportModel!!.subtotal,
                        bundleSaleReportModel!!.grandTotal.toString(),
                        bundleSaleReportModel!!.productModelList
                    )
                }.await()
                showDialog.dismiss()
                if (uri!=null){
                    MyUtils.shareFileWithOthersViaUri(this@ViewSaleReportActivity, uri)
                }else{
                    showToast("File not found.")
                }
            }
        }



    }
}