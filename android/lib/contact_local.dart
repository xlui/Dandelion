import 'dart:convert';
import 'dart:io';

import 'package:after_layout/after_layout.dart';
import 'package:android/login.dart';
import 'package:android/ui_utils.dart';
import 'package:android/utils.dart';
import 'package:contacts_service/contacts_service.dart';
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'consts.dart';

class LocalContact extends StatefulWidget {
  @override
  _LocalContactState createState() => _LocalContactState();
}

class _LocalContactState extends State<LocalContact>
    with AfterLayoutMixin<LocalContact> {
  final textEditingController = TextEditingController();
  var _contacts = List<Contact>();
  SharedPreferences _prefs;

  /// UI 加载完成后从本地加载联系人，并通过 setState 通知 UI 刷新
  @override
  void afterFirstLayout(BuildContext context) {
    _loadContacts();
    _loadSharedPreferences();
  }

  /// 加载联系人
  void _loadContacts() {
    ContactsService.getContacts().then((contacts) {
      setState(() {
        _contacts = contacts.toList(growable: false);
      });
    });
  }

  /// 加载内存访问组件
  void _loadSharedPreferences() {
    SharedPreferences.getInstance().then((prefs) {
      setState(() {
        _prefs = prefs;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: drawBody(context, _contacts),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Upload',
        child: Icon(Icons.arrow_upward),
        onPressed: _loginOrUpload,
      ),
    );
  }

  /// 点击向上箭头：
  /// 如果本地没有配置 baseUrl，则提示进行配置；
  /// 如果用户没有登录，则跳转到登录界面；
  /// 如果已登录，则上传本地通讯录。
  void _loginOrUpload() {
    if (!isBaseUrlSet(_prefs)) {
      showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: Text("请首先配置服务器地址！"),
            content: TextField(
              decoration: InputDecoration(hintText: "TextField in the dialog"),
              controller: textEditingController,
            ),
            actions: <Widget>[
              FlatButton(
                child: Text("关闭"),
                onPressed: () => Navigator.pop(context),
              ),
              FlatButton(
                child: Text("提交"),
                onPressed: () {
                  setBaseUrl(_prefs, textEditingController.text);
                  Navigator.pop(context);
                },
              ),
            ],
          );
        },
      );
      return;
    }
    if (!isLoggedIn(_prefs)) {
      Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => Login(),
        ),
      );
      return;
    }

    /// 上传通讯录
    Fluttertoast.showToast(msg: "即将上传本地联系人");
    _uploadContacts();
  }

  void _uploadContacts() {
    setBaseUrl(_prefs, "https://dandelion.xlui.app");
    Dio(BaseOptions(
      baseUrl: getBaseUrl(_prefs),
      contentType: ContentType.json,
      headers: {"Authorization": "JWT ${getAccessToken(_prefs)}"},
    )).post(
      pathUpload,
      data: jsonEncode(_contacts
          .map((contact) =>
      {
        "displayName": contact.displayName,
        "phones": contact.phones.map((item) => item.value).join(", ")
      })
          .toList(growable: false)),
    ).then((response) {
      var resp = json.decode(response.toString());
      Fluttertoast.showToast(msg: resp['data']);
      print('Response: $response');
    }).timeout(timeout, onTimeout: () {
      Fluttertoast.showToast(msg: '请求服务器接口超时，请检查服务器设置！');
    }).catchError((error) {
      Fluttertoast.showToast(msg: '上传本地通讯录失败，可能是本地 Token 已过期，请重新登录！');
      print('Error: $error');
    });
  }
}
