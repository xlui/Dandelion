package com.example.androidclient.common

import com.example.androidclient.entity.ContactsEntity
import com.example.androidclient.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

suspend fun register(userName: String, password: String): Boolean = withContext(Dispatchers.IO) {
    val service = getRetrofitService(Service::class.java)
    val call = service.register(User(userName = userName, password = password))
    val registerResponse = call.execute().body()

    if (registerResponse != null) {
        return@withContext (registerResponse.error != null)
    }
    false
}

suspend fun login(userName: String, password: String): String = withContext(Dispatchers.IO) {
    val service = getRetrofitService(Service::class.java)
    val call = service.login(User(userName = userName, password = password))
    val loginResponse = call.execute().body()
    if (loginResponse != null) {
        return@withContext loginResponse.token
    }
    ""
}

suspend fun pull(headerMap: Map<String, String>): ContactsEntity = withContext(Dispatchers.IO) {
    val service = getRetrofitService(Service::class.java)
    val call = service.pull(headerMap)
    call.execute().body() ?: ContactsEntity()
}

suspend fun push(headerMap: Map<String, String>, contactsEntity: ContactsEntity): Boolean =
    withContext(Dispatchers.IO) {
        val service = getRetrofitService(Service::class.java)
        val call = service.push(headerMap, contactsEntity)
        call.execute().isSuccessful
    }

fun <T> getRetrofitService(clazz: Class<T>): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(clazz)
}