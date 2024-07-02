package com.dukaancalculator.ui.activities.kharchamodule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.atta.dukaancalculator.R
import com.atta.dukaancalculator.databinding.ActivityKharchaBinding
import com.atta.dukaancalculator.databinding.KharchaGridSampleRowBinding
import com.atta.dukaancalculator.databinding.KharchaSampleRowBinding
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyConstants.attaMuhammadNumber
import com.dukaancalculator.Utils.MyUtils.convertDateToDayMonthYearFormat
import com.dukaancalculator.Utils.MyUtils.convertToYearMonthDayFormate
import com.dukaancalculator.Utils.MyUtils.getCurrentDate
import com.dukaancalculator.Utils.MyUtils.increaseAndDecreaseDay
import com.dukaancalculator.Utils.MyUtils.sendMessageToWhatsApp
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.showDatePickerDialog
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.adapters.CustomSpinnerAdapter
import com.dukaancalculator.ui.models.commonmodels.UserModel
import com.dukaancalculator.ui.models.kharcha.ExpenseCategoryModel
import com.dukaancalculator.ui.models.kharcha.KharchaModel
import com.dukaancalculator.ui.viewmodel.KharchaViewModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lymors.lycommons.utils.MyExtensions.onTextChange
import com.lymors.lycommons.utils.MyExtensions.showToast
import com.lymors.lycommons.utils.Utils.shareText
import com.lymors.lycommons.utils.Utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@AndroidEntryPoint
class KharchaActivity : AppCompatActivity() {
    lateinit var binding: ActivityKharchaBinding

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var auth: FirebaseAuth
    var totalExpense = 0L
    var selectedCategory = ""
    var userModel: UserModel? = null
    lateinit var selectedExpenseCategoryModel: ExpenseCategoryModel

    @Inject
    lateinit var databaseReference: DatabaseReference

    @Inject
    lateinit var kharchaViewModel: KharchaViewModel
    var list = listOf<KharchaModel>()
    private val filteredList = mutableListOf<KharchaModel>()
    var isClicked = false
    var kharchaPath=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKharchaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()
        kharchaPath=MyConstants.getKharchaPath(auth)

        lifecycleScope.launch {
            mainViewModel.kharchaFlow.collect {
                list = it
                updateRecyclerView(it)
            }
        }

        binding.listLinear.setOnClickListener {
            handleViewToggle(false)
        }

        binding.groupLinear.setOnClickListener {
            handleViewToggle(true)
        }

        lifecycleScope.launch {
            userModel = mainViewModel.getAnyModel("Users/${auth.currentUser?.uid}", UserModel::class.java)
        }

        lifecycleScope.launch {
            mainViewModel.collectAnyModel( kharchaPath+ getCurrentDate("yyyy/MM/dd"), KharchaModel::class.java)
        }

        binding.expenseBtn.setOnClickListener {
            val date=binding.dateTv.text.toString()
            val nowCurrentDate = convertToYearMonthDayFormate(date)
            expenseSheetDialog(nowCurrentDate)
            collectKharchaData(nowCurrentDate)
        }

        var currentDate = getCurrentDate("yyyy/MM/dd")
        binding.dateTv.text = convertDateToDayMonthYearFormat(currentDate)

        binding.arrowBack.setOnClickListener {
            currentDate = changeDate(currentDate, -1)
        }

        binding.arrowForward.setOnClickListener {
            currentDate = changeDate(currentDate, 1)
        }

        binding.calendarImg.setOnClickListener {
            showDatePickerDialog(this@KharchaActivity) {
                binding.dateTv.text=it
                currentDate = convertToYearMonthDayFormate(it)
                collectKharchaData(currentDate)
            }
        }

        binding.dateTv.setOnClickListener {
            showDatePickerDialog(this@KharchaActivity) {
                binding.dateTv.text=it
                currentDate = convertToYearMonthDayFormate(it)
                collectKharchaData(currentDate)
            }
        }

        binding.searchEdt.onTextChange {
            filterList(it)
        }


        binding.support.setOnClickListener {
            sendMessageToWhatsApp(this, attaMuhammadNumber,"")
        }
        
