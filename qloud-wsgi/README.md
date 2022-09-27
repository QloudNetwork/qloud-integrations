# Qloud Integration for WSGI Servers

`qloud-wsgi` is a WSGI middleware compatible with all frameworks that support WSGI, like
[Flask](https://flask.palletsprojects.com/). It provides a simple way to integrate with [Qloud](https://qloud.network).

## Installation

```bash
pip install qloud-wsgi
```

Note, we currently only support Python 3.7 and above.

## Usage

### Flask

```python
from flask import Flask, request
import qloud

SECRET = "YOUR_SECRET"

app = Flask(__name__)
app.wsgi_app = qloud.QloudAuthentication(app.wsgi_app, SECRET, credentials_required=False)

@app.route("/")
def hello_user():
    return request.environ.get("auth") or "Hello Anonymous!"
```

The `SECRET` is the secret key that you can find in the [Qloud Console Dashboard](https://console.qloud.network),
respectively, for the [DevAuth environment](https://docs.qloud.network/local-development/) it's fixed
to `00000000000000000000000000000000`.

The middleware injects the decoded JSON Web Token using the key `auth` into the environment of the request (e.g.
`environ["auth"]` in plain WSGI, or `request.environ.get("auth")` in a Flask app).
[Our documentation](https://docs.qloud.network/architecture/jwt) has all the information of the fields present in the
JWT.

### Credentials Required

The `credentials_required` parameter is optional and defaults to `False`. For requests without a JSON Web Token,
`environ["auth"]` will not be set.

If your application uses [mandatory authentication](https://docs.qloud.network/configuration/authentication-mode), we
recommend to set `credentials_required` to `True`, the integration itself will then also reject unauthenticated requests
if they bypass the Proxy.
