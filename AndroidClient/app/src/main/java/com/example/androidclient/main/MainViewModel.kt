package com.example.androidclient.main

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.ContactsContract
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.RawContacts
import android.provider.ContactsContract.CommonDataKinds.*
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidclient.common.*
import com.example.androidclient.entity.Person
import com.example.androidclient.entity.PersonArray
import com.example.androidclient.entity.PullEntity
import com.example.androidclient.entity.PushEntity
import com.google.gson.Gson
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
        localContacts.postValue(list)
    }

    fun getUserName(): String = userName.value ?: ""

    suspend fun pull(): List<Person> = withContext(Dispatchers.IO) {
        val headerMap = mapOf("Authorization" to ("JWT " + (token.value ?: "")))
        val service = getRetrofitService(Service::class.java)
        val call = service.pull(headerMap)
        val response = call.execute()
        val pullEntity = response.body() ?: PullEntity(data = "", error = "empty")
        val json = pullEntity.data
        val personArray = Gson().fromJson(json, PersonArray::class.java)
        return@withContext personArray.data
    }

    suspend fun push(): Boolean =
        withContext(Dispatchers.IO) {
            Log.e("xkf", token.value ?: "...")
            val headerMap = mapOf("Authorization" to ("JWT " + (token.value ?: "")))
            val service = getRetrofitService(Service::class.java)
            val call =
                service.push(headerMap, PushEntity(localContacts.value ?: mutableListOf()))
            val response = call.execute()
            return@withContext response.isSuccessful
        }

    suspend fun merge(context: Context) = withContext(Dispatchers.IO) {
        val cloudyList = pull()
        if (cloudyList.isEmpty()) {
            return@withContext
        }
        val localList = localContacts.value ?: mutableListOf()
        val localMap = hashMapOf<String, String>()
        localList.forEach { person ->
            localMap[person.name] = person.phoneNumber
        }
        cloudyList.forEach { person ->
            if (person.name in localMap && person.phoneNumber == localMap[person.name]) {
                return@forEach
            }
            appendContacts(context, person.name, person.phoneNumber)
        }
        readLocalContacts(context)
    }

    private fun appendContacts(context: Context, name: String, number: String) {
        val values = ContentValues()
        val rawContactUri = context.contentResolver.insert(RawContacts.CONTENT_URI, values)!!
        val rawContactId = ContentUris.parseId(rawContactUri)

        //写入姓名
        values.clear()
        values.put(Data.RAW_CONTACT_ID, rawContactId)
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
        values.put(StructuredName.GIVEN_NAME, name)
        context.contentResolver.insert(Data.CONTENT_URI, values)

        //写入电话数据
        values.clear()
        values.put(Data.RAW_CONTACT_ID, rawContactId)
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
        values.put(Phone.NUMBER, number)
        values.put(Phone.TYPE, Phone.TYPE_MOBILE)
        context.contentResolver.insert(Data.CONTENT_URI, values)
    }
}