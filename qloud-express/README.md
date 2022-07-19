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

All requests not having a valid token will then automatically be redirected to the Qloud login page. For all other
requests the user information will be available via `req.auth`:

```javascript
app.get("/", (req, res) => {
    res.json(req.auth);
});
```

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
