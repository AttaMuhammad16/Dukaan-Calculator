package com.dukaancalculator.ui.models.maalmodels

import com.dukaancalculator.ui.models.commonmodels.ParentModel

data class MaalBankDetailsModel(
    override var key: String="",
    var amount:String="",
    var accountType:String="",
    var payedVia:String="",
    var date:String=""
):ParentModel(key)