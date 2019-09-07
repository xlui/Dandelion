import 'package:android/consts.dart';
import 'package:shared_preferences/shared_preferences.dart';

String getBaseUrl(SharedPreferences prefs) => prefs?.getString(baseUrl);

void setBaseUrl(SharedPreferences prefs, String _baseUrl) =>
    prefs?.setString(baseUrl, _baseUrl);

bool isBaseUrlSet(SharedPreferences prefs) =>
    getBaseUrl(prefs)?.isNotEmpty ?? false;

String getAccessToken(SharedPreferences prefs) => prefs?.getString(accessToken);

void setAccessToken(SharedPreferences prefs, String _accessToken) =>
    prefs?.setString(accessToken, _accessToken);

void delAccessToken(SharedPreferences prefs, String _accessToken) =>
    prefs?.remove(_accessToken);

bool isLoggedIn(SharedPreferences prefs) =>
    getAccessToken(prefs)?.isNotEmpty ?? false;
