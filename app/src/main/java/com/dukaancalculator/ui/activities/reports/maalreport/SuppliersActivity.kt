package com.dukaancalculator.ui.activities.reports.maalreport

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.dukaancalculator.R
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.databinding.ActivityCustomersBinding
import com.dukaancalculator.databinding.ActivitySuppliersBinding
import com.dukaancalculator.databinding.CustomerSaleReportSampleRowBinding
import com.dukaancalculator.databinding.SupplierMaalReportSampleRowBinding
import com.dukaancalculator.ui.activities.reports.salereport.SaleReportActivity
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.lymors.lycommons.utils.MyExtensions.onTextChange
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class SuppliersActivity : AppCompatActivity() {

    @Inject
    lateinit var mainViewModel: MainViewModel
    @Inject
    lateinit var auth: FirebaseAuth

    lateinit var binding: ActivitySuppliersBinding
    var list = listOf<String>()
    private val filteredList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuppliersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        binding.backImg.setOnClickListener {
            finish()
        }

        lifecycleScope.launch {
            val progressDialog = MyUtils.showProgressDialog(this@SuppliersActivity, "Loading...")
            mainViewModel.getSupplierName(MyConstants.getNewMaalPath(auth)).collect {
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
            SupplierMaalReportSampleRowBinding::inflate
        ) { binding, item, position ->

            binding.supplierNameTv.text = item

            binding.cardView.setOnClickListener {
                val intent= Intent(this@SuppliersActivity, MaalReportActivity::class.java)
                intent.putExtra("supplierName",item)
                startActivity(intent)
            }
        }
    }


}