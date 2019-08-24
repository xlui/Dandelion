package com.example.androidclient.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidclient.common.*
import com.example.androidclient.entity.User
import com.example.androidclient.event.TokenEvent
import com.example.androidclient.event.UserNameEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import com.example.androidclient.entity.RegisterEntity
import java.lang.Exception


class LoginViewModel : ViewModel() {
    private val userName = MutableLiveData<String>()
    private val password = MutableLiveData<String>()

    fun initSpVaLue(context: Context) {
        userName.value = getSPString(context, USER_NAME)
        password.value = getSPString(context, PASSWORD)
    }

    suspend fun register(userName: String, password: String): Boolean =
        withContext(Dispatchers.IO) {
            val service = getRetrofitService(Service::class.java)
            val call = service.register(User(userName, password))
            Log.e("xkf", baseUrl)
            val registerEntity = call.execute().body()
            if (registerEntity != null && registerEntity.error == null) {
                return@withContext true
            }
            Log.e("xkf", registerEntity?.error ?: "")
            return@withContext false
        }

    suspend fun login(userName: String, password: String): String = withContext(Dispatchers.IO) {
        val service = getRetrofitService(Service::class.java)
        val call = service.login(User(username = userName, password = password))
        val loginEntity = call.execute().body()
        if (loginEntity != null) {
            return@withContext loginEntity.token ?: ""
        }
        return@withContext ""
    }

    fun getUserName(): String = userName.value ?: ""

    fun getPassword(): String = password.value ?: ""

    fun saveData(context: Context, userName: String, password: String, token: String) {
        // 去往 SyncFragment.onUserNameEvent
        EventBus.getDefault().post(UserNameEvent(userName))
        // 去往 CloudyListFragment.onTokenEvent
        EventBus.getDefault().post(TokenEvent(token))

        saveSPString(context, TOKEN, token)
        saveSPString(context, USER_NAME, userName)
        saveSPString(context, PASSWORD, password)
    }
}