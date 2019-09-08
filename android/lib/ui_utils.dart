import 'package:contacts_service/contacts_service.dart';
import 'package:flutter/material.dart';

import 'login.dart';

void toLogin(BuildContext context, {Function onSuccess}) {
  Navigator.push(
    context,
    MaterialPageRoute(
      builder: (context) =>
          Login(
            onSuccess: onSuccess,
          ),
    ),
  );
}

/// 构建联系人界面
Widget drawBody(BuildContext context, List<Contact> contacts) {
  var listTiles = List<Widget>();
  for (int i = 0; i < contacts.length; i++) {
    /// 构建本地联系人列表项
    listTiles.add(_buildRow(context, contacts[i]));
    if (i != contacts.length - 1) {
      /// 如果不是最后一个元素，则添加分隔线
      listTiles.add(Divider(
        height: 1,
        indent: 12,
        endIndent: 12,
      ));
    }
  }
  return ListView(
    children: listTiles,
  );
}

/// 构建列表行骨架
Widget _buildRow(BuildContext context, Contact contact) {
  return GestureDetector(
    behavior: HitTestBehavior.translucent,
    onTap: () {
      /// 点击列表项对话框显示联系人信息
      showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: Text(contact.displayName),
            content: Text(
              contact.phones.map((phone) => phone.value).join(', '),
            ),
          );
        },
      );
    },
    child: Container(
      padding: EdgeInsets.fromLTRB(16.0, 10.0, 16.0, 10.0),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: _buildContactInfo(contact),
      ),
    ),
  );
}

/// 构建联系人显示信息
List<Widget> _buildContactInfo(Contact contact) {
  var contactInfo = List<Widget>();
  contactInfo.add(Container(
    padding: EdgeInsets.only(bottom: 8.0),
    child: Text(
      contact.displayName,
      style: TextStyle(fontWeight: FontWeight.bold),
    ),
  ));
  for (var item in contact.phones) {
    contactInfo.add(
      new Text(
        item.value,
        style: TextStyle(color: Colors.grey[500]),
      ),
    );
  }
  return contactInfo;
}
