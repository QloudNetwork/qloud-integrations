# Integration for Express

[Express](https://expressjs.com/) middleware to use Qloud for authentication.

Install the dependency:

```shell
npm install --save @semanticlabsgmbh/qloud-express
```

Add the Qloud middleware to your Express application:

```javascript
const qloud = require("qloud-express");

app.use(qloud({secret: "insert-qloud-secret-here"}));
```

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

## Minimal Example

```javascript
const express = require("express");
const qloud = require("@semanticlabsgmbh/qloud-express");

const port = process.env.PORT || 3000;
const app = express();
app.use(qloud({secret: "insert-qloud-secret-here"}));

app.get("/", (req, res) => {
    res.json(req.auth);
});

app.listen(port);
```
