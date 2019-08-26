import 'package:contacts_service/contacts_service.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class LocalContact extends StatefulWidget {
  @override
  _LocalContactState createState() => _LocalContactState();
}

class _LocalContactState extends State<LocalContact> {
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

  Widget _drawBody() {
    _loadContacts();
    return Center(
      child: Text(
        'Local contacts',
        style: TextStyle(fontSize: 20),
      ),
    );
  }

  void _loadContacts() async {
    var contacts = await ContactsService.getContacts();
    contacts.forEach((contact) {
      print(
          '${contact.displayName}: ${contact.phones.map((item) =>
          item.value)})}');
    });
  }
}
