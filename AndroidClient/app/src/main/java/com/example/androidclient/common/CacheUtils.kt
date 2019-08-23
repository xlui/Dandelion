package com.example.androidclient.common

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.*


fun saveSPString(context: Context, key: String, value: String) {
    val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    sharedPreferences.edit().putString(key, value).apply()
}

fun getSPString(context: Context, key: String): String {
    val sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getString(key, "")?:""
}

suspend fun fileAppendString(context: Context, fileName: String, content: String) =
    withContext(Dispatchers.IO) {
        val bufferedWriter =
            BufferedWriter(
                OutputStreamWriter(
                    context.openFileOutput(
                        fileName,
                        Context.MODE_APPEND
                    )
                )
            )
        bufferedWriter.write(content)
        bufferedWriter.newLine()
        bufferedWriter.close()
    }

suspend fun fileReadHistoryList(context: Context, fileName: String): List<String> =
    withContext(Dispatchers.IO) {
        val result = ArrayList<String>()
        val historyFile = File(context.filesDir, fileName)
        if (!historyFile.exists()) {
            return@withContext result
        }
        val buffedReader = BufferedReader(InputStreamReader(context.openFileInput(fileName)))
        buffedReader.forEachLine {
            result.add(it)
        }
        result
    }