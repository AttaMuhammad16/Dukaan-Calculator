package com.dukaancalculator.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dukaancalculator.data.mainrepository.MainRepositoryLocal
import com.dukaancalculator.ui.models.commonmodels.ProductModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SaleViewModel @Inject constructor(private val mainRepositoryLocal: MainRepositoryLocal):ViewModel() {

    var exp = ArrayList<String>()

    // expArrayListFlow
    private val _expArrayListFlow = MutableStateFlow(listOf<String>())
    val expArrayListFlow= _expArrayListFlow.asStateFlow()


    var productModelList = ArrayList<ProductModel>()

    private val _calculatorData = MutableStateFlow("")
    val calculatorData= _calculatorData.asStateFlow()

    private val _topFlow = MutableStateFlow("")
    val topFlow= _topFlow.asStateFlow()


    // total ProductCounter activity
    private val _totalAmount = MutableStateFlow("")
    val grandTotalAmount= _totalAmount.asStateFlow()



    // adding item in to expArrayListFlow
    fun addItemInExpArrayList(list: List<String>,bol:Boolean) {
        var newList=list
        if (bol){
            newList = list.toMutableList().apply {
                add("0")
            }
        }
        _expArrayListFlow.value = newList
    }


    fun setGrandTotalAmount(data:String){
        _totalAmount.value=data
    }



    //  sub total amount
    private val _subTotalAmount = MutableStateFlow("")
    val subTotalAmount= _subTotalAmount.asStateFlow()

    fun setSubTotalAmount(data:String){
        _subTotalAmount.value=data
    }


    // amount
    private val _discountAmount = MutableStateFlow("")
    val discountAmount= _discountAmount.asStateFlow()
    fun setDiscountAmount(data:String){
        _discountAmount.value=data
    }




    // percentage
    private val _discountPercentage = MutableStateFlow("")
    val discountPercentage= _discountPercentage.asStateFlow()
    fun setPercentage(data:String){
        _discountPercentage.value=data
    }





    fun setCalculatorData(data:String){
        _calculatorData.value=data
    }

    fun setCalculatorTopData(data:String){
        _topFlow.value=data
    }

}