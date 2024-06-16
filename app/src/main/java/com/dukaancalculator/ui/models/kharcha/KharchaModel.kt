package com.dukaancalculator.ui.models.kharcha

import com.dukaancalculator.ui.models.commonmodels.ParentModel


data class KharchaModel(
    override var key: String="",
    var color:Int=0,
    var shopOwnerName:String="",
    var expense:Long=0,
    var date:String="",
    var category: String="",
    var noteOfExpense:String=""
):ParentModel(key)