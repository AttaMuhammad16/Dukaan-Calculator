package com.dukaancalculator.ui.activities.salemodule


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.R
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyConstants.getNewSalePath
import com.dukaancalculator.Utils.MyConstants.getSaleReportPath
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.getCurrentDate
import com.dukaancalculator.Utils.MyUtils.logT
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.showProgressDialog
import com.dukaancalculator.Utils.MyUtils.showSmsDialog
import com.dukaancalculator.Utils.MyUtils.smsPermission
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityNewSaleBinding
import com.dukaancalculator.ui.models.salemodels.NewSaleModel
import com.dukaancalculator.ui.models.salemodels.SaleReportModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.dukaancalculator.ui.viewmodel.SaleViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lymors.lycommons.utils.MyExtensions.onTextChange
import com.lymors.lycommons.utils.MyExtensions.showToast
import com.lymors.lycommons.utils.MyExtensions.toIntOrDefault
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewSaleActivity : AppCompatActivity() {
    lateinit var binding:ActivityNewSaleBinding
    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var saleViewModel: SaleViewModel

    var givenAmount=0
    @Inject
    lateinit var databaseReference: DatabaseReference
    @Inject
    lateinit var auth:FirebaseAuth
    var resultAmount=0
    var UddharOrDiscount=""
    var receiptNumber=""

    var customerNames = listOf<String>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNewSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()
        receiptNumber= MyUtils.generateReceiptNumber()
        val listOfProduct=saleViewModel.productModelList

        val totalAmount = saleViewModel.grandTotalAmount.value.toFloatOrNull()?.toInt() ?: 0
        resultAmount=totalAmount
        val subTotal=saleViewModel.subTotalAmount.value

        lifecycleScope.launch {
            mainViewModel.getCustomerNames(getNewSalePath(auth)).collect{
                customerNames=it
            }
        }

        lifecycleScope.launch {
            saleViewModel.grandTotalAmount.collect { total ->
                binding.totalAmountTv.text = "Rs $total"
            }
        }

        binding.support.setOnClickListener {
            sendMessageToWhatsApp(this, MyConstants.adbulRaufPhoneNumber,"")
        }

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.amountCollectEdt.onTextChange { text ->
            val enteredAmount= text.toIntOrDefault(0)
            if (enteredAmount>=totalAmount){
                resultAmount = enteredAmount - totalAmount
                binding.amountToReturnAndGetTv.text = "Amount to Return: Rs $resultAmount"
                binding.amountToReturnAndGetTv.setTextColor(ContextCompat.getColor(this@NewSaleActivity, R.color.white))
            }else{
                resultAmount = totalAmount - enteredAmount
                binding.amountToReturnAndGetTv.text = "Amount to Take: Rs $resultAmount"
                binding.amountToReturnAndGetTv.setTextColor(ContextCompat.getColor(this@NewSaleActivity, com.lymors.lycommons.R.color.red))
            }
            givenAmount=enteredAmount
        }

        binding.newSaleBtn.setOnClickListener {
            if (givenAmount<totalAmount){
                if (UddharOrDiscount.isEmpty()){
                    showToast("Enter Uddhar or Discount")
                }else{
                    showCustomerDialog(customerNames)
                }
            }
        }

        binding.saveImg.setOnClickListener {
            if(givenAmount>=totalAmount){
                UddharOrDiscount="done"
                showCustomerDialog(customerNames)
            }else{
                if (UddharOrDiscount.isEmpty()){
                    showToast("Enter Uddhar or Discount")
                }else{
                    showCustomerDialog(customerNames)
                }
            }
        }





        binding.whatsAppImg.setOnClickListener {
            sendMessageToWhatsApp(this@NewSaleActivity,MyConstants.adbulRaufPhoneNumber,"")
        }

        smsPermission(this@NewSaleActivity,12)

        binding.sms.setOnClickListener {
            val productsString = listOfProduct.joinToString(separator = "\n") {
""" 
    
item: ${it.productName}

price: ${it.productPrice} | quantity: ${it.productQuantity} | total: ${it.total}
""".trimIndent()
 }

val message = """   
Hi Customer

Total Bill Amount is Rs $totalAmount

Receipt: $receiptNumber

Date: ${getCurrentDate()}

------------------------

$productsString

------------------------
Subtotal     $subTotal
-------------------------
TOTAL        $totalAmount
-------------------------
Payment Type:   $UddharOrDiscount  

Thank you visit again 
""".trimIndent()
showSmsDialog(MyConstants.adbulRaufPhoneNumber, message) }

        binding.viewAndPrint.setOnClickListener {
             startReceiptActivity()
        }

        binding.doneBtn.setOnClickListener {

            if(givenAmount>=totalAmount){

//                val intent = Intent(this, MainActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
                showToast("done")

            }else{

                val alert=androidx.appcompat.app.AlertDialog.Builder(this@NewSaleActivity).setView(R.layout.ask_uddhar_or_discount_dialog).show()
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
        val intent=Intent(this@NewSaleActivity,ReceiptActivity::class.java)
        intent.putExtra("receiptNumber",receiptNumber.toString())

        if (UddharOrDiscount=="Uddhar"){
            if (resultAmount!=0){
                intent.putExtra("UddharOrDiscount",UddharOrDiscount)
                intent.putExtra("amount",resultAmount.toString())
                intent.putExtra("date","Date: ${MyUtils.getCurrentDateTime()}")

            }else{
                showToast("Enter collected amount.")
            }
        }else if (UddharOrDiscount=="Discount"){
            intent.putExtra("UddharOrDiscount",UddharOrDiscount)
            intent.putExtra("amount",resultAmount.toString())
            intent.putExtra("date","Date: ${MyUtils.getCurrentDateTime()}")
        }
        startActivity(intent)
    }


    fun showCustomerDialog(customerNames: List<String>) {

        val dialog= AlertDialog.Builder(this).setView(R.layout.add_customer_detail_dialog).show()

        val customerNameEdt: AutoCompleteTextView =dialog.findViewById(R.id.customerNameEdt)
        val phoneNumberEdt: EditText =dialog.findViewById(R.id.phoneNumberEdt)

        val saveBtn: Button =dialog.findViewById(R.id.saveBtn)
        val cancelBtn: Button =dialog.findViewById(R.id.cancelBtn)

        customerNameEdt.threshold=1
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, customerNames)
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

                val progressDialog= showProgressDialog(this@NewSaleActivity,"Saving...")
                val key=databaseReference.push().key.toString()

                val newSaleModel= NewSaleModel(key,name,phoneNumber,resultAmount.toString(),UddharOrDiscount,System.currentTimeMillis())

                lifecycleScope.launch {

                    val result=mainViewModel.uploadAnyModel("${getNewSalePath(auth)}$name/$key",newSaleModel)

                    result.whenSuccess {
                        lifecycleScope.launch {

                            val k=databaseReference.push().key.toString()
                            val listOfProductModels=saleViewModel.productModelList
                            val subTotal=saleViewModel.subTotalAmount.value
                            val grandTotal=saleViewModel.grandTotalAmount.value.toFloat().toInt()
                            val uploadSaleResult=mainViewModel.uploadAnyModel(getSaleReportPath(auth)+"$name/${getCurrentDate("yyyy/MM/dd")}/"+k,SaleReportModel(k,receiptNumber,MyUtils.getCurrentDateTime(),listOfProductModels,subTotal,UddharOrDiscount,resultAmount.toString(),grandTotal))

                            uploadSaleResult.whenSuccess {
                                showToast("Saved")
                                progressDialog.dismiss()
                                dialog.dismiss()
                            }

                            uploadSaleResult.whenError {
                                showToast(it.message.toString())
                                progressDialog.dismiss()
                                dialog.dismiss()
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