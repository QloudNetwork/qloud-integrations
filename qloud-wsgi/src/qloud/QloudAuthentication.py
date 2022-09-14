from werkzeug.wrappers import Request
from werkzeug.utils import redirect
import jwt


class QloudAuthentication:
    def __init__(self, app, secret, credentials_required=True):
        self.app = app
        self.secret = secret
        self.credentialsRequired = credentials_required

    def _get_qloud_jwt(self, environ):
        request = Request(environ)
        return request.cookies.get("__q__token__")

    def __call__(self, environ, start_response):
        qloud_jwt = self._get_qloud_jwt(environ)
        if qloud_jwt:
            # Credentials are present
            try:
                environ["auth"] = jwt.decode(
                    qloud_jwt,
                    self.secret,
                    algorithms=["HS256"],
                    options={"verify_aud": False},
                )
                return self.app(environ, start_response)
            except jwt.exceptions.InvalidTokenError:
                # Credentials are invalid (fall through to redirect
                pass
        elif not self.credentialsRequired:
            # No credentials required, none given
            return self.app(environ, start_response)

        # Credentials required, none given (or invalid)
        res = redirect("/.q/login", code=303)
        return res(environ, start_response)
