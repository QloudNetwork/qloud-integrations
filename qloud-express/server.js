const express = require("express");
const qloud = require(".");

const port = process.env.PORT || 3000;
const app = express();
app.use(qloud({secret: "ppsecretppsecretppsecretppsecret"}));

app.get("/", (req, res) => {
    res.json(req.auth);
});

app.listen(port, () => {
    console.log(`Server listening at http://localhost:${port}`)
});
