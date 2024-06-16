package com.dukaancalculator.ui.models.salemodels

import com.dukaancalculator.ui.models.commonmodels.ParentModel


data class NewSaleModel(
    override var key: String="",
    var customerName:String="",
    var customerPhoneNumber:String="",
    var uddharOrDiscountAmount:String="",
    var uddharOrDiscount:String="",
    var timeStamp:Long=0
): ParentModel(key)

