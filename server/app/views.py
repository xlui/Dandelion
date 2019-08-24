from flask import request
from flask_jwt import jwt_required, current_identity

from app import app, db
from app.models import Response, Code, User


@app.route('/', methods=['GET'])
def index():
    return """
    <!doctype html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Dandelion - 通讯录迁移工具</title>
    <style>
        article {
            width: 50%;
            margin: auto;
            padding: 8px 16px;
            background-color: #fff;
            border: 1px solid #d1d5da;
            border-radius: 3px;
        }

        article h1 {
            margin-top: 24px;
            margin-bottom: 16px;
            font-size: 2em;
            border-bottom: 1px solid #eaecef;
            padding-bottom: .3em;
        }

        article h2 {
            margin-top: 24px;
            margin-bottom: 16px;
            font-size: 1.5em;
            border-bottom: 1px solid #eaecef;
            padding-bottom: .3em;
        }

        article ol {
            padding-left: 2em;
        }

        article ol li {
            margin-top: .25em;
        }

        a {
            text-decoration: none;
        }

        .highlight {
            margin-bottom: 16px
        }

        .markdown-body code {
            background-color: rgba(27, 31, 35, .05);
            border-radius: 3px;
            font-size: .9em;
            margin: 0;
            padding: .2em .4em
        }

        .markdown-body pre, .markdown-body .highlight pre {
            margin-bottom: 0;
            padding: 16px;
            border-radius: 3px;
            line-height: 1.45;
            background-color: #f6f8fa;
            overflow: auto;
            word-break: normal;
        }

        .markdown-body pre code {
            padding: 0;
            margin: 0;
            border: 0;
            background-color: transparent;
            display: inline;
            line-height: inherit;
            overflow: visible;
            word-wrap: normal;
        }

        .pl-c1 {
            color: #005cc5
        }

        .pl-pds, .pl-s {
            color: #032f62
        }
    </style>
</head>
<body>
<article class="markdown-body">
    <h1>
        <a id="dandelion" href="#dandelion">
            <svg width="16" height="16">
                <path fill-rule="evenodd"
                      d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path>
            </svg>
        </a>
        Dandelion
    </h1>
    <p>通讯录迁移工具，用于更换手机时快速迁移通讯录。</p>
    <p><a href="https://github.com/kjcxsoft/Dandelion" target="_blank">GitHub</a>


    <h2>
        <a id="v01-功能需求" class="anchor" aria-hidden="true" href="#v01-功能需求">
            <svg width="16" height="16">
                <path fill-rule="evenodd"
                      d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path>
            </svg>
        </a>
        v0.1 功能需求
    </h2>
    <ol>
        <li>读取旧机的联系人列表，存入服务器</li>
        <li>新机从服务器拉取联系人列表，写入本地通讯录</li>
        <li>如果本地通讯录已经有同名联系人，则判断要写入的手机号是否已经存在，如果存在则跳过，如果不存在则在联系人下新增手机号</li>
        <li>客户端需要支持动态更换服务器的 URL</li>
        <li>服务器实现尽量简洁并支持 Docker 启动，做到随用随开，不用就停。</li>
    </ol>


    <h1>
        <a id="api-说明" aria-hidden="true" href="#api-说明">
            <svg width="16" height="16">
                <path fill-rule="evenodd"
                      d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path>
            </svg>
        </a>
        API 说明
    </h1>
    <p>
        Dandelion 后端 API 说明，后端只做数据中转。同一用户可以重复提交通讯录，但是以最新的为主。
        并没有重置密码的功能，如果忘记密码就重新注册一个账号吧 :)
    </p>
    <p>
        安卓在开发时需要确保 API 地址可动态设置，即可做到服务器随用随开，不用就停。
    </p>


    <h2>
        <a id="测试用服务器" class="anchor" aria-hidden="true" href="#测试用服务器">
            <svg width="16" height="16">
                <path fill-rule="evenodd"
                      d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path>
            </svg>
        </a>
        测试用服务器
    </h2>
    <p>
        <a href="https://dandelion.xlui.app" rel="nofollow">https://dandelion.xlui.app</a>
    </p>


    <h2>
        <a id="1-注册" class="anchor" aria-hidden="true" href="#1-注册">
            <svg width="16" height="16">
                <path fill-rule="evenodd"
                      d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path>
            </svg>
        </a>
        1. 注册
    </h2>
    <p><code>/register</code></p>
    <p>接收 JSON 数据：</p>
    <div class="highlight">
        <pre>{
            <span class="pl-s"><span class="pl-pds">"</span>username<span class="pl-pds">"</span></span>:<span
                    class="pl-s"><span class="pl-pds">"</span>someUsername<span class="pl-pds">"</span></span>,
            <span class="pl-s"><span class="pl-pds">"</span>password<span class="pl-pds">"</span></span>:<span
                    class="pl-s"><span class="pl-pds">"</span>somePassword<span class="pl-pds">"</span></span>
        }</pre>
    </div>
    <p>返回自定义的 Response：</p>
    <div class="highlight">
        <pre>{
          <span class="pl-s"><span class="pl-pds">"</span>code<span class="pl-pds">"</span></span>: <span class="pl-c1">0</span>,
          <span class="pl-s"><span class="pl-pds">"</span>data<span class="pl-pds">"</span></span>: <span
                    class="pl-s"><span class="pl-pds">"</span>成功注册！<span class="pl-pds">"</span></span>,
          <span class="pl-s"><span class="pl-pds">"</span>error<span class="pl-pds">"</span></span>: <span
                    class="pl-c1">null</span>
        }</pre>
    </div>
    <p>或者：</p>
    <div class="highlight">
        <pre>{
          <span class="pl-s"><span class="pl-pds">"</span>code<span class="pl-pds">"</span></span>: <span
                    class="pl-c1">10000</span>,
          <span class="pl-s"><span class="pl-pds">"</span>data<span class="pl-pds">"</span></span>: <span
                    class="pl-c1">null</span>,
          <span class="pl-s"><span class="pl-pds">"</span>error<span class="pl-pds">"</span></span>: <span class="pl-s"><span
                    class="pl-pds">"</span>请求数据缺少用户名或密码！<span class="pl-pds">"</span></span>
        }</pre>
    </div>
    <p>
        只有在 <code>code</code> 为 0 时才是请求成功，此时 <code>data</code> 中的数据有效，<code>error</code> 无效。
        <code>code</code> 不为 0 代表请求失败，此时 <code>code</code> 为错误码，<code>error</code> 为错误信息，<code>data</code> 无效。
    </p>


    <h2>
        <a id="2-登录" class="anchor" aria-hidden="true" href="#2-登录">
            <svg width="16" height="16">
                <path fill-rule="evenodd"
                      d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path>
            </svg>
        </a>
        2. 登录
    </h2>
    <p><code>/login</code></p>
    <p>接收 JSON 数据：</p>
    <div class="highlight">
        <pre>{
          <span class="pl-s"><span class="pl-pds">"</span>username<span class="pl-pds">"</span></span>: <span
                    class="pl-s"><span class="pl-pds">"</span>1<span class="pl-pds">"</span></span>,
          <span class="pl-s"><span class="pl-pds">"</span>password<span class="pl-pds">"</span></span>: <span
                    class="pl-s"><span class="pl-pds">"</span>dev<span class="pl-pds">"</span></span>
        }</pre>
    </div>
    <p>返回 <code>access_token</code>：</p>
    <div class="highlight">
        <pre>{
          <span class="pl-s"><span class="pl-pds">"</span>access_token<span class="pl-pds">"</span></span>: <span
                    class="pl-s"><span class="pl-pds">"</span>a long token<span class="pl-pds">"</span></span>
        }</pre>
    </div>
    <p>由于登录是托管给 Flask-JWT 的，没办法自定义返回数据做到统一。</p>


    <h2>
        <a id="3-上传通讯录" class="anchor" aria-hidden="true" href="#3-上传通讯录">
            <svg width="16" height="16">
                <path fill-rule="evenodd"
                      d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path>
            </svg>
        </a>
        3. 上传通讯录
    </h2>
    <p><code>/push</code>，此 API 需要 <code>access_token</code>。</p>
    <p>请求头需要添加认证：</p>
    <pre><code>Authorization: JWT access_token</code></pre>
    <p>请求体直接上传 JSON 数据即可，服务端只做保存。</p>


    <h2>
        <a id="4-下载通讯录" class="anchor" aria-hidden="true" href="#4-下载通讯录">
            <svg width="16" height="16">
                <path fill-rule="evenodd"
                      d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path>
            </svg>
        </a>
        4. 下载通讯录
    </h2>
    <p><code>/pull</code>，此 API 需要 <code>access_token</code>。</p>
    <p>请求头需要添加认证：</p>
    <pre><code>Authorization: JWT access_token</code></pre>
    <p>会返回 Token 对应用户保存的通讯录，即在上一步上传的 JSON。</p>


    <h1>
        <a id="license" class="anchor" aria-hidden="true" href="#license">
            <svg width="16" height="16">
                <path fill-rule="evenodd"
                      d="M4 9h1v1H4c-1.5 0-3-1.69-3-3.5S2.55 3 4 3h4c1.45 0 3 1.69 3 3.5 0 1.41-.91 2.72-2 3.25V8.59c.58-.45 1-1.27 1-2.09C10 5.22 8.98 4 8 4H4c-.98 0-2 1.22-2 2.5S3 9 4 9zm9-3h-1v1h1c1 0 2 1.22 2 2.5S13.98 12 13 12H9c-.98 0-2-1.22-2-2.5 0-.83.42-1.64 1-2.09V6.25c-1.09.53-2 1.84-2 3.25C6 11.31 7.55 13 9 13h4c1.45 0 3-1.69 3-3.5S14.5 6 13 6z"></path>
            </svg>
        </a>
        License
    </h1>
    <p>MIT.</p>
</article>
</body>
</html>
    """

