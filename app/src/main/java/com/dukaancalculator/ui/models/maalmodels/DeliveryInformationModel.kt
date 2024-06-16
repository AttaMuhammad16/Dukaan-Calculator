package com.dukaancalculator.ui.models.maalmodels

import android.os.Parcelable
import com.dukaancalculator.ui.models.commonmodels.ParentModel
import kotlinx.parcelize.Parcelize


@Parcelize
data class DeliveryInformationModel(
    override var key: String="",
    var distributorCompanyName:String="",
    var supplierName:String="",
    var supplierNumber:String="",
    var imageUrl:String=""
): ParentModel(key), Parcelable