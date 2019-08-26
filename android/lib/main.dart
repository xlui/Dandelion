import 'package:after_layout/after_layout.dart';
import 'package:android/contact_local.dart';
import 'package:android/contact_remote.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:permission_handler/permission_handler.dart';

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

class _HomeState extends State<Home> with AfterLayoutMixin<Home> {
  final permission = PermissionGroup.contacts;
  var _status = PermissionStatus.unknown;
  int _currentIndex = 0;
  StatefulWidget _currentWidget = LocalContact();

  @override
  void initState() {
    super.initState();
  }

  // 只在UI界面绘制完成后调用一次
  @override
  void afterFirstLayout(BuildContext context) {
    _checkPermission();
  }

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
        onTap: _onTap,
      ),
      body: _currentWidget,
    );
  }

  // 检查是否有联系人权限
  void _checkPermission() {
    if (_status != PermissionStatus.granted) {
      PermissionHandler().requestPermissions([permission]).then((statuses) {
        final status = statuses[permission];
        if (status != PermissionStatus.granted) {
          Fluttertoast.showToast(msg: '无法读取本地联系人，请授予相关权限！');
          PermissionHandler().openAppSettings();
        } else {
          setState(() {
            _status = status;
          });
        }
      });
    }
  }

  // 底栏切换
  void _onTap(int index) {
    setState(() {
      _currentIndex = index;
      if (_currentIndex == 0) {
        // 如果点击“本地联系人”但是当前并不在，性能优化
        if (_currentWidget is LocalContact == false) {
          _checkPermission();
          _currentWidget = LocalContact();
        }
      } else {
        // 如果点击“远程联系人”但是当前并不在，性能优化
        if (_currentWidget is RemoteContact == false) {
          _currentWidget = RemoteContact();
        }
      }
    });
  }

  // 侧栏点击“本地联系人”
  void _loadLocalContact() {
    _onTap(0);
    Navigator.pop(context);
  }

  // 侧栏点击“云端联系人”
  void _loadRemoteContact() {
    _onTap(1);
    Navigator.pop(context);
  }
}
