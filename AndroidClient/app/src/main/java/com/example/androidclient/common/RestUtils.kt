package com.example.androidclient.common

import com.example.androidclient.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun register(baseUrl: String, userName: String, password: String) =
    withContext(Dispatchers.IO) {
        val service = getRetrofitService(baseUrl, Service::class.java)
        val call = service.register(User(userName = userName, password = password))
        val registerResponse = call.execute().body()

        if (registerResponse != null) {
            return@withContext (registerResponse.error != null)
        }
        false
    }

suspend fun login(baseUrl: String, userName: String, password: String) =
    withContext(Dispatchers.IO) {
        val service = getRetrofitService(baseUrl, Service::class.java)
        val call = service.login(User(userName = userName, password = password))
        val loginResponse = call.execute().body()
        if (loginResponse != null) {
            return@withContext loginResponse.token
        }
        ""
    }

fun <T> getRetrofitService(baseUrl: String, clazz: Class<T>): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(clazz)
}