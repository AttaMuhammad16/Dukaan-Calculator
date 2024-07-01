package com.dukaancalculator.ui.activities.reports.maalreport

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.R
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityMaalReportBinding
import com.dukaancalculator.databinding.MaalReportSampleRowBinding
import com.dukaancalculator.ui.models.maalmodels.MaalReportModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.lymors.lycommons.utils.MyExtensions.onTextChange
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MaalReportActivity : AppCompatActivity() {

    lateinit var binding: ActivityMaalReportBinding
    var bundleSupplierName:String=""
    var maalReportPath:String=""
    @Inject
    lateinit var auth: FirebaseAuth
    @Inject
    lateinit var mainViewModel: MainViewModel
    private val filteredList = mutableListOf<MaalReportModel>()
    var list = listOf<MaalReportModel>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMaalReportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bundleSupplierName=intent.getStringExtra("supplierName")!!
        maalReportPath= MyConstants.getMaalReportPath(auth)+bundleSupplierName+"/"
        statusBarColor()

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.support.setOnClickListener {
            MyUtils.sendMessageToWhatsApp(this, MyConstants.attaMuhammadNumber, "")
        }


        lifecycleScope.launch {
            mainViewModel.collectAnyModel(maalReportPath+ MyUtils.getCurrentDate("yyyy/MM/dd"), MaalReportModel::class.java)
        }

        lifecycleScope.launch {
            mainViewModel.maalReportFlow.collect{
                list=it
                setRecyclerView(it)
                val totalMaal=it.sumOf { it.grandTotal.toString().toFloat().toInt()}
                binding.totalMaalTv.text= "Rs $totalMaal"

            }
        }

        var currentDate = MyUtils.getCurrentDate("yyyy/MM/dd")
        binding.dateTv.text = MyUtils.convertDateToDayMonthYearFormat(currentDate)

        binding.arrowBack.setOnClickListener {
            currentDate = changeDate(currentDate, -1)
            collectMaalReportModelData(currentDate)
        }

        binding.arrowForward.setOnClickListener {
            currentDate = changeDate(currentDate, 1)
            collectMaalReportModelData(currentDate)
        }


        binding.calendarImg.setOnClickListener {
            MyUtils.showDatePickerDialog(this) {
                binding.dateTv.text = it
                currentDate = MyUtils.convertToYearMonthDayFormate(it)
                collectMaalReportModelData(currentDate)
            }
        }


        binding.dateTv.setOnClickListener {
            MyUtils.showDatePickerDialog(this) {
                binding.dateTv.text = it
                currentDate = MyUtils.convertToYearMonthDayFormate(it)
                collectMaalReportModelData(currentDate)
            }
        }

        binding.searchEdt.onTextChange {
            filterList(it)
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
            mainViewModel.collectAnyModel(maalReportPath + newDate, MaalReportModel::class.java)
        }
        binding.dateTv.text = MyUtils.convertDateToDayMonthYearFormat(newDate)
        return newDate
    }

    private fun collectMaalReportModelData(date: String) {
        lifecycleScope.launch {
            mainViewModel.collectAnyModel(maalReportPath + date, MaalReportModel::class.java)
        }
    }


    fun setRecyclerView(list: List<MaalReportModel>){

        binding.recyclerView.setData(list, MaalReportSampleRowBinding::inflate){ binding, item, position ->

            binding.receiptNumberTv.text=item.receiptNumber
            binding.dateTv.text=item.date

            if (item.uddharOrDiscount=="Uddhar"){
                binding.colorOftheView.setBackgroundColor(ContextCompat.getColor(this, com.lymors.lycommons.R.color.red))
            }else{
                binding.colorOftheView.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
            }

            binding.cardView.setOnClickListener {
                val intent= Intent(this, ViewMaalReportActivity::class.java)
                intent.putExtra("maalReportModel",item)
                startActivity(intent)
            }

        }

    }

}