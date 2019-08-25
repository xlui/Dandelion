import 'package:android/contact_local.dart';
import 'package:android/contact_remote.dart';
import 'package:flutter/material.dart';

void main() => runApp(MyApp());
var theme = Colors.blue;

class MyApp extends StatelessWidget {
  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Dandelion',
      theme: ThemeData(
        // This is the theme of your application.
        //
        // Try running your application with "flutter run". You'll see the
        // application has a blue toolbar. Then, without quitting the app, try
        // changing the primarySwatch below to Colors.green and then invoke
        // "hot reload" (press "r" in the console where you ran "flutter run",
        // or simply save your changes to "hot reload" in a Flutter IDE).
        // Notice that the counter didn't reset back to zero; the application
        // is not restarted.
        primarySwatch: theme,
      ),
      home: Home(),
    );
  }
}

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  int _currentIndex = 0;
  StatelessWidget _currentWidget = LocalContact();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      drawer: Drawer(
        child: ListView(
          padding: EdgeInsets.zero,
          children: <Widget>[
            DrawerHeader(
              padding: EdgeInsets.fromLTRB(16.0, 16.0, 16.0, 0),
              decoration: BoxDecoration(color: theme),
              child: Text('Drawer Header'),
            ),
            ListTile(
              title: Text('本地联系人'),
              onTap: _loadLocalContact,
            ),
            ListTile(
              title: Text('云端联系人'),
              onTap: _loadRemoteContact,
            ),
            Divider(
              height: 1,
              color: Colors.grey,
            ),
            ListTile(
              title: Text('设置'),
              onTap: () {
                // do nothing
              },
            )
          ],
        ),
      ),
      appBar: AppBar(
        title: new Text('Dandelion'),
      ),
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        items: [
          BottomNavigationBarItem(
            icon: Icon(Icons.people),
            title: new Text('本地联系人'),
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.people_outline),
            title: new Text('云端联系人'),
          )
        ],
        onTap: _onTabTapped,
      ),
      body: _currentWidget,
    );
  }

  // 底栏切换
  void _onTabTapped(int index) {
    setState(() {
      _currentIndex = index;
      if (_currentIndex == 0) {
        _currentWidget = LocalContact();
      } else {
        _currentWidget = RemoteContact();
      }
    });
  }

  // 侧栏点击“本地联系人”
  void _loadLocalContact() {
    _onTabTapped(0);
    Navigator.pop(context);
  }

  // 侧栏点击“云端联系人”
  void _loadRemoteContact() {
    _onTabTapped(1);
    Navigator.pop(context);
  }
}
