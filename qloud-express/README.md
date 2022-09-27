# Qloud Integration for Express

[Express](https://expressjs.com/) middleware to use Qloud for authentication.

Install the dependency:

```shell
npm install --save @qloud/express
```

Add the Qloud middleware to your Express application:

```javascript
const qloud = require("@qloud/express");

app.use(qloud({secret: "insert-qloud-secret-here"}));
```

The `secret` is the secret key that you can find in the [Qloud Console Dashboard](https://console.qloud.network),
respectively, in the [DevAuth environment](https://docs.qloud.network/local-development/) it's fixed
to `00000000000000000000000000000000`.

If the user is authenticated, the data from the [JWT token](https://docs.qloud.network/architecture/jwt) will be
available in the `auth` property of the request:

```javascript
app.get("/", (req, res) => {
    res.json(req.auth);
});
```

Requests with an invalid or expired token will be redirected to the login page. If a request does not contain a token,
`req.auth` is undefined. You can enable the configuration option `credentialsRequired` if you also want to redirect
requests without a token to the login page:

```javascript
app.use(qloud({secret: "insert-qloud-secret-here", credentialsRequired: true}));
```

This option is set to `false` by default to support applications with optional authentication (see
[authentication modes](https://docs.qloud.network/configuration/authentication-mode)).

If your application uses [mandatory authentication](https://docs.qloud.network/configuration/authentication-mode), we
recommend to set `credentialsRequired` to `true`, the integration itself will then also reject unauthenticated requests
if they bypass the Proxy.

## Minimal Example

```javascript
const express = require("express");
const qloud = require("@qloud/express");

const port = process.env.PORT || 3000;
const app = express();
app.use(qloud({secret: "insert-qloud-secret-here"}));

app.get("/", (req, res) => {
    res.json(req.auth);
});

app.listen(port);
```
