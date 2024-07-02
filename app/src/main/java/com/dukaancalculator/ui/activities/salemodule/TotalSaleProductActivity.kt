package com.dukaancalculator.ui.activities.salemodule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.atta.dukaancalculator.R
import com.atta.dukaancalculator.databinding.ActivityTotalSaleProductBinding
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils.calculateAmount
import com.dukaancalculator.Utils.MyUtils.calculatePercentageChange
import com.dukaancalculator.Utils.MyUtils.getCurrentDateTime
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.models.commonmodels.ProductModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.dukaancalculator.ui.viewmodel.SaleViewModel
import com.lymors.lycommons.utils.MyExtensions.onTextChange
import com.lymors.lycommons.utils.MyExtensions.showToast
import com.lymors.lycommons.utils.MyExtensions.toIntOrDefault
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class TotalSaleProductActivity : AppCompatActivity() {
    lateinit var binding: ActivityTotalSaleProductBinding
    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var saleViewModel: SaleViewModel

    var productModelList= arrayListOf<ProductModel>()

    var total = 0.0f
    var discount = 0
    val listOfEditText= mutableListOf<EditText>()
    var previousListSize=0
    var bol=false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTotalSaleProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        var listExp = saleViewModel.exp
        saleViewModel.addItemInExpArrayList(listExp,false)

        binding.addNewItemCard.setOnClickListener {
            saleViewModel.addItemInExpArrayList(listExp,true)
        }

        lifecycleScope.launch {
            saleViewModel.expArrayListFlow.collect{newList->
                if (newList.isNotEmpty()){
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
            sendMessageToWhatsApp(this, MyConstants.attaMuhammadNumber,"")
        }

        binding.totalItems.text=listExp.size.toString()
        binding.dateTv.text=getCurrentDateTime()

        binding.dateTv.setOnClickListener {

            val dialog=AlertDialog.Builder(this@TotalSaleProductActivity).setView(R.layout.date_time_dialog).show()
            val dateAndTimeEdt:EditText=dialog.findViewById(R.id.dateAndTimeEdt)
            val saveBtn:Button=dialog.findViewById(R.id.saveBtn)
            val cancelBtn:Button=dialog.findViewById(R.id.cancelBtn)
            dateAndTimeEdt.setText(getCurrentDateTime())

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            saveBtn.setOnClickListener {
                binding.dateTv.text=dateAndTimeEdt.text.toString()
                dialog.dismiss()
            }

        }




//     sub total
        saleViewModel.setSubTotalAmount(total.toString())
        lifecycleScope.launch {
            saleViewModel.subTotalAmount.collect{
                binding.subTotalTv.text="Rs $it"
            }
        }
        

//      getGrand total
        saleViewModel.setGrandTotalAmount(total.toString())
        lifecycleScope.launch {
            saleViewModel.grandTotalAmount.collect{
                binding.grandTotalTv.text="Rs $it"
            }
        }


        // discountAmount
        lifecycleScope.launch {
            saleViewModel.discountAmount.collect{
                if (it.isNotEmpty()) {
                    binding.discountLinear.visibility = View.VISIBLE
                    binding.amount.text = "Rs $it"
                } else {
                    binding.discountLinear.visibility = View.GONE
                }
            }
        }

        // discountPercentage
        lifecycleScope.launch {
            saleViewModel.discountPercentage.collect{
                if (it.isNotEmpty()) {
                    binding.discountLinear.visibility = View.VISIBLE
                    binding.discountPercentage.text = "(${String.format("%.2f", calculatePercentageChange(total, it.toFloatOrNull() ?: 0f))} %)"
                } else {
                    binding.discountLinear.visibility = View.GONE
                }
            }
        }


        binding.paymentCard.setOnClickListener {
            startNewSaleActivity(productModelList)
        }

        binding.bankPaymentCard.setOnClickListener {
            startActivity(Intent(this@TotalSaleProductActivity,BankPaymentScreen::class.java))
        }


        binding.discountCard.setOnClickListener {

            val dialog=AlertDialog.Builder(this).setView(R.layout.dicount_dialog).show()
            val discountEdt:EditText=dialog.findViewById(R.id.discountEdt)
            val checkBox:CheckBox=dialog.findViewById(R.id.checkBox)
            val saveBtn:Button=dialog.findViewById(R.id.saveBtn)
            val cancelBtn:Button=dialog.findViewById(R.id.cancelBtn)
            val totalAmount:TextView=dialog.findViewById(R.id.totalAmount)

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            totalAmount.text="Rs: $total"

            var discountAmount=0.0f
            var amountChanged=0


            discountEdt.onTextChange {a->
                amountChanged=a.trim().toIntOrDefault(0)
                if (checkBox.isChecked){
                    discountAmount=calculateAmount(total,amountChanged)
                }else{
                    discountAmount=amountChanged.toFloat()
                }
            }

            saveBtn.setOnClickListener {

                lifecycleScope.launch {
                    if (checkBox.isChecked){
                        discountAmount=calculateAmount(total,amountChanged)
                    }else{
                        discountAmount=amountChanged.toFloat()
                    }

                    if (discountAmount>total){
                        showToast("Enter correct amount")
                    }else{
                        val newTotal=total-discountAmount
                        saleViewModel.setGrandTotalAmount(newTotal.toString())
                        saleViewModel.setDiscountAmount(discountAmount.toString())
                        saleViewModel.setPercentage(discountAmount.toString())
                        discount=discountAmount.toInt()
                        dialog.cancel()
                    }
                }
            }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun startNewSaleActivity(list: ArrayList<ProductModel>) {
        lifecycleScope.launch {
            saleViewModel.productModelList=list
            val intent=Intent(this@TotalSaleProductActivity,NewSaleActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    fun addViews(data:String, i:Int){

        var previousQuantity = 0.0f
        var previousPrice = 0.0f

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
            previousPrice = price
            productModelList.add(i, ProductModel(UUID.randomUUID().toString(),"",price.toString(),quantity.toString(),(price * quantity).toString()))
            totalTv.text=(price * quantity).toString()

        } else {

            val price = data.toFloatOrNull() ?: 0f

            total += price
            priceEdt.setText(price.toString())
            quantityEdt.setText("1")
            previousQuantity = 1.0f
            previousPrice = price
            productModelList.add(i, ProductModel(UUID.randomUUID().toString(),"",price.toString(),"1",(price * 1).toString()))
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
            saleViewModel.setGrandTotalAmount((total - discount).toString())
            binding.subTotalTv.text = "Rs $total"
            previousQuantity = newQuantity
            productModelList[i].productQuantity=changedQuantity
            productModelList[i].total=(previousPrice * newQuantity).toString()
            saleViewModel.setSubTotalAmount(total.toString())
            totalTv.text=(previousPrice * newQuantity).toString()
        }

        priceEdt.onTextChange {changedPrice->
            val newPrice = changedPrice.toFloatOrNull() ?: 0f
            total -= previousQuantity * previousPrice
            total += previousQuantity * newPrice
            saleViewModel.setGrandTotalAmount((total - discount).toString())
            binding.subTotalTv.text = "Rs $total"
            previousPrice = newPrice
            productModelList[i].productPrice=changedPrice
            productModelList[i].total=(previousQuantity * newPrice).toString()
            saleViewModel.setSubTotalAmount(total.toString())
            totalTv.text=(newPrice * previousQuantity).toString()
        }
        productModelList[i].productName="No Name"
        productNameEdt.onTextChange {
            productModelList[i].productName=it
        }
        binding.linearLayout.addView(view)
    }



}