# qloud-wsgi

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
from flask import Flask
import qloud

SECRET = "YOUR_SECRET"

app = Flask(__name__)
app.wsgi_app = qloud.QloudAuthentication(app.wsgi_app, SECRET, credentials_required=True)
#...
```

The `SECRET` is the secret key that you can find in the [Qloud Console Dashboard](https://console.qloud.network),
respectively, in the [DevAuth environment](https://docs.qloud.network/local-development/) it's fixed
to `00000000000000000000000000000000`.

The `credentials_required` parameter is optional and defaults to `True`. If set to `False`, the middleware will
allow requests without a JSON Web Token to access your application (note, invalid or expired JWT will be rejected!).
