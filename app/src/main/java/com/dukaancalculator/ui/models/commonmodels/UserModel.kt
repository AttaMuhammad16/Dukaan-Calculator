package com.dukaancalculator.ui.models.commonmodels

import com.dukaancalculator.ui.models.commonmodels.ParentModel

data class UserModel(
    override var key: String="",
    var email:String="",
    var userName:String="",
    var phoneNumber:String="",
    var role:String="",
    var password:String=""
): ParentModel(key)