import 'dart:convert';
import 'dart:io';

import 'package:android/login.dart';
import 'package:android/ui_utils.dart';
import 'package:android/utils.dart';
import 'package:contacts_service/contacts_service.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';

import 'consts.dart';
import 'globals.dart';

class RemoteContact extends StatefulWidget {
  @override
  _RemoteContactState createState() => _RemoteContactState();
}

class _RemoteContactState extends State<RemoteContact> {
  var _contacts = List<Contact>();

  @override
  void initState() {
    _loadContacts();
    super.initState();
  }

  /// 拉取云端联系人
  void _loadContacts() {
    if (!isLoggedIn(prefs)) {
      Fluttertoast.showToast(msg: "请先登录！");
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => Login()),
      );
      return;
    }
    Dio(BaseOptions(
      baseUrl: getBaseUrl(prefs),
      contentType: ContentType.json,
      headers: {"Authorization": "JWT ${getAccessToken(prefs)}"},
    )).get(pathDownload).then((response) {
      var resp = json.decode(response.toString());
      setState(() {
        _contacts = _parseContact(resp['data']);
      });
      Fluttertoast.showToast(msg: "成功拉取云端联系人");
    }).timeout(timeout, onTimeout: () {
      Fluttertoast.showToast(msg: "请求服务器接口超时，请检查服务器设置！");
    }).catchError((error) {
      var resp = jsonDecode(error.response.toString());
      if (401 == resp["status_code"]) {
        Fluttertoast.showToast(msg: "登录信息已过期，请重新登录！");
        delAccessToken(prefs);
        return;
      }
      Fluttertoast.showToast(msg: "拉取云端联系人失败，请检查服务器设置！");
      print('Error: $error');
    });
  }

  /// 解析联系人JSON
  List<Contact> _parseContact(String contacts) {
    print('Contacts: $contacts');
    var contactList = json.decode(contacts) as List;
    print('ContactList: $contactList');
    return contactList.map((contactMap) {
      var contact = Contact();
      contact.displayName = contactMap['displayName'];
      var phones = contactMap['phones'].split(", ") as List<String>;
      contact.phones = phones.map((phone) => Item(value: phone)).toList();
      return contact;
    }).toList();
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: drawBody(context, _contacts),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Dwonload',
        child: Icon(Icons.arrow_downward),
        onPressed: _loadContacts,
      ),
    );
  }
}
