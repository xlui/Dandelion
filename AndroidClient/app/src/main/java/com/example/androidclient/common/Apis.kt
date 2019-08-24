package com.example.androidclient.common

import com.example.androidclient.entity.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

var baseUrl = ""


interface Service {
    @POST("/register")
    fun register(@Body user: User): Call<RegisterEntity>

    @POST("/login")
    fun login(@Body user: User): Call<LoginEntity>

    @POST("/push")
    fun push(@HeaderMap header: Map<String, String>, @Body body: PushEntity): Call<ResponseBody>

    @GET("/pull")
    fun pull(@HeaderMap header: Map<String, String>): Call<PullEntity>
}