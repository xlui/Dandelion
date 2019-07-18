package com.example.androidclient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val showStateLiveData = MutableLiveData<String>()


}