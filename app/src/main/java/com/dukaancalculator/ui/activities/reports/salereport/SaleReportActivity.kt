package com.dukaancalculator.ui.activities.reports.salereport

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.atta.dukaancalculator.R
import com.atta.dukaancalculator.databinding.ActivitySaleReportBinding
import com.atta.dukaancalculator.databinding.SaleReportSampleRowBinding
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.models.salemodels.SaleReportModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.lymors.lycommons.utils.MyExtensions.onTextChange
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SaleReportActivity : AppCompatActivity() {

    lateinit var binding: ActivitySaleReportBinding
    var bundleCustomerName:String=""
    var saleReportPath:String=""
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var mainViewModel:MainViewModel
    private val filteredList = mutableListOf<SaleReportModel>()
    var list = listOf<SaleReportModel>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySaleReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundleCustomerName=intent.getStringExtra("customerName")!!
        saleReportPath=MyConstants.getSaleReportPath(auth)+bundleCustomerName+"/"
        statusBarColor()

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.support.setOnClickListener {
            MyUtils.sendMessageToWhatsApp(this, MyConstants.attaMuhammadNumber, "")
        }


        lifecycleScope.launch {
            mainViewModel.collectAnyModel(saleReportPath+MyUtils.getCurrentDate("yyyy/MM/dd"), SaleReportModel::class.java)
        }

        lifecycleScope.launch {
            mainViewModel.saleReportFlow.collect{
                list=it
                setRecyclerView(it)
                val totalSale=it.sumOf { it.grandTotal}
                binding.totalSaleTv.text= "Rs $totalSale"

            }
        }

        var currentDate = MyUtils.getCurrentDate("yyyy/MM/dd")
        binding.dateTv.text = MyUtils.convertDateToDayMonthYearFormat(currentDate)

        binding.arrowBack.setOnClickListener {
            currentDate = changeDate(currentDate, -1)
            collectSaleReportModelData(currentDate)
        }

        binding.arrowForward.setOnClickListener {
            currentDate = changeDate(currentDate, 1)
            collectSaleReportModelData(currentDate)
        }


        binding.calendarImg.setOnClickListener {
            MyUtils.showDatePickerDialog(this@SaleReportActivity) {
                binding.dateTv.text = it
                currentDate = MyUtils.convertToYearMonthDayFormate(it)
                collectSaleReportModelData(currentDate)
            }
        }


        binding.dateTv.setOnClickListener {
            MyUtils.showDatePickerDialog(this@SaleReportActivity) {
                binding.dateTv.text = it
                currentDate = MyUtils.convertToYearMonthDayFormate(it)
                collectSaleReportModelData(currentDate)
            }
        }

        binding.searchEdt.onTextChange {
            filterList(it)
        }

        binding.support.setOnClickListener {
            sendMessageToWhatsApp(this,MyConstants.attaMuhammadNumber,"")
        }


    }

    private fun filterList(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(list)
        } else {
            list.filterTo(filteredList) { it.receiptNumber.contains(query, ignoreCase = true)|| it.date.contains(query, ignoreCase = true)}
        }
        setRecyclerView(filteredList)
    }

    private fun changeDate(currentDate: String, offset: Int): String {
        val newDate = MyUtils.increaseAndDecreaseDay(currentDate, offset)
        lifecycleScope.launch {
            mainViewModel.collectAnyModel(saleReportPath + newDate, SaleReportModel::class.java)
        }
        binding.dateTv.text = MyUtils.convertDateToDayMonthYearFormat(newDate)
        return newDate
    }

    private fun collectSaleReportModelData(date: String) {
        lifecycleScope.launch {
            mainViewModel.collectAnyModel(saleReportPath + date, SaleReportModel::class.java)
        }
    }


    fun setRecyclerView(list:List<SaleReportModel>){

        binding.recyclerView.setData(list,
            SaleReportSampleRowBinding::inflate){ binding, item, position ->

            binding.receiptNumberTv.text=item.receiptNumber
            binding.dateTv.text=item.date

            if (item.uddharOrDiscount=="Uddhar"){
                binding.colorOftheView.setBackgroundColor(ContextCompat.getColor(this@SaleReportActivity, com.lymors.lycommons.R.color.red))
            }else{
                binding.colorOftheView.setBackgroundColor(ContextCompat.getColor(this@SaleReportActivity, R.color.green))
            }

            binding.cardView.setOnClickListener {
                val intent=Intent(this@SaleReportActivity,ViewSaleReportActivity::class.java)
                intent.putExtra("saleReportModel",item)
                startActivity(intent)
            }
        }

    }

}