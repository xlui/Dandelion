package com.example.androidclient.main

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidclient.common.*
import com.example.androidclient.entity.ContactsEntity
import com.example.androidclient.entity.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainViewModel : ViewModel() {
    val mainStateLiveData = MutableLiveData<String>()
    val userName = MutableLiveData<String>()
    val token = MutableLiveData<String>()
    val localContacts = MutableLiveData<MutableList<Person>>()

    init {
        mainStateLiveData.value = REQUEST_PERMISSIONS
    }

    fun initSpValue(context: Context) {
        userName.value = getSPString(context, USER_NAME)
        token.value = getSPString(context, TOKEN)
        baseUrl = getSPString(context, BASE_URL)
        println()
    }

    fun readLocalContacts(context: Context) {
        val list: MutableList<Person> = localContacts.value ?: mutableListOf()
        list.clear()
        val cursor = context.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取联系人姓名
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                // 获取联系人手机号
                val number =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                list.add(Person(name, number))
            }
            cursor.close()
        }
        localContacts.value = list
    }

    fun getUserName(): String = userName.value ?: ""

    suspend fun pull(): ContactsEntity = withContext(Dispatchers.IO) {
        val headerMap = mapOf("Authorization" to (token.value ?: ""))
        val service = getRetrofitService(Service::class.java)
        val call = service.pull(headerMap)
        call.execute().body() ?: ContactsEntity()
    }

    suspend fun push(): Boolean =
        withContext(Dispatchers.IO) {
            val headerMap = mapOf("Authorization" to (token.value ?: ""))
            val service = getRetrofitService(Service::class.java)
            val call =
                service.push(headerMap, ContactsEntity(localContacts.value ?: mutableListOf()))
            call.execute().isSuccessful
        }

    suspend fun merge() = withContext(Dispatchers.IO) {
        val contactsEntity = pull()
        val cloudyContacts = contactsEntity.persons
        if (cloudyContacts.isEmpty()) {
            return@withContext
        }

//        localContacts.value =
    }
}