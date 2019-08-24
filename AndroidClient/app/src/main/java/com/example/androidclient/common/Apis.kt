package com.example.androidclient.common

import com.example.androidclient.entity.ContactsEntity
import com.example.androidclient.entity.LoginEntity
import com.example.androidclient.entity.RegisterEntity
import com.example.androidclient.entity.User
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

var baseUrl = ""


interface Service {
    @POST("/register")
    fun register(@Body user: User): Call<RegisterEntity>

    @POST("/login")
    fun login(@Body user: User): Call<LoginEntity>

    @POST("/push")
    fun push(@HeaderMap header: Map<String, String>, @Body json: ContactsEntity): Call<ResponseBody>

    @GET("/pull")
    fun pull(@HeaderMap header: Map<String, String>): Call<ContactsEntity>
}