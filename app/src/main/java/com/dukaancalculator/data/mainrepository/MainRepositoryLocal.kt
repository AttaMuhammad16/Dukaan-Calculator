package com.dukaancalculator.data.mainrepository

import android.net.Uri
import com.dukaancalculator.Utils.MyResult
import com.dukaancalculator.ui.models.commonmodels.ParentModel
import com.dukaancalculator.ui.models.maalmodels.NewMaalModel
import com.dukaancalculator.ui.models.salemodels.NewSaleModel
import kotlinx.coroutines.flow.Flow

interface MainRepositoryLocal {

    suspend fun <T> getAnyModel(path: String, clazz: Class<T>): T?
    fun <T>  collectAnyModel(path:String, clazz: Class<T>): Flow<List<T>>
    suspend fun uploadAnyModel(child: String, model: ParentModel): MyResult<String>
    suspend fun getCustomerNames(child: String):Flow<List<String>>


    suspend fun getTotalSaleUddhar(path:String):Int
    suspend fun getTotalMaalUddhar(path:String):Int




    suspend fun getSaleUddhar(path:String):List<NewSaleModel>
    suspend fun getMaalUddhar(path:String):List<NewMaalModel>







    suspend fun uploadImageToFirebaseStorageWithUri(uri: Uri, name: String): MyResult<String>

}