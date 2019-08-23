package com.example.androidclient.main

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidclient.common.*
import com.example.androidclient.entity.Person

class MainViewModel : ViewModel() {
    val mainStateLiveData = MutableLiveData<String>()
    val userName = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val token = MutableLiveData<String>()
    val baseurl = MutableLiveData<String>()

    init {
        mainStateLiveData.value = REQUEST_PERMISSIONS
    }

    fun initSpValue(context: Context) {
        userName.value = getSPString(context, USER_NAME)
        password.value = getSPString(context, PASSWORD)
        token.value = getSPString(context, TOKEN)
        baseurl.value = getSPString(context, BASE_URL)
    }

    fun readLocalContacts(context: Context): List<Person> {
        val cursor = context.contentResolver?.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        if (cursor != null) {
            val result = ArrayList<Person>()
            while (cursor.moveToNext()) {
                // 获取联系人姓名
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                // 获取联系人手机号
                val number =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                result.add(Person(name, number))
            }
            cursor.close()
            return result
        }

        return emptyList()
    }

    fun getToken(): String = token.value ?: ""

    fun getUserName(): String = userName.value ?: ""

    fun getBaseUrl(): String = baseurl.value ?: ""
}