import os
from datetime import timedelta

from flask import Flask

from app.models import db, jwt


def create_app():
    app = Flask(__name__)
    app.config['DEBUG'] = True
    app.config['SECRET_KEY'] = 'Y3ApDe7hFQs%@XY7P6TX'
    app.config['SQLALCHEMY_DATABASE_URI'] = f'sqlite:///{os.path.join(os.path.pardir, "data.sqlite")}'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    app.config['SQLALCHEMY_RECORD_QUERIES'] = True
    app.config['SQLALCHEMY_COMMIT_ON_TEARDOWN'] = True
    app.config['JWT_EXPIRATION_DELTA'] = timedelta(days=7)
    app.config['JWT_AUTH_URL_RULE'] = '/login'

    db.init_app(app)
    jwt.init_app(app)

    return app


app = create_app()

from app import views
