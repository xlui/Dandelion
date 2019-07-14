package com.example.androidclient

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactsViewModel : ViewModel() {
    val showModeLiveData = MutableLiveData<String>()

    init {
        showModeLiveData.value = SHOW_MODE_LOCAL
    }

    fun getShowMode(): String = showModeLiveData.value!!

}