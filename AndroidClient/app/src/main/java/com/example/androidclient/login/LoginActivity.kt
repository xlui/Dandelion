package com.example.androidclient.login

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.androidclient.R
import com.example.androidclient.common.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.EventBus


class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
    }

    private fun initView() {
        fillInfo()

        buttonLogin.setOnClickListener {
            val userName = inputUsername.editText?.toString()
            val password = inputPassword.editText?.toString()
            if (userName == null || password == null) {
                return@setOnClickListener
            }
            callLogin(userName, password)
        }

        textRegister.setOnClickListener {
            val userName = inputUsername.editText?.toString()
            val password = inputPassword.editText?.toString()
            if (userName == null || password == null) {
                return@setOnClickListener
            }
            launch(Dispatchers.Main) {
                val registerResult = register(userName, password)
                if (!registerResult) {
                    Toast.makeText(this@LoginActivity, "注册失败了", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, "注册成功，尝试使用此账号登陆", Toast.LENGTH_SHORT).show()
                    callLogin(userName, password)
                }
            }
        }
    }

    private fun fillInfo() {
        val userName = getSPString(this, USER_NAME)
        val password = getSPString(this, PASSWORD)
        if (userName.isNotEmpty() && password.isNotEmpty()) {
            inputUsername.editText?.setText(userName)
            inputPassword.editText?.setText(password)
        }
    }

    private fun callLogin(userName: String, password: String) = launch(Dispatchers.Main) {
        textRegister.isClickable = false
        buttonLogin.isCheckable = false

        val token = login(userName, password)
        if (token.isEmpty()) {
            Toast.makeText(this@LoginActivity, "登陆失败了", Toast.LENGTH_SHORT).show()
            return@launch
        }
        // 去往 SyncFragment.updateTextView
        EventBus.getDefault().post(userName)
        saveSPString(this@LoginActivity, TOKEN, token)
        saveSPString(this@LoginActivity, USER_NAME, userName)
        saveSPString(this@LoginActivity, PASSWORD, password)

        textRegister.isClickable = true
        buttonLogin.isCheckable = true
        finish()
    }
}
