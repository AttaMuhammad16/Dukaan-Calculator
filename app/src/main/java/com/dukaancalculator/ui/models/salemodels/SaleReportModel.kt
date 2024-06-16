package com.dukaancalculator.ui.models.salemodels

import android.os.Parcelable
import com.dukaancalculator.ui.models.commonmodels.ParentModel
import com.dukaancalculator.ui.models.commonmodels.ProductModel
import kotlinx.parcelize.Parcelize


@Parcelize
data class SaleReportModel(
    override var key: String="",
    var receiptNumber: String="",
    var date:String="",
    var productModelList:ArrayList<ProductModel> = ArrayList(),
    var subtotal:String="",
    var uddharOrDiscount:String="",
    var uddharOrDiscountAmount:String="",
    var grandTotal:Int=0
):ParentModel(key), Parcelable