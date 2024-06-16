package com.dukaancalculator.id.repositoriesmodule

import com.dukaancalculator.data.authrepository.AuthRepositoryLocal
import com.dukaancalculator.data.authrepository.AuthRepositoryLocalImplLocal
import com.dukaancalculator.data.mainrepository.MainRepositoryLocal
import com.dukaancalculator.data.mainrepository.MainRepositoryLocalImpl
import com.dukaancalculator.ui.models.kharcha.KharchaModel
import com.dukaancalculator.ui.viewmodel.KharchaViewModel
import com.dukaancalculator.ui.viewmodel.MaalViewModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.dukaancalculator.ui.viewmodel.SaleViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideMainRepo(databaseReference: DatabaseReference,storageReference: StorageReference):MainRepositoryLocal{
        return MainRepositoryLocalImpl(databaseReference,storageReference)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth):AuthRepositoryLocal{
        return AuthRepositoryLocalImplLocal(auth)
    }



    @Provides
    @Singleton
    fun provideMainViewModel(mainRepositoryLocal: MainRepositoryLocal,authRepositoryLocal: AuthRepositoryLocal):MainViewModel{
        return MainViewModel(mainRepositoryLocal,authRepositoryLocal)
    }


    @Provides
    @Singleton
    fun provideSaleViewModel(mainRepositoryLocal: MainRepositoryLocal):SaleViewModel{
        return SaleViewModel(mainRepositoryLocal)
    }


    @Provides
    @Singleton
    fun provideMaalViewModel(mainRepositoryLocal: MainRepositoryLocal):MaalViewModel{
        return MaalViewModel(mainRepositoryLocal)
    }

    @Provides
    @Singleton
    fun provideKharchaViewModel(mainRepositoryLocal: MainRepositoryLocal):KharchaViewModel{
        return KharchaViewModel(mainRepositoryLocal)
    }




}