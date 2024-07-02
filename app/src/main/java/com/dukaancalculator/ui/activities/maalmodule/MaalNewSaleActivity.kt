package com.dukaancalculator.ui.activities.maalmodule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.atta.dukaancalculator.R
import com.atta.dukaancalculator.databinding.ActivityMaalNewSaleBinding
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyConstants.getMaalReportPath
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.showSmsDialog
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.models.commonmodels.ProductModel
import com.dukaancalculator.ui.models.maalmodels.DeliveryInformationModel
import com.dukaancalculator.ui.models.maalmodels.MaalReportModel
import com.dukaancalculator.ui.models.maalmodels.NewMaalModel
import com.dukaancalculator.ui.viewmodel.MaalViewModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lymors.lycommons.utils.MyExtensions.onTextChange
import com.lymors.lycommons.utils.MyExtensions.showToast
import com.lymors.lycommons.utils.MyExtensions.toIntOrDefault
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MaalNewSaleActivity : AppCompatActivity() {
    lateinit var binding: ActivityMaalNewSaleBinding
    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var maalViewModel: MaalViewModel

    var givenAmount=0

    @Inject
    lateinit var databaseReference: DatabaseReference

    @Inject
    lateinit var auth: FirebaseAuth

    var resultAmount=0
    var UddharOrDiscount="Uddhar"

    var supplierNames = listOf<String>()
    var currentDate= ""
    var receiptNumber=""
    var listOfProductModels = ArrayList<ProductModel>()

    lateinit var deliveryInfoModel:DeliveryInformationModel
    var grandTotal=""
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMaalNewSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        deliveryInfoModel=maalViewModel.deliveryModel.value
        receiptNumber= MyUtils.generateReceiptNumber()
        listOfProductModels=maalViewModel.productModelList
        grandTotal=maalViewModel.grandTotalAmount.value


        val totalAmount = maalViewModel.grandTotalAmount.value.toFloatOrNull()?.toInt() ?: 0
        resultAmount=totalAmount


        lifecycleScope.launch {
            mainViewModel.getCustomerNames(MyConstants.getNewMaalPath(auth)).collect{
                supplierNames=it
            }
        }



        lifecycleScope.launch {
            maalViewModel.grandTotalAmount.collect { total ->
                binding.totalAmountTv.text = "Rs $total"
            }
        }

        binding.support.setOnClickListener {
            MyUtils.sendMessageToWhatsApp(this, MyConstants.attaMuhammadNumber, "")
        }

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.amountCollectEdt.onTextChange { text ->
            val enteredAmount= text.toIntOrDefault(0)
            if (enteredAmount>=totalAmount){
                resultAmount = enteredAmount - totalAmount
                binding.amountToReturnAndGetTv.text = "Amount to Return: Rs $resultAmount"
                binding.amountToReturnAndGetTv.setTextColor(ContextCompat.getColor(this@MaalNewSaleActivity, R.color.white))
            }else{
                resultAmount = totalAmount - enteredAmount
                binding.amountToReturnAndGetTv.text = "Amount to Give: Rs $resultAmount"
                binding.amountToReturnAndGetTv.setTextColor(ContextCompat.getColor(this@MaalNewSaleActivity, com.lymors.lycommons.R.color.red))
            }
            givenAmount=enteredAmount
        }

//        binding.newInvoiceBtn.setOnClickListener {
//            if (givenAmount<totalAmount){
//                if (UddharOrDiscount.isEmpty()){
//                    showToast("Select Uddhar or Discount")
//                }else{
//                    showCustomerDialog(supplierNames)
//                }
//            }
//        }

        binding.whatsAppImg.setOnClickListener {
            MyUtils.sendMessageToWhatsApp(this@MaalNewSaleActivity, MyConstants.attaMuhammadNumber, "")
        }

        binding.smsImg.setOnClickListener {
            showSmsDialog(MyConstants.attaMuhammadNumber)
        }


        binding.saveImg.setOnClickListener {
            if(givenAmount>=totalAmount){
                UddharOrDiscount="done"
                showCustomerDialog(supplierNames)
            }else{
                if (UddharOrDiscount.isEmpty()){
                    showToast("Enter Uddhar or Discount")
                }else{
                    showCustomerDialog(supplierNames)
                }
            }
        }


        binding.viewAndPrint.setOnClickListener {
            startReceiptActivity()
        }


        binding.doneBtn.setOnClickListener {

            if(givenAmount>=totalAmount){

//                val intent = Intent(this, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)

            }else{

                val alert=androidx.appcompat.app.AlertDialog.Builder(this@MaalNewSaleActivity).setView(
                    R.layout.ask_uddhar_or_discount_dialog).show()
                val uddharTv=alert.findViewById<TextView>(R.id.uddharTv)
                val discountTv=alert.findViewById<TextView>(R.id.discountTv)

                uddharTv?.setOnClickListener {
                    UddharOrDiscount="Uddhar"
                    showToast("Uddhar")
                    alert.dismiss()
                }

                discountTv?.setOnClickListener {
                    UddharOrDiscount="Discount"
                    showToast("Discount")
                    alert.dismiss()
                }

                alert.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }

    }

    fun startReceiptActivity(){
        val intent=Intent(this, MaalReceiptActivity::class.java)
        currentDate=MyUtils.getCurrentDateTime()
        if (UddharOrDiscount=="Uddhar"){
            if (resultAmount!=0){
                intent.putExtra("UddharOrDiscount",UddharOrDiscount)
                intent.putExtra("amount",resultAmount.toString())
                intent.putExtra("date",currentDate)
                intent.putExtra("receiptNumber",receiptNumber)
            }else{
                showToast("Enter collected amount.")
            }
        }else if (UddharOrDiscount=="Discount"){
            intent.putExtra("UddharOrDiscount",UddharOrDiscount)
            intent.putExtra("amount",resultAmount.toString())
            intent.putExtra("date",currentDate)
            intent.putExtra("receiptNumber",receiptNumber)
        }
        startActivity(intent)
    }


    fun showCustomerDialog(supplierNames: List<String>){
        currentDate=MyUtils.getCurrentDateTime()

        val dialog= AlertDialog.Builder(this).setView(R.layout.add_customer_detail_dialog).show()

        val customerNameEdt: AutoCompleteTextView =dialog.findViewById(R.id.customerNameEdt)
        val phoneNumberEdt: EditText =dialog.findViewById(R.id.phoneNumberEdt)

        val saveBtn: Button =dialog.findViewById(R.id.saveBtn)
        val cancelBtn: Button =dialog.findViewById(R.id.cancelBtn)

        customerNameEdt.threshold=1
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, supplierNames)
        customerNameEdt.setAdapter(adapter)


        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {

            val name=customerNameEdt.text.toString().trim()
            val phoneNumber=phoneNumberEdt.text.toString().trim()
            if (name.isEmpty()){
                showToast("Enter Customer Name")
            }else if (phoneNumber.isEmpty()){
                showToast("Enter Customer PhoneNumber")
            }else{

                val progressDialog= MyUtils.showProgressDialog(this@MaalNewSaleActivity, "Saving...")
                val key=databaseReference.push().key.toString()
                val newMaalModel= NewMaalModel(key,name,phoneNumber,resultAmount.toString(),UddharOrDiscount,System.currentTimeMillis())

                lifecycleScope.launch {
                    val result=mainViewModel.uploadAnyModel("${MyConstants.getNewMaalPath(auth)}$name/$key",newMaalModel)

                    result.whenSuccess {

                        if (deliveryInfoModel.supplierName.isNotEmpty()&&deliveryInfoModel.supplierNumber.isNotEmpty()){
                            lifecycleScope.launch {

                                if (deliveryInfoModel.imageUrl.isNotEmpty()){
                                    val deliveryImgUploadResult=mainViewModel.uploadImageToFirebaseStorageWithUri(Uri.parse(deliveryInfoModel.imageUrl),"")
                                    deliveryImgUploadResult.whenSuccess {imgUrl->
                                        lifecycleScope.launch {

                                            deliveryInfoModel.imageUrl=imgUrl
                                            deliveryInfoModel.key=key

                                            val maalReportModel = MaalReportModel(key,receiptNumber,currentDate,deliveryInfoModel,listOfProductModels,UddharOrDiscount,resultAmount.toString(),grandTotal)

                                            val maalReportModelResult=mainViewModel.uploadAnyModel(getMaalReportPath(auth)+name+"/"+MyUtils.getCurrentDate("yyyy/MM/dd")+"/"+key,maalReportModel)

                                            maalReportModelResult.whenSuccess {
                                                showToast("Saved")
                                                progressDialog.dismiss()
                                                dialog.dismiss()
                                            }
                                            maalReportModelResult.whenError {
                                                showToast(it.message.toString())
                                                progressDialog.dismiss()
                                            }
                                        }
                                    }
                                }else{

                                    lifecycleScope.launch {

                                        deliveryInfoModel.key=key
                                        val maalReportModel = MaalReportModel(key,receiptNumber,currentDate,deliveryInfoModel,listOfProductModels,UddharOrDiscount,resultAmount.toString(),grandTotal)

                                        val maalReportModelResult=mainViewModel.uploadAnyModel(getMaalReportPath(auth)+name+"/"+MyUtils.getCurrentDate("yyyy/MM/dd")+"/"+key,maalReportModel)

                                        maalReportModelResult.whenSuccess {
                                            showToast("Saved")
                                            progressDialog.dismiss()
                                            dialog.dismiss()
                                        }

                                        maalReportModelResult.whenError {
                                            showToast(it.message.toString())
                                            progressDialog.dismiss()
                                        }

                                    }
                                }
                            }
                        }else{

                            lifecycleScope.launch {

                                val maalReportModel = MaalReportModel(key,receiptNumber,currentDate,deliveryInfoModel,listOfProductModels,UddharOrDiscount,resultAmount.toString(),grandTotal)

                                val maalReportModelResult=mainViewModel.uploadAnyModel(getMaalReportPath(auth)+name+"/"+MyUtils.getCurrentDate("yyyy/MM/dd")+"/"+key,maalReportModel)

                                maalReportModelResult.whenSuccess {
                                    showToast("Saved")
                                    progressDialog.dismiss()
                                    dialog.dismiss()
                                }

                                maalReportModelResult.whenError {
                                    showToast(it.message.toString())
                                    progressDialog.dismiss()
                                }
                            }

                        }
                    }

                    result.whenError {
                        progressDialog.dismiss()
                        showToast(it.message.toString())
                    }

                }

            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}