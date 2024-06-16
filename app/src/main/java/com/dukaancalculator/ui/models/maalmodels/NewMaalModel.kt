package com.dukaancalculator.ui.models.maalmodels

import com.dukaancalculator.ui.models.commonmodels.ParentModel


data class NewMaalModel(
    override var key: String="",
    var supplierName:String="",
    var supplierPhoneNumber:String="",
    var uddharOrDiscountAmount:String="",
    var uddharOrDiscount:String="",
    var timeStamp:Long=0
): ParentModel(key)