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
import com.example.androidclient.R
import com.example.androidclient.common.USER_NAME
import com.example.androidclient.common.getSPString
import com.example.androidclient.login.LoginActivity
import com.example.androidclient.url_setting.UrlSettingActivity
import kotlinx.android.synthetic.main.fragment_sync.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SyncFragment : Fragment() {
    private lateinit var textView: TextView

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

        textView = view.textLoginStatus
        val text = getSPString(context!!, USER_NAME)
        updateTextView(text)
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
    fun updateTextView(text: String) {
        if (text.isEmpty()) {
            textView.text = "未登陆"
        }
        textView.text = text
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}