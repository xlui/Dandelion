package com.example.androidclient.main

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.androidclient.R
import com.example.androidclient.event.UserNameEvent
import com.example.androidclient.login.LoginActivity
import com.example.androidclient.url_setting.UrlSettingActivity
import kotlinx.android.synthetic.main.fragment_sync.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SyncFragment : Fragment() {
    private val mainViewModel by lazy {
        ViewModelProviders.of(context as MainActivity).get(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sync, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //跳转设置baseUrl
        view.layoutUrl.setOnClickListener {
            startActivity(Intent(context, UrlSettingActivity::class.java))
        }

        //pull
        view.layoutPull.setOnClickListener {
            showTextDialog(
                "合并云端数据到本地",
                "合并云端的数据到本地，本地已存在的联系人将不做改变，不存在的联系人会创建新的，同名联系人但是电话不同创建新的，确定这样做吗？"
            ) { _, _ -> pullData() }
        }

        //push
        view.layoutPush.setOnClickListener {
            showTextDialog(
                "上传本地数据到云端",
                "上传本地联系人到云端，本地数据不会发生改变，确定这样做吗？"
            ) { _, _ -> pushData() }
        }

        //跳转登陆
        view.layoutLogin.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }

        mainViewModel.userName.observe(context as MainActivity, Observer { text ->
            if (text.isEmpty()) {
                view.textLoginStatus.text = "未登陆"
                return@Observer
            }
            view.textLoginStatus.text = text
        })
        mainViewModel.userName.value = mainViewModel.getUserName()
    }

    private fun pushData() {
        val dialog = createProgressDialog()
        dialog.cancel()
    }

    private fun pullData() {
        val dialog = createProgressDialog()
        dialog.cancel()
    }

    private fun showTextDialog(
        title: String,
        content: String,
        callBack: (DialogInterface, Int) -> Unit
    ) {
        val textView = TextView(context)
        textView.text = content
        AlertDialog
            .Builder(context!!)
            .setTitle(title)
            .setMessage(content)
            .setNegativeButton("取消") { _, _ -> }
            .setPositiveButton("确定", callBack)
            .create()
            .show()
    }

    private fun createProgressDialog(): AlertDialog {
        val progressBar = ProgressBar(context)
        progressBar.setPadding(0, 120, 0, 120)
        val dialog = AlertDialog
            .Builder(context!!)
            .setTitle("等待中")
            .setCancelable(false)
            .setView(progressBar)
            .create()
        dialog.show()
        return dialog
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateTextView(event:UserNameEvent) {
        mainViewModel.userName.value = event.userName
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}