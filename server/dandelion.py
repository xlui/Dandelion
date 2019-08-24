from flask_script import Manager, Shell, Command

from app import app, db
from app.models import User

manager = Manager(app)

if __name__ == '__main__':
    def make_shell_context():
        return dict(app=app, db=db, User=User)


    def init():
        db.drop_all()
        db.create_all()
        print('Init success!')


    manager.add_command('shell', Shell(make_context=make_shell_context))
    manager.add_command('init', Command(func=init))

    manager.run()
