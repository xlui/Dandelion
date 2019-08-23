package com.example.androidclient.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.androidclient.R
import com.example.androidclient.common.REQUEST_PERMISSIONS
import com.example.androidclient.common.SHOW_CONTENT
import com.example.androidclient.common.permissions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViewModel()
        initViews()

        if (checkPermissions()) {
            getMainViewModel().mainStateLiveData.value =
                SHOW_CONTENT
        } else {
            getMainViewModel().mainStateLiveData.value =
                REQUEST_PERMISSIONS
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


        val localFragment = LocalListFragment.newInstance()
        val cloudyFragment = CloudyListFragment.newInstance()
        val syncFragment = SyncFragment()
        val pageAdapter = MainViewPageAdapter(supportFragmentManager)
        pageAdapter.setFragments(arrayListOf(localFragment, cloudyFragment, syncFragment))
        viewPager.offscreenPageLimit = 0
        viewPager.adapter = pageAdapter
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()) {
            grantResults.forEach {
                if (it != PackageManager.PERMISSION_GRANTED) {
                    return
                }
            }
            getMainViewModel().mainStateLiveData.value =
                SHOW_CONTENT
        } else {
            return
        }
    }
}
