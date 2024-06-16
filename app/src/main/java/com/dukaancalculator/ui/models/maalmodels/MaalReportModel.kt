package com.dukaancalculator.ui.models.maalmodels

import android.os.Parcelable
import com.dukaancalculator.ui.models.commonmodels.ParentModel
import com.dukaancalculator.ui.models.commonmodels.ProductModel
import kotlinx.parcelize.Parcelize


@Parcelize
data class MaalReportModel(
    override var key: String="",
    var receiptNumber:String="",
    var date:String="",
    var maalDeliveryInformationModel:DeliveryInformationModel= DeliveryInformationModel(),
    var listOfProductModel: ArrayList<ProductModel> = arrayListOf(),
    var uddharOrDiscount:String="",
    var uddharOrDiscountAmount:String="",
    var grandTotal:String="",
):ParentModel(key), Parcelable