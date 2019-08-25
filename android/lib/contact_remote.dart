import 'package:flutter/material.dart';

class RemoteContact extends StatelessWidget {
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
          tooltip: 'Dwonload',
          child: Icon(Icons.arrow_downward),
          onPressed: null,
        ));
  }
}
