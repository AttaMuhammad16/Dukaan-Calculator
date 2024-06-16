package com.dukaancalculator.id.firebasemodule

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



//@Module
//@InstallIn(SingletonComponent::class)
//object FirebaseModule {
//
//    @Provides
//    @Singleton
//    fun provideFirebaseDataBase(): DatabaseReference {
//        return FirebaseDatabase.getInstance().reference
//    }
//
//    @Provides
//    @Singleton
//    fun provideFirebaseStorageRef(): StorageReference {
//        return FirebaseStorage.getInstance().reference
//    }
//
//    @Provides
//    @Singleton
//    fun provideFirebaseStorage(): FirebaseStorage {
//        return FirebaseStorage.getInstance()
//    }
//
//    @Provides
//    @Singleton
//    fun provideFirebaseAuth(): FirebaseAuth {
//        return FirebaseAuth.getInstance()
//    }
//
//}