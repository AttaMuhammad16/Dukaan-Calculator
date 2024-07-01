package com.dukaancalculator.ui.activities.reports.salereport

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.showProgressDialog
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityCustomersBinding
import com.dukaancalculator.databinding.CustomerSaleReportSampleRowBinding
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.lymors.lycommons.utils.MyExtensions.onTextChange
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class CustomersActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var auth: FirebaseAuth

    lateinit var binding: ActivityCustomersBinding
    var list = listOf<String>()
    private val filteredList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        binding.backImg.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            val progressDialog = showProgressDialog(this@CustomersActivity, "Loading...")
            mainViewModel.getCustomerNames(MyConstants.getNewSalePath(auth)).collect {
                list = it
                setUpRecyclerView(list)
                progressDialog.dismiss()
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
            list.filterTo(filteredList) { it.contains(query, ignoreCase = true) }
        }
        setUpRecyclerView(filteredList)
    }

    fun setUpRecyclerView(list: List<String>) {
        binding.recyclerView.setData(
            list,
            CustomerSaleReportSampleRowBinding::inflate
        ) { binding, item, position ->

            binding.customerNameTv.text = item

            binding.cardView.setOnClickListener {
                val intent=Intent(this@CustomersActivity,SaleReportActivity::class.java)
                intent.putExtra("customerName",item)
                startActivity(intent)
            }

        }
    }

}