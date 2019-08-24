package com.example.androidclient.entity

import com.google.gson.annotations.SerializedName

data class LoginEntity(@SerializedName("access_token") var token: String?)