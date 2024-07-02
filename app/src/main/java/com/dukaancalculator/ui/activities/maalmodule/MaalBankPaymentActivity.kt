package com.dukaancalculator.ui.activities.maalmodule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.lifecycleScope
import com.atta.dukaancalculator.R
import com.atta.dukaancalculator.databinding.ActivityMaalBankPaymentBinding
import com.atta.dukaancalculator.databinding.MaalBankPaymentDetailSampleRowBinding
import com.dukaancalculator.Utils.MyConstants
import com.dukaancalculator.Utils.MyUtils
import com.dukaancalculator.Utils.MyUtils.getCurrentDateTime
import com.dukaancalculator.Utils.MyUtils.setData
import com.dukaancalculator.Utils.MyUtils.statusBarColor
import com.dukaancalculator.ui.models.maalmodels.MaalBankDetailsModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.lymors.lycommons.utils.MyExtensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MaalBankPaymentActivity : AppCompatActivity() {
    @Inject
    lateinit var databaseReference: DatabaseReference
    @Inject
    lateinit var auth:FirebaseAuth

    @Inject
    lateinit var mainViewModel: MainViewModel


    lateinit var binding: ActivityMaalBankPaymentBinding

    var path=""
    var selectedItem=""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMaalBankPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBarColor()

        path= MyConstants.getMaalBankDetailsPath(auth)

        binding.backImg.setOnClickListener {
            finish()
        }

        binding.floatingActionButton.setOnClickListener {
            showDialogToAddBankDetail()
        }

        lifecycleScope.launch {
            mainViewModel.collectAnyModel(path, MaalBankDetailsModel::class.java)
        }

        lifecycleScope.launch {
            mainViewModel.maalBankDetailFlow.collect{
                binding.recyclerView.setData(it,
                    MaalBankPaymentDetailSampleRowBinding::inflate){ binding, item, position ->

                    binding.accountTypeTv.text="AccountType: ${item.accountType}"
                    binding.payedViaTv.text="Payed Via: ${item.payedVia}"
                    binding.amountTv.text="Amount: ${item.amount}"
                    binding.dateTv.text=item.date

                    binding.editImg.setOnClickListener{
                        showEditDetailsDialog(item)
                    }

                }
            }
        }
    }

    private fun showDialogToAddBankDetail() {

        val dialog= AlertDialog.Builder(this).setView(R.layout.maal_bank_detail_dialog).show()

        val amountEdt=dialog.findViewById<EditText>(R.id.amountEdt)
        val accountTypeEdt=dialog.findViewById<EditText>(R.id.accountTypeEdt)
        val spinner=dialog.findViewById<AppCompatSpinner>(R.id.spinner)
        val dateEdt=dialog.findViewById<EditText>(R.id.dateEdt)

        val cancelBtn=dialog.findViewById<Button>(R.id.cancelBtn)
        val saveBtn=dialog.findViewById<Button>(R.id.saveBtn)

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dateEdt.setText(getCurrentDateTime())

        setSpinner(listOf("Select","Cheque","Pay order","Online Transfer"),spinner)

        saveBtn.setOnClickListener {

            val amount=amountEdt.text.toString().trim()
            val accountType=accountTypeEdt.text.toString().trim()
            val date=dateEdt.text.toString().trim()

            if (auth.currentUser!=null){

                val key=databaseReference.push().key.toString()
                val maalDetailsPath=path+key

                if (amount.isEmpty()){
                    showToast("Enter amount")
                }else if (accountType.isEmpty()){
                    showToast("Enter Account Name")
                }else if (selectedItem=="Select"){
                    showToast("Select paymentType")
                }else if (date.isEmpty()){
                    showToast("Enter Date")
                }else{

                    val maalBankDetailModel= MaalBankDetailsModel(key,amount, accountType,selectedItem,date)
                    val progressDialog= MyUtils.showProgressDialog(this, "Saving...")
                    lifecycleScope.launch {
                        val result=mainViewModel.uploadAnyModel(maalDetailsPath,maalBankDetailModel)
                        result.whenSuccess {
                            showToast("Saved")
                            progressDialog.dismiss()
                            dialog.dismiss()
                        }
                        result.whenError {
                            showToast(it.message.toString())
                            progressDialog.dismiss()
                        }
                    }
                }
            }else{
                showToast("Please Sign In")
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    private fun showEditDetailsDialog(data: MaalBankDetailsModel){
        val dialog= AlertDialog.Builder(this).setView(R.layout.maal_bank_detail_dialog).show()

        val amountEdt=dialog.findViewById<EditText>(R.id.amountEdt)
        val accountTypeEdt=dialog.findViewById<EditText>(R.id.accountTypeEdt)
        val spinner=dialog.findViewById<AppCompatSpinner>(R.id.spinner)
        val dateEdt=dialog.findViewById<EditText>(R.id.dateEdt)

        val cancelBtn=dialog.findViewById<Button>(R.id.cancelBtn)
        val saveBtn=dialog.findViewById<Button>(R.id.saveBtn)

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        amountEdt.setText(data.amount)
        accountTypeEdt.setText(data.accountType)
        dateEdt.setText(data.date)

        val listOfType=listOf("Select","Cheque","Pay order","Online Transfer")
        setSpinner(listOfType,spinner)
        spinner.setSelection(listOfType.indexOf(data.payedVia))

        saveBtn.setOnClickListener {

            val amount=amountEdt.text.toString().trim()
            val accountType=accountTypeEdt.text.toString().trim()
            val date=dateEdt.text.toString().trim()

            if (auth.currentUser!=null){
                val bankDetailsPath=path+data.key

                if (amount.isEmpty()){
                    showToast("Enter amount")
                }else if (accountType.isEmpty()){
                    showToast("Enter Account Name")
                }else if (selectedItem=="Select"){
                    showToast("Select paymentType")
                }else if (date.isEmpty()){
                    showToast("Enter Date")
                }else{

                    val maalBankDetailModel= MaalBankDetailsModel(data.key,amount, accountType,selectedItem,date)
                    val progressDialog= MyUtils.showProgressDialog(this, "Saving...")

                    lifecycleScope.launch {
                        val result=mainViewModel.uploadAnyModel(bankDetailsPath,maalBankDetailModel)
                        result.whenSuccess {
                            showToast("Saved")
                            progressDialog.dismiss()
                            dialog.dismiss()
                        }
                        result.whenError {
                            showToast(it.message.toString())
                            progressDialog.dismiss()
                        }
                    }
                }
            }else{
                showToast("Please Sign In")
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    private fun setSpinner(items: List<String>, spinner: AppCompatSpinner) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                selectedItem = items[position]
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
    //            blank
            }
        }
    }


}