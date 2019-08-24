from datetime import datetime
from enum import Enum

from flask_jwt import JWT
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import safe_str_cmp

db = SQLAlchemy()
jwt = JWT()


class User(db.Model):
    __tablename__ = 'user'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    username = db.Column(db.VARCHAR(128), nullable=False, unique=True, index=True)
    password = db.Column(db.VARCHAR(128), nullable=False)
    record = db.Column(db.TEXT)

    create_time = db.Column(db.DATETIME, default=datetime.now)
    update_time = db.Column(db.DATETIME, default=datetime.now, onupdate=datetime.now)

    def __repr__(self):
        return f'User(id={self.id}, username={self.username})'


class Code(Enum):
    OK = 0  # 请求成功

    InvalidRequest = 10000  # 请求数据非法
    NotFound = 10001  # HTTP 404 错误
    InternalError = 10002  # HTTP 500 错误

    UsernameRegistered = 20000  # 用户名已被注册
    UserNotExist = 200001  # 用户不存在

    RequestFailed = 99999  # 请求失败


class Response:
    """通用 HTTP Response，code 为 0 时代表请求成功，此时 data 有效；
    code 为非 0 时代表请求失败，此时 error 有效。
    """

    def __init__(self, code: Code, data=None, error=None) -> None:
        super().__init__()
        self.code = code
        self.data = data
        self.error = error

    def build(self):
        from flask import jsonify
        from flask import make_response
        resp = jsonify({
            'code': self.code.value,
            'data': self.data,
            'error': self.error
        })
        return make_response(resp, 200)

    def __repr__(self):
        return f'Response(code={self.code}, data={self.data}, error={self.error})'


@jwt.authentication_handler
def authenticate(username, password):
    user = User.query.filter_by(username=username).scalar()
    if user and safe_str_cmp(user.password.encode('utf-8'), password.encode('utf-8')):
        return user


@jwt.identity_handler
def identify(payload):
    return User.query.filter_by(id=payload['identity']).scalar()
