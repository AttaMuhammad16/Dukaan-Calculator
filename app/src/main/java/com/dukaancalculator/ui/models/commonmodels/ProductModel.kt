package com.dukaancalculator.ui.models.commonmodels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ProductModel(
    var productKey:String="",
    var productName:String="",
    var productPrice:String="",
    var productQuantity:String="",
    var total:String="",
) : Parcelable