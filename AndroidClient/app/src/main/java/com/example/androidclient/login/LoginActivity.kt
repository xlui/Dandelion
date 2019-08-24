package com.example.androidclient.login

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.androidclient.R
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*


class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val loginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginViewModel.initSpVaLue(this)

        initView()
    }

    private fun initView() {
        fillInfo()

        buttonLogin.setOnClickListener {
            val userName = inputUsername.editText?.text.toString()
            val password = inputPassword.editText?.text.toString()
            callLogin(userName, password)
        }

        textRegister.setOnClickListener {
            val userName = inputUsername.editText?.text.toString()
            val password = inputPassword.editText?.text.toString()
            launch(Dispatchers.Main) {
                val registerResult = loginViewModel.register(userName, password)
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
        val userName = loginViewModel.getUserName()
        val password = loginViewModel.getPassword()
        if (userName.isNotEmpty() && password.isNotEmpty()) {
            inputUsername.editText?.setText(userName)
            inputPassword.editText?.setText(password)
        }
    }

    private fun callLogin(userName: String, password: String) = launch(Dispatchers.Main) {
        textRegister.isClickable = false
        buttonLogin.isCheckable = false

        val token = loginViewModel.login(userName, password)
        if (token.isEmpty()) {
            Toast.makeText(this@LoginActivity, "登陆失败了", Toast.LENGTH_SHORT).show()
            return@launch
        }

        loginViewModel.saveData(this@LoginActivity, userName, password, token)

        textRegister.isClickable = true
        buttonLogin.isCheckable = true
        finish()
    }
}
