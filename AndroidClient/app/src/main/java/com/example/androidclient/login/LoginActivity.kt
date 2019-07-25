package com.example.androidclient.login

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.androidclient.R
import com.example.androidclient.common.login
import com.example.androidclient.common.register
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity(),CoroutineScope by MainScope(){
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
        loginButton.setOnClickListener {
//            launch {
//                val loginResult = login("","","")
//                if(loginResult.isEmpty()){
//
//                }else{
//
//                }
//            }
        }

        registerButton.setOnClickListener {
            launch {
                val registerResult = register("","","")
                if(registerResult){

                }else{
                    Toast.makeText(this@LoginActivity,"注册失败",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
