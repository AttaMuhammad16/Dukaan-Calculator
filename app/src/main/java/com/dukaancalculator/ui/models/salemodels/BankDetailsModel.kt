package com.dukaancalculator.ui.models.salemodels

import com.dukaancalculator.ui.models.commonmodels.ParentModel

data class BankDetailsModel(
    override var key: String="",
    var accountTitle:String="",
    var ibanNumber:String="",
    var accountNumber:String=""
): ParentModel(key)