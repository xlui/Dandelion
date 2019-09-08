final split = ", ";
final placeholder = "MD5";
final gravatar = "https://www.gravatar.com/avatar/$placeholder?d=identicon";
final greet = [
  "I'm Feeling Lucky!",
  "I'm Feeling Doodley!",
  "I'm Feeling Adventurous!",
  "I'm Feeling Artistic!",
  "I'm Feeling Hungry!",
  "I'm Feeling Puzzled!",
  "I'm Feeling Trendy!",
  "I'm Feeling Stellar!",
  "I'm Feeling Playful!",
  "I'm Feeling Wonderful!",
  "I'm Feeling Generous!",
  "I'm Feeling Curious!",
];

/// HTTP host，可配置化
final baseUrl = "base_url";
final accessToken = "access_token";
final username = "username";
final timeout = Duration(seconds: 2);

/// HTTP 请求接口
final pathLogin = "/login";
final pathUpload = "/push";
final pathDownload = "/pull";
