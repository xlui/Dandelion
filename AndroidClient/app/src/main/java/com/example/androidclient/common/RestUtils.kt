package com.example.androidclient.common

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun <T> getRetrofitService(clazz: Class<T>): T {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    return retrofit.create(clazz)
}