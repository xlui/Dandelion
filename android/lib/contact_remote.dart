import 'package:flutter/material.dart';

class RemoteContact extends StatefulWidget {
  @override
  _RemoteContactState createState() => _RemoteContactState();
}

class _RemoteContactState extends State<RemoteContact> {
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
      ),
    );
  }
}