        binding.shareImg.setOnClickListener {
            shareText("\uD83D\uDED2 Welcome to Dukaan Calculator! \uD83D\uDCF1\uD83D\uDCBC\n" +
                    "\n" +
                    "Discover the ultimate solution for managing your store efficiently! \uD83D\uDE80\n" +
                    "\n" +
                    "\uD83D\uDCCA Track sales, manage inventory, and create beautiful receipts effortlessly with Dukaan Calculator. \uD83D\uDCDD\uD83D\uDCBC\n" +
                    "\n" +
                    "\uD83D\uDD0D Never miss a sale again with our powerful inventory management features! \uD83D\uDCA1\n" +
                    "\n" +
                    "\uD83D\uDCA1 Download Dukaan Calculator now for a hassle-free store management experience! \uD83D\uDCF2\n" +
                    "\n" +
                    "https://play.google.com/store/apps/details?id=com.dukaancalculator\n\n" +
                    "Join thousands of satisfied users who trust Dukaan Calculator for their store management needs! \uD83D\uDCBC✨","Dukaan Calculator")
        }

    }

    private fun handleViewToggle(isGroupView: Boolean) {
        isClicked = isGroupView
        val listColor = if (isGroupView) R.color.gray else R.color.kharcha_blue
        val groupColor = if (isGroupView) R.color.kharcha_blue else R.color.gray

        binding.listLinear.setBackgroundColor(ContextCompat.getColor(this@KharchaActivity, listColor))
        binding.groupLinear.setBackgroundColor(ContextCompat.getColor(this@KharchaActivity, groupColor))
        updateRecyclerView(if (filteredList.isEmpty()) mainViewModel.kharchaFlow.value else filteredList)
    }

    private fun changeDate(currentDate: String, offset: Int): String {
        val newDate = increaseAndDecreaseDay(currentDate, offset)
        lifecycleScope.launch {
            mainViewModel.collectAnyModel(kharchaPath + newDate, KharchaModel::class.java)
        }
        binding.dateTv.text = convertDateToDayMonthYearFormat(newDate)
        return newDate
    }

    private fun collectKharchaData(date: String) {
        lifecycleScope.launch {
            mainViewModel.collectAnyModel(kharchaPath + date, KharchaModel::class.java)
        }
    }

    private fun updateRecyclerView(list: List<KharchaModel>) {
        if (isClicked) gridRecyclerView(list) else linearRecyclerView(list)
    }

    private fun filterList(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(list)
        } else {
            list.filterTo(filteredList) { it.category.contains(query, ignoreCase = true) }
        }
        updateRecyclerView(filteredList)
    }

    private fun linearRecyclerView(list: List<KharchaModel>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this@KharchaActivity)
        binding.recyclerView.setData(list, KharchaSampleRowBinding::inflate) { binding, item, _ ->
            binding.colorOftheView.setBackgroundColor(item.color)
            binding.categoryName.text = item.category
            binding.shopOwnerName.text = "By ${item.shopOwnerName}"
            binding.expenseTv.text = "Rs ${item.expense}"
            binding.noteTv.visibility = if (item.noteOfExpense.isEmpty()) View.GONE else View.VISIBLE
            binding.noteTv.text = item.noteOfExpense
            binding.cardView.setOnLongClickListener {
                deleteExpenseDialog(item)
                true
            }
        }
        binding.totalExpenseTv.text = "Rs ${list.sumOf { it.expense }}"
    }

    @SuppressLint("SetTextI18n")
    private fun gridRecyclerView(list: List<KharchaModel>) {
        binding.recyclerView.layoutManager = GridLayoutManager(this@KharchaActivity, 3)
        binding.recyclerView.setData(list, KharchaGridSampleRowBinding::inflate) { binding, item, _ ->
            binding.linear.setBackgroundColor(item.color)
            binding.categoryName.text = item.category
            binding.expense.text = "Rs ${item.expense}"
        }
        binding.totalExpenseTv.text = "Rs ${list.sumOf { it.expense }}"
    }

    fun expenseSheetDialog(currentDate: String) {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.add_expense_sheet_dialog, null)

        val categoryFirstCharacterCard = view.findViewById<CardView>(R.id.CategoryFirstCharacterCard)
        val topCard = view.findViewById<CardView>(R.id.topCard)
        val topConstraint = view.findViewById<ConstraintLayout>(R.id.topConstraint)
        val categoryFirstCharacterTv = view.findViewById<TextView>(R.id.CategoryFirstCharacterTv)
        val amountEdt = view.findViewById<EditText>(R.id.amountEdt)
        val noteEdt = view.findViewById<EditText>(R.id.noteEdt)
        val currentDateTv = view.findViewById<TextView>(R.id.currentDateTv)
        val expenseBtn = view.findViewById<Button>(R.id.expenseBtn)
        val spinner = view.findViewById<Spinner>(R.id.spinner)
        val dropDownImg = view.findViewById<ImageView>(R.id.dropDownImg)

        val currentDateDDMMYY = getCurrentDate("dd/MM/yyyy")
        currentDateTv.text = currentDateDDMMYY

        val categories = listOf(
            ExpenseCategoryModel(ContextCompat.getColor(this@KharchaActivity, R.color.white), "Select Category", "S"),
            ExpenseCategoryModel(ContextCompat.getColor(this@KharchaActivity, R.color.item1), "Default", "D"),
            ExpenseCategoryModel(ContextCompat.getColor(this@KharchaActivity, R.color.item2), "Fuel", "F"),
            ExpenseCategoryModel(ContextCompat.getColor(this@KharchaActivity, R.color.item3), "Food", "F"),
            ExpenseCategoryModel(ContextCompat.getColor(this@KharchaActivity, R.color.item4), "Bill", "B"),
            ExpenseCategoryModel(ContextCompat.getColor(this@KharchaActivity, R.color.item5), "Salary", "S"),
            ExpenseCategoryModel(ContextCompat.getColor(this@KharchaActivity, R.color.item6), "Rent", "R"),
            ExpenseCategoryModel(ContextCompat.getColor(this@KharchaActivity, R.color.item7), "Repairs", "R"),
            ExpenseCategoryModel(ContextCompat.getColor(this@KharchaActivity, R.color.item8), "Advertising", "A")
        )

        val adapter = CustomSpinnerAdapter(this, R.layout.expense_category_sample_row, categories)
        spinner.adapter = adapter

        dropDownImg.setOnClickListener {
            spinner.performClick()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedExpenseCategoryModel = categories[position]
                val color = selectedExpenseCategoryModel.color
                topCard.setCardBackgroundColor(color)
                spinner.setBackgroundColor(color)
                topConstraint.setBackgroundColor(color)
                categoryFirstCharacterTv.text = selectedExpenseCategoryModel.firstLetter
                selectedCategory = selectedExpenseCategoryModel.categoryName

                view?.let {
                    it.findViewById<ConstraintLayout>(R.id.itemConstraintBack)?.setBackgroundColor(color)
                    it.findViewById<ConstraintLayout>(R.id.topItemConstraint)?.setBackgroundColor(color)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        expenseBtn.setOnClickListener {
            val amount = amountEdt.text.toString().trim()
            val note = noteEdt.text.toString().trim()

            when {
                selectedCategory == "Select Category" -> showToast("Select Category")
                amount.isEmpty() -> {
                    showToast("Enter amount")
                    amountEdt.requestFocus()
                    amountEdt.showKeyboard()
                }
                else -> {
                    val key = databaseReference.push().key.toString()
                    val kharchaModel = KharchaModel(key, selectedExpenseCategoryModel.color, userModel?.userName ?: "unknown", amount.toLong(), currentDate, selectedExpenseCategoryModel.categoryName, note)
                    lifecycleScope.launch {
                        mainViewModel.uploadAnyModel("$kharchaPath$currentDate/$key", kharchaModel)
                        bottomSheetDialog.dismiss()
                    }
                }
            }
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    fun deleteExpenseDialog(item: KharchaModel) {
        val dialog = AlertDialog.Builder(this).setView(R.layout.delete_expense_dialog).show()

        val yes=dialog.findViewById<Button>(R.id.yesBtn)
        val noBtn=dialog.findViewById<Button>(R.id.noBtn)

        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        yes.setOnClickListener {
            lifecycleScope.launch {
                databaseReference.child(kharchaPath+item.date+"/"+item.key).removeValue().await()
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


}
