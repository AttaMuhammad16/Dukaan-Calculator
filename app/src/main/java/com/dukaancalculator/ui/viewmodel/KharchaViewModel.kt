package com.dukaancalculator.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dukaancalculator.data.mainrepository.MainRepositoryLocal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class KharchaViewModel @Inject constructor(private val mainRepositoryLocal: MainRepositoryLocal):ViewModel()  {

}