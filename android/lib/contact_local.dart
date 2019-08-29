import 'package:after_layout/after_layout.dart';
import 'package:contacts_service/contacts_service.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class LocalContact extends StatefulWidget {
  @override
  _LocalContactState createState() => _LocalContactState();
}

class _LocalContactState extends State<LocalContact>
    with AfterLayoutMixin<LocalContact> {
  var _contacts = List<Contact>();

  /// UI 加载完成后从本地加载联系人，并通过 setState 通知 UI 刷新
  @override
  void afterFirstLayout(BuildContext context) {
    _loadContacts();
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      body: _drawBody(),
      floatingActionButton: FloatingActionButton(
        tooltip: 'Upload',
        child: Icon(Icons.arrow_upward),
        onPressed: null,
      ),
    );
  }

  /// 构建本地联系人界面
  Widget _drawBody() {
    var listTiles = List<Widget>();
    for (int i = 0; i < _contacts.length; i++) {
      listTiles.add(_buildRow(_contacts[i]));
      if (i != _contacts.length - 1) {
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

  /// 构建列表行
  Widget _buildRow(Contact contact) {
    return GestureDetector(
      behavior: HitTestBehavior.translucent,
      onTap: () {
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
        print('Tap list tile!');
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

  /// 加载联系人
  void _loadContacts() {
    ContactsService.getContacts().then((contacts) {
      setState(() {
        _contacts = contacts.toList(growable: false);
      });
    });
  }
}
