package com.dukaancalculator.data.authrepository

import com.dukaancalculator.Utils.MyResult

interface AuthRepositoryLocal {

    suspend fun registerUserWithEmailPassword(email:String,password:String): MyResult<String>
    suspend fun loginUserWithEmailPassword(email:String, password:String): MyResult<String>


}