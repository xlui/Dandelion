import 'package:flutter/material.dart';

class LocalContact extends StatelessWidget {
  // Load local contacts
  @override
  Widget build(BuildContext context) {
    return new Scaffold(
        body: Center(
          child: Text(
            'Local contacts',
            style: TextStyle(fontSize: 20),
          ),
        ),
        floatingActionButton: FloatingActionButton(
          tooltip: 'Upload',
          child: Icon(Icons.arrow_upward),
          onPressed: null,
        ));
  }
}
