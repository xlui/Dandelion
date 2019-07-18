package com.example.androidclient

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun checkPermissions(): Boolean {
        val requestList = ArrayList<String>()
        for (permission in permissions) {
            val check = ContextCompat.checkSelfPermission(this, permission)
            if (check == PackageManager.PERMISSION_DENIED) {
                requestList.add(permission)
            }
        }
        if (requestList.isEmpty()) {
            return true
        }
        requestPermissions(permissions.toTypedArray(), 101)
        return false
    }

    private fun readLocalContacts(): List<Person> {
        val cursor = contentResolver.query(
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
}
