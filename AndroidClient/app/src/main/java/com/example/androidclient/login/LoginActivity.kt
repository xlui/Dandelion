package com.example.androidclient.login

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.androidclient.R
import com.example.androidclient.common.login
import com.example.androidclient.common.register
import com.example.androidclient.entity.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.*


class LoginActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val viewModel: LoginViewModel by lazy {
        ViewModelProviders.of(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        initViewModel()
        initView()
    }

    private fun initViewModel() {
        viewModel.loginStatus.observe(this, Observer { status ->

        })
    }

    private fun initView() {
        buttonLogin.setOnClickListener {
            val userName = inputUsername.editText?.toString()
            val password = inputPassword.editText?.toString()
            if (userName == null || password == null) {
                return@setOnClickListener
            }
            launch(Dispatchers.Main) {
                val token = login("", userName, password)

            }
        }

        textRegister.setOnClickListener {
            val userName = inputUsername.editText?.toString()
            val password = inputPassword.editText?.toString()
            if (userName == null || password == null) {
                return@setOnClickListener
            }
            launch(Dispatchers.Main) {
                val registerResult = register("", userName, password)
                if (!registerResult) {

                } else {

                }
            }
        }
    }
}
