package com.example.androidclient.common

import android.content.Context
import android.content.SharedPreferences

fun saveSPString(context: Context, key: String, value: String) {
    val sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE)
    sharedPreferences.edit().putString(key,value).apply()
}

fun getSPString(context: Context,key: String): String {
    val sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE)
    return sharedPreferences.getString(key,"")!!
}