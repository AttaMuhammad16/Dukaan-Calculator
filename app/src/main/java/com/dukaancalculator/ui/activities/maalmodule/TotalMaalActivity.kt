package com.dukaancalculator.ui.activities.maalmodule

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.R
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityTotalMaalBinding
import com.dukaancalculator.ui.activities.salemodule.BankPaymentScreen
import com.dukaancalculator.ui.models.maalmodels.DeliveryInformationModel
import com.dukaancalculator.ui.models.commonmodels.ProductModel
import com.dukaancalculator.ui.viewmodel.MaalViewModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lymors.lycommons.utils.MyExtensions.onTextChange
import com.lymors.lycommons.utils.MyExtensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class TotalMaalActivity : AppCompatActivity() {
    lateinit var binding: ActivityTotalMaalBinding
    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var maalViewModel: MaalViewModel

    @Inject
    lateinit var auth:FirebaseAuth

    @Inject
    lateinit var databaseReference: DatabaseReference

    var productModelList= arrayListOf<ProductModel>()

    var total = 0f
    val listOfEditText= mutableListOf<EditText>()
    var previousListSize=0
    var bol=false
    lateinit var  invoiceImg:ImageView
    var  invoiceImgUri: String=""


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTotalMaalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        var listExp = maalViewModel.exp
        maalViewModel.addItemInExpArrayList(listExp,false)

        binding.addNewItemCard.setOnClickListener {
            maalViewModel.addItemInExpArrayList(listExp,true)
        }

        lifecycleScope.launch {
            maalViewModel.expArrayListFlow.collect{newList->
                if (newList.isNotEmpty()){
                    binding.totalItems.text=newList.size.toString()

                    listExp=newList as ArrayList<String>
                    if (previousListSize<newList.size){
                        if (!bol){
                            for ((i,data) in newList.withIndex()) {
                                addViews(data,i)
                            }
                            bol=true
                        }else{
                            addViews(newList.last(),newList.lastIndex)
                        }
                        previousListSize=newList.size
                    }

                }
            }
        }




        // back press
        binding.backImg.setOnClickListener {
            finish()
        }

        // support
        binding.support.setOnClickListener {
            MyUtils.sendMessageToWhatsApp(this, MyConstants.adbulRaufPhoneNumber, "")
        }

        binding.totalItems.text=listExp.size.toString()
        binding.dateTv.text= MyUtils.getCurrentDateTime()

        binding.dateTv.setOnClickListener {

            val dialog= AlertDialog.Builder(this).setView(R.layout.date_time_dialog).show()
            val dateAndTimeEdt: EditText =dialog.findViewById(R.id.dateAndTimeEdt)
            val saveBtn: Button =dialog.findViewById(R.id.saveBtn)
            val cancelBtn: Button =dialog.findViewById(R.id.cancelBtn)
            dateAndTimeEdt.setText(MyUtils.getCurrentDateTime())

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            saveBtn.setOnClickListener {
                binding.dateTv.text=dateAndTimeEdt.text.toString()
                dialog.dismiss()
            }

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        }



//      getGrand total
        maalViewModel.setGrandTotalAmount(total.toString())
        lifecycleScope.launch {
            maalViewModel.grandTotalAmount.collect{
                binding.grandTotalTv.text="Rs $it"
            }
        }

        binding.paymentCard.setOnClickListener {
            startNewSaleActivity(productModelList)
        }

        binding.bankPaymentCard.setOnClickListener {
            startActivity(Intent(this, MaalBankPaymentActivity::class.java))
        }

        // add delivery information.
        binding.addDeliveryInformation.setOnClickListener {
            showDeliveryInfoDialog()
        }

    }

    fun startNewSaleActivity(list: ArrayList<ProductModel>) {
        lifecycleScope.launch {
            maalViewModel.productModelList=list
            val intent= Intent(this@TotalMaalActivity, MaalNewSaleActivity::class.java)
            startActivity(intent)
        }
    }

    fun showDeliveryInfoDialog(){

        val dialog= AlertDialog.Builder(this).setView(R.layout.delivery_information_dialog).show()

        val distributorCompanyNameEdt: EditText =dialog.findViewById(R.id.distributorCompanyNameEdt)
        val supplierNameEdt: EditText =dialog.findViewById(R.id.supplierNameEdt)
        val supplierNumberEdt: EditText =dialog.findViewById(R.id.supplierNumberEdt)

        val addInvoiceBtn: Button =dialog.findViewById(R.id.addInvoiceBtn)
         invoiceImg =dialog.findViewById(R.id.invoiceImg)

        val saveBtn: Button =dialog.findViewById(R.id.saveBtn)
        val cancelBtn: Button =dialog.findViewById(R.id.cancelBtn)


        addInvoiceBtn.setOnClickListener {
            MyUtils.pickImage(12,this)
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {

            val distributorCompanyName=distributorCompanyNameEdt.text.toString().trim()
            val supplierName=supplierNameEdt.text.toString().trim()
            val supplierNumber=supplierNumberEdt.text.toString().trim()

            if (distributorCompanyName.isEmpty()){
                showToast("Enter distributor/CompanyName")
            }else if (supplierName.isEmpty()){
                showToast("Enter supplierName")
            }else if (supplierNumber.isEmpty()){
                showToast("Enter supplierNumber")
            }else{
                val key=databaseReference.push().key.toString()
                val deliveryInformationModel= DeliveryInformationModel(key, distributorCompanyName, supplierName, supplierNumber,invoiceImgUri)
                maalViewModel.setDeliveryInformationModel(deliveryInformationModel)
                showToast("Saved")
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==12&&resultCode==Activity.RESULT_OK){
            val uri=data?.data
            uri?.apply {
                invoiceImg.setImageURI(uri)
                invoiceImgUri=uri.toString()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    fun addViews(data:String, i:Int){

        var previousQuantity = 0.0f
        var previousPrice = 0

        val view = layoutInflater.inflate(R.layout.product_counter_sample_row, binding.linearLayout, false)

        val productNameEdt = view.findViewById<EditText>(R.id.productNameEdt)
        val quantityEdt = view.findViewById<EditText>(R.id.quantityEdt)
        val priceEdt = view.findViewById<EditText>(R.id.priceEdt)
        val totalTv = view.findViewById<TextView>(R.id.totalTv)

        val incrementImg = view.findViewById<ImageView>(R.id.incrementImg)
        val decrementImg = view.findViewById<ImageView>(R.id.decrementImg)

        listOfEditText.add(i,productNameEdt)

        if (data.contains("×")) {

            val parts = data.split('×')
            val price = parts[0].toFloatOrNull() ?: 0f
            val quantity = parts[1].toFloatOrNull() ?: 1f

            priceEdt.setText(price.toString())
            quantityEdt.setText(quantity.toString())

            total += price * quantity
            previousQuantity = quantity
            previousPrice = price.toInt()
            productModelList.add(i, ProductModel(UUID.randomUUID().toString(),"",price.toString(),quantity.toString()))
            totalTv.text=(price * quantity).toString()

        } else {

            val price = data.toFloatOrNull() ?: 0f

            total += price
            priceEdt.setText(price.toString())
            quantityEdt.setText("1")
            previousQuantity = 1.0f
            previousPrice = price.toInt()
            productModelList.add(i, ProductModel(UUID.randomUUID().toString(),"",price.toString(),"1"))
            totalTv.text=(price * 1).toString()
        }


        incrementImg.setOnClickListener {
            val currentQuantity = quantityEdt.text.toString().toFloatOrNull()
            val newQuantity = if (currentQuantity != null) {
                currentQuantity + 1
            } else {
                0f
            }
            quantityEdt.setText(newQuantity.toString())
        }


        decrementImg.setOnClickListener {
            val currentQuantity = quantityEdt.text.toString().toFloatOrNull()
            val newQuantity = if (currentQuantity != null) {
                currentQuantity - 1
            } else {
                0f
            }
            if (newQuantity >= 0) {
                quantityEdt.setText(newQuantity.toString())
            }
        }


        quantityEdt.onTextChange {changedQuantity->
            val newQuantity = changedQuantity.toFloatOrNull() ?: 0f
            total -= previousPrice * previousQuantity
            total += previousPrice * newQuantity
            maalViewModel.setGrandTotalAmount((total).toString())
            previousQuantity = newQuantity
            productModelList[i].productQuantity=changedQuantity
            totalTv.text=(previousPrice * newQuantity).toString()
        }

        priceEdt.onTextChange {changedPrice->
            val newPrice = changedPrice.toFloatOrNull() ?: 0f
            total -= previousQuantity * previousPrice
            total += previousQuantity * newPrice
            maalViewModel.setGrandTotalAmount((total).toString())
            previousPrice = newPrice.toInt()
            productModelList[i].productPrice=changedPrice
            totalTv.text=(newPrice * previousQuantity).toString()
        }

        productNameEdt.setText("No Name")
        productModelList[i].productName="No Name"

        productNameEdt.onTextChange {
            productModelList[i].productName=it
        }

        binding.linearLayout.addView(view)

    }

}