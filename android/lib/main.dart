import 'dart:convert';

import 'package:after_layout/after_layout.dart';
import 'package:android/consts.dart';
import 'package:android/contact_local.dart';
import 'package:android/contact_remote.dart';
import 'package:android/globals.dart';
import 'package:android/ui_utils.dart';
import 'package:android/utils.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:crypto/crypto.dart';
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
  bool loggedIn = false;

  /// 只在UI界面绘制完成后调用一次
  @override
  void afterFirstLayout(BuildContext context) {
    // 检查权限
    _checkPermission();
    // 设置全局数据访问接口
    SharedPreferences.getInstance().then((_prefs) {
      prefs = _prefs;
      setState(() {
        loggedIn = isLoggedIn(prefs);
      });
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
    // body
    drawer.addAll([
      _buildDrawerHeader(),
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
      ),
      _buildLoginOrLogoutOption(),
    ]);
    return drawer;
  }

  /// 构建侧栏 header
  Widget _buildDrawerHeader() {
    if (loggedIn) {
      var content = Utf8Encoder().convert(getUsername(prefs));
      var digest = md5.convert(content).toString();
      return UserAccountsDrawerHeader(
        accountName: Text(getUsername(prefs)),
        accountEmail: null,
        // 从 Gravatar 加载头像并缓存
        currentAccountPicture: GestureDetector(
          child: CircleAvatar(
            backgroundImage: CachedNetworkImageProvider(
              gravatar.replaceFirst(placeholder, digest),
            ),
          ),
          onTap: () {
            Fluttertoast.showToast(msg: (greet..shuffle()).first);
          },
        ),
      );
    } else {
      var content = Utf8Encoder().convert(DateTime.now().toIso8601String());
      var randomDigest = md5.convert(content).toString();
      return UserAccountsDrawerHeader(
        accountName: GestureDetector(
          child: Text("未登录！"),
          onTap: () {
            toLogin(context, onSuccess: () {
              setState(() {
                loggedIn = true;
              });
            });
          },
        ),
        accountEmail: null,
        // 未登录用户的头像是依据时间戳生成的
        currentAccountPicture: GestureDetector(
          child: CircleAvatar(
            backgroundImage:
            NetworkImage(gravatar.replaceFirst(placeholder, randomDigest)),
          ),
          onTap: () {
            toLogin(context, onSuccess: () {
              setState(() {
                loggedIn = true;
              });
            });
          },
        ),
      );
    }
  }

  /// 构建登入、登出侧栏
  Widget _buildLoginOrLogoutOption() {
    if (loggedIn) {
      return ListTile(
        title: Text("退出登录"),
        onTap: () {
          delAccessToken(prefs);
          delUsername(prefs);
          setState(() {
            loggedIn = false;
          });
          Fluttertoast.showToast(msg: "退出登录成功");
          Navigator.pop(context);
        },
      );
    } else {
      return ListTile(
        title: Text("登录"),
        onTap: () {
          toLogin(context, onSuccess: () {
            setState(() {
              loggedIn = true;
            });
          });
        },
      );
    }
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
