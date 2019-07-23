package com.example.androidclient.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidclient.common.REQUEST_PERMISSIONS

class MainViewModel : ViewModel() {
    val mainStateLiveData = MutableLiveData<String>()

    init {
        mainStateLiveData.value = REQUEST_PERMISSIONS
    }

    fun getMainState(): String = mainStateLiveData.value!!
}