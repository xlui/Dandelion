import 'dart:convert';
import 'dart:io';

import 'package:android/consts.dart';
import 'package:android/utils.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

import 'globals.dart';

class Login extends StatefulWidget {
  Login({this.onSuccess});

  final onSuccess;

  @override
  _LoginState createState() => _LoginState();
}

class _LoginState extends State<Login> {
  final style = TextStyle(fontFamily: 'Montserrat', fontSize: 16.0);
  final usernameController = TextEditingController();
  final passwordController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("登录"),
      ),
      body: Center(
        child: Container(
          color: Colors.white,
          child: Padding(
            padding: EdgeInsets.all(36.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                /// logo
                SizedBox(
                  height: 75.0,
                  child: Image.asset(
                    "assets/logo.png",
                    fit: BoxFit.contain,
                  ),
                ),
                SizedBox(
                  height: 25.0,
                ),

                /// 用户名
                TextField(
                  obscureText: false,
                  style: style,
                  decoration: InputDecoration(
                    contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
                    hintText: "用户名",
                    errorText: _isNotEmpty(usernameController.text),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(32.0),
                    ),
                  ),
                  controller: usernameController,
                ),
                SizedBox(
                  height: 15.0,
                ),

                /// 密码
                TextField(
                  obscureText: false,
                  style: style,
                  decoration: InputDecoration(
                    contentPadding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
                    hintText: "密码",
                    errorText: _isNotEmpty(passwordController.text),
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(32.0),
                    ),
                  ),
                  controller: passwordController,
                ),
                SizedBox(
                  height: 18.0,
                ),

                /// 登录按钮
                Material(
                  elevation: 5.0,
                  borderRadius: BorderRadius.circular(30.0),
                  color: Color(0xff66ccff),
                  child: MaterialButton(
                    minWidth: MediaQuery.of(context).size.width,
                    padding: EdgeInsets.fromLTRB(20.0, 15.0, 20.0, 15.0),
                    child: Text(
                      "登录",
                      textAlign: TextAlign.center,
                      style: style.copyWith(
                          color: Colors.white, fontWeight: FontWeight.bold),
                    ),
                    onPressed: _login,
                  ),
                ),
                SizedBox(
                  height: 55.0,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  String _isNotEmpty(String input) {
    bool isEmpty = input?.isEmpty ?? true;
    if (isEmpty) {
      return 'Input must contains at least 1 character';
    }
    return null;
  }

  void _login() {
    setBaseUrl(prefs, 'https://dandelion.xlui.app');
    Dio(BaseOptions(
      baseUrl: getBaseUrl(prefs),
      contentType: ContentType.json,
    )).post(
      pathLogin,
      data: {
        "username": usernameController.text,
        "password": passwordController.text
      },
    ).then((response) {
      var resp = json.decode(response.toString());
      setAccessToken(prefs, resp['access_token']);
      setUsername(prefs, usernameController.text);
      Fluttertoast.showToast(msg: '登录成功！');
      // 调用回调
      if (widget.onSuccess != null) {
        widget.onSuccess();
      }
      Navigator.pop(context);
    }).timeout(timeout, onTimeout: () {
      Fluttertoast.showToast(msg: "调用登录接口超时，请检查服务器配置！");
    }).catchError((error) {
      showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: Text('调用接口失败！'),
            content:
            Text('尝试登录失败，请检查 BaseUrl 和输入！\nBase URL: ${getBaseUrl(prefs)}'),
          );
        },
      );
      print(error);
    });
  }
}
