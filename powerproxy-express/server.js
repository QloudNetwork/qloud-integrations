const express = require("express");
const powerproxy = require(".");

const port = process.env.PORT || 3000;
const app = express();
app.use(powerproxy({secret: "ppsecretppsecretppsecretppsecret"}));

app.get("/", (req, res) => {
    res.json(req.user);
});

app.listen(port, () => {
    console.log(`Server listening at http://localhost:${port}`)
});
