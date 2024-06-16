package com.dukaancalculator.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dukaancalculator.Utils.MyResult
import com.dukaancalculator.data.authrepository.AuthRepositoryLocal
import com.dukaancalculator.data.mainrepository.MainRepositoryLocal
import com.dukaancalculator.ui.models.salemodels.BankDetailsModel
import com.dukaancalculator.ui.models.commonmodels.ParentModel
import com.dukaancalculator.ui.models.kharcha.KharchaModel
import com.dukaancalculator.ui.models.salemodels.NewSaleModel
import com.dukaancalculator.ui.models.maalmodels.MaalBankDetailsModel
import com.dukaancalculator.ui.models.maalmodels.MaalReportModel
import com.dukaancalculator.ui.models.maalmodels.NewMaalModel
import com.dukaancalculator.ui.models.salemodels.SaleReportModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepositoryLocal: MainRepositoryLocal,private val authRepositoryLocal: AuthRepositoryLocal) :ViewModel() {


    //Sale RemainingPayments Flow or  Sale Udhhar
    private val _newSaleFlow = MutableStateFlow<List<NewSaleModel>>(emptyList())
    val newSaleFlow= _newSaleFlow.asStateFlow()


    //Maal RemainingPayments Flow or  Maal Udhhar
    private val _MaalRemainingPaymentFlow = MutableStateFlow<List<NewMaalModel>>(emptyList())
    val MaalRemainingPaymentFlow= _MaalRemainingPaymentFlow.asStateFlow()



    //sale bankDetails Flow
    private val _bankDetailsFlow = MutableStateFlow<List<BankDetailsModel>>(emptyList())
    val bankDetailsFlow= _bankDetailsFlow.asStateFlow()


    //maal bankDetails Flow
    private val _maalBankDetailFlow = MutableStateFlow<List<MaalBankDetailsModel>>(emptyList())
    val maalBankDetailFlow= _maalBankDetailFlow.asStateFlow()



    // kharcha kharcha flow

    private val _kharchaFlow = MutableStateFlow<List<KharchaModel>>(emptyList())
    val kharchaFlow= _kharchaFlow.asStateFlow()



    // sale report flow

    private val _saleReportFlow = MutableStateFlow<List<SaleReportModel>>(emptyList())
    val saleReportFlow= _saleReportFlow.asStateFlow()

    // maal report flow

    private val _maalReportFlow = MutableStateFlow<List<MaalReportModel>>(emptyList())
    val maalReportFlow= _maalReportFlow.asStateFlow()



    // data base
    suspend fun uploadAnyModel(child: String, model: ParentModel): MyResult<String>{
        return withContext(Dispatchers.IO){mainRepositoryLocal.uploadAnyModel(child,model)}
    }

    fun <T> collectAnyModel(child: String, model: Class<T>){
        viewModelScope.launch {
            mainRepositoryLocal.collectAnyModel(child,model).collect{
                when(model){

                    NewSaleModel::class.java -> {
                        _newSaleFlow.value = it as List<NewSaleModel>
                    }

                    NewMaalModel::class.java -> {
                        _MaalRemainingPaymentFlow.value = it as List<NewMaalModel>
                    }

                    BankDetailsModel::class.java->{
                        _bankDetailsFlow.value=it as List<BankDetailsModel>
                    }

                    MaalBankDetailsModel::class.java->{
                        _maalBankDetailFlow.value=it as List<MaalBankDetailsModel>
                    }

                    KharchaModel::class.java->{
                        _kharchaFlow.value=it as List<KharchaModel>
                    }

                    SaleReportModel::class.java->{
                        _saleReportFlow.value=it as List<SaleReportModel>
                    }

                    MaalReportModel::class.java->{
                        _maalReportFlow.value=it as List<MaalReportModel>
                    }

                }
            }
        }
    }


    suspend fun <T> getAnyModel(path: String, clazz: Class<T>): T?{
        return withContext(Dispatchers.IO){mainRepositoryLocal.getAnyModel(path, clazz)}
    }

    suspend fun getCustomerNames(child: String): Flow<List<String>> {
        return withContext(Dispatchers.IO){mainRepositoryLocal.getCustomerNames(child)}
    }

    suspend fun getSupplierName(child: String): Flow<List<String>>{
        return withContext(Dispatchers.IO){mainRepositoryLocal.getCustomerNames(child)}
    }





    suspend fun getSaleUddhar(path: String): List<NewSaleModel>{
        return withContext(Dispatchers.IO){mainRepositoryLocal.getSaleUddhar(path)}
    }


    suspend fun getMaalUddhar(path: String): List<NewMaalModel>{
        return withContext(Dispatchers.IO){mainRepositoryLocal.getMaalUddhar(path)}
    }


    suspend fun getTotalSaleUddhar(path: String): Int{
        return withContext(Dispatchers.IO){mainRepositoryLocal.getTotalSaleUddhar(path)}
    }


    suspend fun getTotalMaalUddhar(path: String): Int{
        return withContext(Dispatchers.IO){mainRepositoryLocal.getTotalMaalUddhar(path)}
    }



    // auth
    suspend fun loginUserWithEmailPassword(email: String, password: String): MyResult<String>{
        return withContext(Dispatchers.IO){authRepositoryLocal.loginUserWithEmailPassword(email, password)}
    }

    suspend fun registerUserWithEmailPassword(email: String, password: String): MyResult<String>{
        return withContext(Dispatchers.IO){authRepositoryLocal.registerUserWithEmailPassword(email, password)}
    }




    // storage
    suspend fun uploadImageToFirebaseStorageWithUri(uri: Uri, name: String): MyResult<String>{
        return mainRepositoryLocal.uploadImageToFirebaseStorageWithUri(uri, name)
    }




}