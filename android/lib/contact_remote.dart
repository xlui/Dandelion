import 'dart:convert';
import 'dart:io';

import 'package:after_layout/after_layout.dart';
import 'package:android/login.dart';
import 'package:android/ui_utils.dart';
import 'package:android/utils.dart';
import 'package:contacts_service/contacts_service.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';

import 'consts.dart';
import 'globals.dart';

class RemoteContact extends StatefulWidget {
  @override
  _RemoteContactState createState() => _RemoteContactState();
}

class _RemoteContactState extends State<RemoteContact>
    with AfterLayoutMixin<RemoteContact> {
  final _refreshController = RefreshController();
  var _contacts = List<Contact>();

  @override
  void afterFirstLayout(BuildContext context) {
    _loadContacts(callback: () => Fluttertoast.showToast(msg: "成功拉取云端联系人"));
  }

  /// 拉取云端联系人
  void _loadContacts({Function callback}) {
    if (!isLoggedIn(prefs)) {
      Fluttertoast.showToast(msg: "请先登录！");
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => Login()),
      );
      return;
    }
    Fluttertoast.showToast(msg: "开始拉取云端联系人");
    Dio(BaseOptions(
      baseUrl: getBaseUrl(prefs),
      contentType: ContentType.json,
      headers: {"Authorization": "JWT ${getAccessToken(prefs)}"},
    )).get(pathDownload).then((response) {
      var resp = json.decode(response.toString());
      if (resp['data'] == null) {
        Fluttertoast.showToast(msg: "服务器返回结果为空，尚未上传本地联系人？");
        return;
      }
      setState(() {
        _contacts = _parseContact(resp['data']);
      });
      if (callback != null) {
        callback();
      }
    }).timeout(timeout, onTimeout: () {
      Fluttertoast.showToast(msg: "请求服务器接口超时，请检查服务器设置！");
    }).catchError((error) {
      var resp = jsonDecode(error.response.toString());
      if (401 == resp["status_code"]) {
        Fluttertoast.showToast(msg: "登录信息已过期，请重新登录！");
        delAccessToken(prefs);
        delUsername(prefs);
        return;
      }
      Fluttertoast.showToast(msg: "拉取云端联系人失败，请检查服务器设置！");
      print('Error: $error');
    });
  }

  /// 解析联系人JSON
  List<Contact> _parseContact(String contacts) {
    var contactList = json.decode(contacts) as List;
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
      body: SmartRefresher(
        controller: _refreshController,
        enablePullDown: true,
        onRefresh: () {
          _loadContacts(
            callback: () => Fluttertoast.showToast(msg: "加载成功 (～￣▽￣)～"),
          );
          _refreshController.refreshCompleted();
        },
        child: drawBody(context, _contacts),
      ),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Dwonload',
        child: Icon(Icons.arrow_downward),
        onPressed: _onDownloadPress,
      ),
    );
  }

  /// 提供按钮选项
  void _onDownloadPress() {
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: Text("请选择要进行的操作："),
          actions: <Widget>[
            FlatButton(
              child: Text("查看云端联系人"),
              onPressed: () {
                _loadContacts(
                  callback: () => Fluttertoast.showToast(msg: "成功拉取云端联系人"),
                );
                Navigator.pop(context);
              },
            ),
            FlatButton(
              child: Text("同步云端联系人到本地"),
              onPressed: _mergeRemoteContacts,
            ),
          ],
        );
      },
    );
  }

  /// 拉取云端联系人并合并到本地
  void _mergeRemoteContacts() {
    _loadContacts(callback: () {
      Fluttertoast.showToast(msg: "开始同步云端联系人到本地");
      ContactsService.getContacts().then((contacts) {
        // 将本地联系人转为 displayName:Contact 的形式，方便验证云端是否存在相同联系人
        var contactMap = Map<String, Contact>.fromIterable(
          contacts,
          key: (item) => item.displayName,
          value: (item) => item,
        );
        // 对每个本地联系人分别处理
        for (var newContact in _contacts) {
          if (contactMap.containsKey(newContact.displayName)) {
            // 如果云端存在和本地联系人同名的联系人，则合并手机号
            var oldPhones = contactMap[newContact.displayName].phones.toList();
            var oldPhoneSet = oldPhones.map((item) => item.value).toSet();
            // 将本地不存在的手机号合并进本地手机号列表
            newContact.phones
                .skipWhile((item) => oldPhoneSet.contains(item.value))
                .forEach((item) => oldPhones.add(item));
            // 更新本地手机号引用
            contactMap[newContact.displayName].phones = oldPhones;
            // 更新本地联系人
            ContactsService.updateContact(contactMap[newContact.displayName]);
          } else {
            // 如果云端不存在和本地联系人同名联系人，则新建联系人
            newContact.givenName = newContact.displayName;
            ContactsService.addContact(newContact);
          }
        }
        Fluttertoast.showToast(msg: "同步完成！");
        Navigator.pop(context);
      });
    });
  }
}
