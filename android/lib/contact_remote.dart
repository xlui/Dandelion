import 'dart:convert';
import 'dart:io';

import 'package:android/ui_utils.dart';
import 'package:android/utils.dart';
import 'package:contacts_service/contacts_service.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'consts.dart';

class RemoteContact extends StatefulWidget {
  @override
  _RemoteContactState createState() => _RemoteContactState();
}

class _RemoteContactState extends State<RemoteContact> {
  var _contacts = List<Contact>();
  SharedPreferences _prefs;

  @override
  void initState() {
    _loadSharedPreferences();
    super.initState();
  }

  void _loadSharedPreferences() {
    if (_prefs == null) {
      SharedPreferences.getInstance().then((prefs) {
        setState(() {
          _prefs = prefs;
        });
        _loadContacts();
      });
    }
  }

  /// 拉取云端联系人
  void _loadContacts() {
    if (_prefs != null) {
      Dio(BaseOptions(
        baseUrl: getBaseUrl(_prefs),
        contentType: ContentType.json,
        headers: {"Authorization": "JWT ${getAccessToken(_prefs)}"},
      )).get(pathDownload).then((response) {
        var resp = json.decode(response.toString());
        setState(() {
          _contacts = _parseContact(resp['data']);
        });
        Fluttertoast.showToast(msg: "成功拉取云端联系人");
      }).timeout(timeout, onTimeout: () {
        Fluttertoast.showToast(msg: "请求服务器接口超时，请检查服务器设置！");
      }).catchError((error) {
        Fluttertoast.showToast(msg: "拉取云端联系人失败，请检查服务器设置！");
        print('Error: $error');
      });
    }
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
}
