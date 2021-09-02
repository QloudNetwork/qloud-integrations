# Integration for Express

[Express](https://expressjs.com/) middleware to use PowerProxy for authentication.

Install the dependency:

```shell
npm install --save powerproxy-express
```

Add the PowerProxy middleware to your Express application:

```javascript
const powerproxy = require("powerproxy-express");

app.use(powerproxy({secret: "insert-powerproxy-secret-here"}));
```

All requests not having a valid token will then automatically be redirected to the PowerProxy login page. For all other
requests the user information will be available via `req.user`:

```javascript
app.get("/", (req, res) => {
    res.json(req.user);
});
```

## Minimal Example

```javascript
const express = require("express");
const powerproxy = require("powerproxy-express");

const port = process.env.PORT || 3000;
const app = express();
app.use(powerproxy({secret: "insert-powerproxy-secret-here"}));

app.get("/", (req, res) => {
    res.json(req.user);
});

app.listen(port);
```
