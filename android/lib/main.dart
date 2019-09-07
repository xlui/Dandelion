import 'package:after_layout/after_layout.dart';
import 'package:android/contact_local.dart';
import 'package:android/contact_remote.dart';
import 'package:android/globals.dart';
import 'package:android/utils.dart';
import 'package:flutter/material.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:validators/validators.dart';

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
  final _permission = PermissionGroup.contacts;
  final _textEditingController = TextEditingController();
  var _status = PermissionStatus.unknown;
  int _currentIndex = 0;
  StatefulWidget _currentWidget = LocalContact();

  /// 只在UI界面绘制完成后调用一次
  @override
  void afterFirstLayout(BuildContext context) {
    // 检查权限
    _checkPermission();
    // 设置全局数据访问接口
    SharedPreferences.getInstance().then((_prefs) {
      prefs = _prefs;
    });
  }

  // 检查是否有联系人权限
  void _checkPermission() {
    if (_status != PermissionStatus.granted) {
      PermissionHandler().requestPermissions([_permission]).then((statuses) {
        final status = statuses[_permission];
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      drawer: Drawer(
        child: ListView(
          padding: EdgeInsets.zero,
          children: _buildDrawerChildren(),
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

  /// 构建侧栏
  List<Widget> _buildDrawerChildren() {
    var drawer = List<Widget>();
    drawer.addAll([
      // TODO: 添加用户头像，判断是否登录
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
        title: Text('设置服务器地址'),
        onTap: _configBaseUrl,
      )
    ]);
    return drawer;
  }

  /// 配置服务器地址
  void _configBaseUrl() {
    _textEditingController.text = "";
    showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: Text("配置服务器地址"),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: <Widget>[
                Text("旧服务器地址"),
                Padding(
                  padding: EdgeInsets.fromLTRB(0, 5.0, 0, 5.0),
                  child: Text(getBaseUrl(prefs)),
                ),
                Text("新服务器地址"),
                TextField(
                  decoration: InputDecoration(
                    hintText: "server address",
                  ),
                  controller: _textEditingController,
                ),
              ],
            ),
            actions: <Widget>[
              FlatButton(
                child: Text("关闭"),
                onPressed: () => Navigator.pop(context),
              ),
              FlatButton(
                child: Text("提交"),
                onPressed: () {
                  var address = _textEditingController.text;
                  if (!isURL(address)) {
                    Fluttertoast.showToast(msg: "服务器地址格式非法！");
                    return;
                  } else {
                    setBaseUrl(prefs, address);
                  }
                  Navigator.pop(context);
                },
              ),
            ],
          );
        });
  }

  /// 底栏切换
  void _onTap(int index) {
    setState(() {
      _currentIndex = index;
      if (_currentIndex == 0) {
        // 点击“本地联系人”，只有当前并不在该页面时才进行切换
        if (_currentWidget is LocalContact == false) {
          _checkPermission();
          _currentWidget = LocalContact();
        }
      } else {
        // 点击“远程联系人”，只有当前并不在该页面时才进行切换
        if (_currentWidget is RemoteContact == false) {
          _currentWidget = RemoteContact();
        }
      }
    });
  }

  /// 侧栏点击“本地联系人”
  void _loadLocalContact() {
    _onTap(0);
    Navigator.pop(context);
  }

  /// 侧栏点击“云端联系人”
  void _loadRemoteContact() {
    _onTap(1);
    Navigator.pop(context);
  }
}
