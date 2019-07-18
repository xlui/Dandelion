package com.example.androidclient

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_content.*

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initViews()

        if (checkPermissions()) {
            getMainViewModel().mainStateLiveData.value = SHOW_CONTENT
        } else {
            getMainViewModel().mainStateLiveData.value = REQUEST_PERMISSIONS
            requestPermissions(permissions.toTypedArray(), 101)
        }
    }

    private fun initViews() {
        bottomTab.setOnNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.item_local_contact -> {
                    viewPager.setCurrentItem(0, true)
                }
                R.id.item_cloud_contact -> {
                    viewPager.setCurrentItem(1, true)
                }
                R.id.item_sync -> {
                    viewPager.setCurrentItem(2, true)
                }
            }
            true
        }
    }

    private fun initViewModel() {
        getMainViewModel().mainStateLiveData.observe(this, Observer {
            if (it == REQUEST_PERMISSIONS) {
                textView.text = "Welcome"
                textView.visibility = View.VISIBLE
                viewPager.visibility = View.GONE
                bottomTab.visibility = View.GONE
            } else if (it == SHOW_CONTENT) {
                textView.visibility = View.GONE
                viewPager.visibility = View.VISIBLE
                bottomTab.visibility = View.VISIBLE
            }
        })
    }

    private fun getMainViewModel(): MainViewModel {
        return ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private fun checkPermissions(): Boolean {
        for (permission in permissions) {
            val check = ContextCompat.checkSelfPermission(this, permission)
            if (check == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
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
