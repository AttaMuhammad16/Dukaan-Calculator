package com.dukaancalculator.data.mainrepository

import android.net.Uri
import com.dukaancalculator.Utils.MyResult
import com.dukaancalculator.Utils.MyUtils.shrink
import com.dukaancalculator.ui.models.commonmodels.ParentModel
import com.dukaancalculator.ui.models.maalmodels.NewMaalModel
import com.dukaancalculator.ui.models.salemodels.NewSaleModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.lymors.lycommons.utils.MyExtensions.toIntOrDefault
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject




class MainRepositoryLocalImpl @Inject constructor(private val databaseReference: DatabaseReference,private val storageReference: StorageReference): MainRepositoryLocal {


    override fun <T> collectAnyModel(path: String, clazz: Class<T>): Flow<List<T>> = callbackFlow {
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val modelList = mutableListOf<T>()
                for (childSnapshot in dataSnapshot.children) {
                    val message = childSnapshot.getValue(clazz)
                    message?.let {
                        modelList.add(it)
                    }
                }
                trySend(modelList as List<T>).isSuccess
            }
            override fun onCancelled(databaseError: DatabaseError) {
                close(databaseError.toException())
            }
        }
        databaseReference.child(path).addValueEventListener(valueEventListener)
        awaitClose {
            databaseReference.child(path).removeEventListener(valueEventListener)
        }
    }


    override suspend fun <T> getAnyModel(path: String, clazz: Class<T>): T? {
        return try {
            val snap = databaseReference.child(path).get().await()
            snap.getValue(clazz)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    override suspend fun uploadAnyModel(child: String, model: ParentModel): MyResult<String> {
        return try {
            val key = model.key.takeIf { it.isNotEmpty() } ?: databaseReference.push().key.toString()
            model.key = key
            databaseReference.child(child).setValue(model.shrink()).await()
            MyResult.Success("Success")
        } catch (e: Exception) {
            MyResult.Error(e.message.toString())
        }
    }


   override suspend fun getCustomerNames(child: String): Flow<List<String>> = flow {
        val newSaleRef = databaseReference.child(child)
        val dataSnapshot = newSaleRef.get().await()
        val names = mutableListOf<String>()

        for (saleSnapshot in dataSnapshot.children) {
            saleSnapshot.key?.let {
                names.add(it)
            }
        }
        emit(names)
    }

    override suspend fun getTotalSaleUddhar(path: String): Int {
        val newSaleRef = databaseReference.child(path)
        val dataSnapshot = newSaleRef.get().await()
        var totalUddharAmount = 0
        for (saleSnapshot in dataSnapshot.children) {
            for (sale in saleSnapshot.children){
                val newSaleModel = sale.getValue(NewSaleModel::class.java)
                if (newSaleModel?.uddharOrDiscount == "Uddhar") {
                    totalUddharAmount += newSaleModel.uddharOrDiscountAmount.toIntOrDefault(0)
                }
            }
        }
        return totalUddharAmount
    }

    override suspend fun getTotalMaalUddhar(path: String): Int{
        val newMaalRef = databaseReference.child(path)
        val dataSnapshot = newMaalRef.get().await()
        var totalMaalAmount = 0
        for (maalSnapshot in dataSnapshot.children) {
            for (sale in maalSnapshot.children){
                val newSaleModel = sale.getValue(NewMaalModel::class.java)
                if (newSaleModel?.uddharOrDiscount == "Uddhar") {
                    totalMaalAmount += newSaleModel.uddharOrDiscountAmount.toIntOrDefault(0)
                }
            }
        }
        return totalMaalAmount
    }


    override suspend fun getSaleUddhar(path: String): List<NewSaleModel> {
        val newSaleRef = databaseReference.child(path)
        val dataSnapshot = newSaleRef.get().await()
        val list = arrayListOf<NewSaleModel>()
        for (saleSnapshot in dataSnapshot.children) {
            for (sale in saleSnapshot.children){
                val newSaleModel = sale.getValue(NewSaleModel::class.java)
                newSaleModel?.apply {
                    list.add(newSaleModel)
                }
            }
        }
        return list
    }


    override suspend fun getMaalUddhar(path: String): List<NewMaalModel> {
        val newMaalRef = databaseReference.child(path)
        val dataSnapshot = newMaalRef.get().await()
        val list = arrayListOf<NewMaalModel>()
        for (maalSnapshot in dataSnapshot.children) {
            for (sale in maalSnapshot.children){
                val newSaleModel = sale.getValue(NewMaalModel::class.java)
                newSaleModel?.apply {
                    list.add(newSaleModel)
                }
            }
        }
        return list
    }





    override suspend fun uploadImageToFirebaseStorageWithUri(uri: Uri, name: String): MyResult<String> {
        return try {
            val filename = name.ifEmpty{ (uri.lastPathSegment) ?: System.currentTimeMillis()}
            val imageRef = storageReference.child("images/${filename}")
            val uploadTask = imageRef.putFile(uri)
            val result: UploadTask.TaskSnapshot = uploadTask.await()
            val downloadUrl = result.storage.downloadUrl.await()
            MyResult.Success(downloadUrl.toString())
        } catch (e: Exception) {
            MyResult.Error(e.message ?: "Unknown error occurred")
        }
    }


}