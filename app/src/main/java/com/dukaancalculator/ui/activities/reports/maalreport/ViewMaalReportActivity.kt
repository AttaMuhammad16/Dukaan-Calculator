package com.dukaancalculator.ui.activities.reports.maalreport

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityViewMaalReportBinding
import com.dukaancalculator.databinding.SampleProductRowBinding
import com.dukaancalculator.ui.models.maalmodels.MaalReportModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.lymors.lycommons.utils.MyExtensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ViewMaalReportActivity : AppCompatActivity() {
    lateinit var binding:ActivityViewMaalReportBinding
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var mainViewModel: MainViewModel
    lateinit var maalReportModel:MaalReportModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityViewMaalReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        maalReportModel=intent.getParcelableExtra("maalReportModel")!!

        binding.receiptNumberTv.text=maalReportModel.receiptNumber
        binding.currentDateAndTime.text=maalReportModel.date

        for ((i,model)in maalReportModel.listOfProductModel.withIndex()){
            model.total=(model.productPrice.toFloat() * model.productQuantity.toFloat()).toString()
        }

        binding.recyclerview.setData(maalReportModel.listOfProductModel, SampleProductRowBinding::inflate){binding, item, position ->
            binding.name.text = item.productName
            binding.price.text = item.productPrice
            binding.qty.text = item.productQuantity
            binding.total.text = item.total
        }

        if (maalReportModel.uddharOrDiscount.isNotEmpty()&&maalReportModel.uddharOrDiscount!="done"){
            binding.uddharDiscountLinear.visibility= View.VISIBLE
            binding.cashOrUdhharTv.text=maalReportModel.uddharOrDiscount
            binding.givenAmountTv.text=maalReportModel.uddharOrDiscountAmount
        }else{
            binding.uddharDiscountLinear.visibility= View.GONE
        }

        binding.grandTotal.text=maalReportModel.grandTotal
        val infoModel=maalReportModel.maalDeliveryInformationModel

        if (infoModel.supplierNumber.isNotEmpty()){
            binding.supplierLinear.visibility=View.VISIBLE
            binding.distributorOrCompanyNameTv.text=infoModel.distributorCompanyName
            binding.supplierNameTv.text=infoModel.supplierName
            binding.supplierNumberTv.text=infoModel.supplierNumber
        }else{
            binding.supplierLinear.visibility=View.GONE
        }

        binding.shareImg.setOnClickListener {
            lifecycleScope.launch {
                val showDialog= MyUtils.showProgressDialog(this@ViewMaalReportActivity, "Creating....")
                val uri=async {
                    MyUtils.createPDFReceipt(
                        this@ViewMaalReportActivity,
                        "Test",
                        "00923034805685",
                        maalReportModel.receiptNumber,
                        maalReportModel.date,
                        maalReportModel.uddharOrDiscount,
                        maalReportModel.uddharOrDiscountAmount,
                        "",
                        maalReportModel.grandTotal,
                        maalReportModel.listOfProductModel,
                        infoModel.distributorCompanyName,
                        infoModel.supplierName,
                        infoModel.supplierNumber
                    )
                }.await()
                showDialog.dismiss()
                if (uri!=null){
                    MyUtils.shareFileWithOthersViaUri(this@ViewMaalReportActivity, uri)
                }else{
                    showToast("File not found.")
                }
            }
        }


        binding.support.setOnClickListener {
            MyUtils.sendMessageToWhatsApp(this, MyConstants.attaMuhammadNumber, "")
        }


        binding.backImg.setOnClickListener {
            finish()
        }




    }
}