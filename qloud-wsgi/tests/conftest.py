import time
import jwt
import pytest

from flask import Flask, Blueprint
import qloud


JWT_SIGNING_ALGORITHM = "HS256"
SECRET = "00000000000000000000000000000000"

bp = Blueprint("test", __name__)


def epoch(adjust=0):
    return int(time.time()) + adjust


def new_jwt_claims(nbf_adjust, exp_adjust):
    claims = {
        "aud": "loqal.host",
        "email": "wsgi@test.code",
        "exp": epoch(exp_adjust),
        "iss": "https://login.loqal.host",
        "name": "Tester",
        "nbf": epoch(nbf_adjust),
        "q:idp": "email",
        "q:idp-sub": "tester",
        "q:udb": "udb-1",
        "sub": "ef0b4165-9f02-3316-86db-2e72a09981f7",
    }
    return jwt.encode(claims, SECRET, algorithm=JWT_SIGNING_ALGORITHM)


@bp.route("/")
def ok():
    return "OK"


@pytest.fixture()
def app_mandatory_auth():
    app = Flask(__name__)
    app.wsgi_app = qloud.QloudAuthentication(app.wsgi_app, SECRET, credentials_required=True)
    app.register_blueprint(bp)
    return app


@pytest.fixture()
def app_optional_auth():
    app = Flask(__name__)
    app.wsgi_app = qloud.QloudAuthentication(app.wsgi_app, SECRET, credentials_required=False)
    app.register_blueprint(bp)
    return app


@pytest.fixture()
def valid_jwt():
    return new_jwt_claims(-5, 5)


@pytest.fixture()
def expired_jwt():
    return new_jwt_claims(-50, -45)
