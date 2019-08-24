package com.example.androidclient.url_setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.androidclient.R
import com.example.androidclient.common.*
import kotlinx.android.synthetic.main.activity_url_setting.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class UrlSettingActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url_setting)

        initViews()
    }

    private fun initViews() {
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        listHistory.adapter = adapter
        listHistory.setOnItemClickListener { _, _, i, _ ->
            val s = adapter.getItem(i) ?: ""
            updateInputText(s, over = true)
        }
        launch {
            reloadHistoryListView()
        }

        chipCom.setOnClickListener {
            updateInputText(".com")
        }
        chipWww.setOnClickListener {
            updateInputText("www.")
        }
        chipHttp.setOnClickListener {
            updateInputText("http://")
        }
        chipHttps.setOnClickListener {
            updateInputText("https://")
        }
        buttonSave.setOnClickListener {
            var url = (textInput.editText?.text?.toString() ?: "")
            if (url.isEmpty()) {
                Toast.makeText(this, "输入为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!url.endsWith("/")) {
                url = "$url/"
            }
            saveSPString(this, BASE_URL, url)
            baseUrl = url

            launch(Dispatchers.Main) {
                fileAppendString(this@UrlSettingActivity, HISTORY_FILE, url)
                reloadHistoryListView()
            }
        }
    }

    private fun updateInputText(text: String, over: Boolean = false) {
        if (over) {
            textInput.editText?.setText(text)
            return
        }
        val content = (textInput.editText?.text?.toString() ?: "") + text
        textInput.editText?.setText(content)
    }

    private suspend fun reloadHistoryListView() {
        val history = fileReadHistoryList(this, HISTORY_FILE)
        adapter.clear()
        adapter.addAll(history)
        adapter.notifyDataSetChanged()
    }
}
