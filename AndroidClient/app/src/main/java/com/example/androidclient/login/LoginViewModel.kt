package com.example.androidclient.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

const val LOGIN_STATUS_LOADING = "loading"

class LoginViewModel : ViewModel() {
    val loginStatus = MutableLiveData<String>()

    init {
        loginStatus.value = LOGIN_STATUS_LOADING
    }
}