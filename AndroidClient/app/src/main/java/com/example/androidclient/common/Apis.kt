package com.example.androidclient.common

import com.example.androidclient.entity.LoginResponse
import com.example.androidclient.entity.RegisterResponse
import com.example.androidclient.entity.User
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Service {
    @POST("/register")
    fun register(@Body user: User): Call<RegisterResponse>

    @POST("/login")
    fun login(@Body user: User):Call<LoginResponse>
}