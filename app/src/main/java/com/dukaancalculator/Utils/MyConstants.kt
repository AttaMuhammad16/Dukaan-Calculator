package com.dukaancalculator.Utils

import com.google.firebase.auth.FirebaseAuth

object MyConstants {

    const val attaMuhammadNumber="+923034805685"


    // sale module paths
    fun getNewSalePath(auth:FirebaseAuth):String{
       return "Sale/NewSale/${auth.currentUser!!.uid}/"
    }

    fun getSaleBankDetailsPath(auth:FirebaseAuth):String{
        return "Sale/SaleBankDetails/${auth.currentUser!!.uid}/"
    }

    fun getSaleReportPath(auth:FirebaseAuth):String{
        return "Sale/SaleReport/${auth.currentUser!!.uid}/"
    }




    // maal module paths
    fun getNewMaalPath(auth:FirebaseAuth):String{
        return "Maal/NewMaal/${auth.currentUser!!.uid}/"
    }

    fun getMaalBankDetailsPath(auth:FirebaseAuth):String{
        return "Maal/MaalBankDetails/${auth.currentUser!!.uid}/"
    }
    fun getMaalReportPath(auth:FirebaseAuth):String{
        return "Maal/MaalReport/${auth.currentUser!!.uid}/"
    }




    // kharcha module
    fun getKharchaPath(auth:FirebaseAuth):String{
        return "Kharcha/${auth.currentUser!!.uid}/"
    }



}