@app.route('/register', methods=['POST'])
def register():
    resp = check_mime()
    if resp.code.value:
        return resp.build()
    username, password = request.json.get('username'), request.json.get('password')
    if not all([username, password]):
        return Response(Code.InvalidRequest, error='请求数据缺少用户名或密码！').build()
    user = User.query.filter_by(username=username).scalar()
    if user:
        return Response(Code.UsernameRegistered, error='用户名已被注册！').build()
    user = User(username=username, password=password)
    db.session.add(user)
    db.session.commit()
    return Response(Code.OK, data='成功注册！').build()


@app.route('/push', methods=['POST'])
@jwt_required()
def push():
    resp = check_mime()
    if resp.code.value:
        return resp.build()
    user = User.query.filter_by(id=current_identity.id).scalar()
    if not user:
        return Response(Code.InternalError, error='无Token对应用户，但是Token有效，用户可能已被删除，请重新注册！').build()
    user.record = str(request.json)
    db.session.add(user)
    db.session.commit()
    return Response(Code.OK, data=f'成功更新 {user.username} 的通讯录！').build()


@app.route('/pull', methods=['GET'])
@jwt_required()
def pull():
    user = User.query.filter_by(id=current_identity.id).scalar()
    if not user:
        return Response(Code.InternalError, error='无Token对应用户，但是Token有效，用户可能已被删除，请重新注册！').build()
    return Response(Code.OK, data=user.record).build()


def check_mime():
    if not request.is_json:
        return Response(Code.InvalidRequest, error='请求数据格式必须为 JSON！')
    else:
        return Response(Code.OK)
