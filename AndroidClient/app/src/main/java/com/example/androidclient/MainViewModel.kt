package com.example.androidclient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val mainStateLiveData = MutableLiveData<String>()

    init {
        mainStateLiveData.value = REQUEST_PERMISSIONS
    }

    fun getMainState(): String = mainStateLiveData.value!!
}