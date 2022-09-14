def test_unauthenticated_request_redirects_to_login(app_mandatory_auth):
    client = app_mandatory_auth.test_client()
    response = client.get("/")
    assert response.status_code == 303
    assert response.headers["Location"] == "/.q/login"


def test_authenticated_request_is_ok(app_mandatory_auth, valid_jwt):
    client = app_mandatory_auth.test_client()
    client.set_cookie("localhost", "__q__token__", valid_jwt)
    response = client.get("/")
    assert response.status_code == 200
    assert response.data == b"OK"


def test_expired_token_redirects_to_login(app_mandatory_auth, expired_jwt):
    client = app_mandatory_auth.test_client()
    client.set_cookie("localhost", "__q__token__", expired_jwt)
    response = client.get("/")
    assert response.status_code == 303
    assert response.headers["Location"] == "/.q/login"


def test_invalid_token_redirects_to_login(app_mandatory_auth):
    client = app_mandatory_auth.test_client()
    client.set_cookie("localhost", "__q__token__", "invalid.token")
    response = client.get("/")
    assert response.status_code == 303
    assert response.headers["Location"] == "/.q/login"
