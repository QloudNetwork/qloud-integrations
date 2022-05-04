const express = require("express");
const request = require("supertest");
const powerproxy = require(".");
const {expiredToken, validToken} = require("./test-utils");
const app = express();

const SECRET = 'ppsecretppsecretppsecretppsecret';
app.use(powerproxy({secret: SECRET}));

app.get("/", (req, res) => {
   res.json(req.user);
});

describe("powerproxy-express", () => {
    it("redirects to /.pp/login if no token is sent", async () => {
        const response = await request(app).get("/");

        expect(response.statusCode).toBe(303);
    });

    it("redirects to /.pp/login if token is invalid", async () => {
        const response = await request(app).get("/").set("Cookie", `__pp__token__=invalid-token`);

        expect(response.statusCode).toBe(303);
    });

    it("redirects to /.pp/login if token has expired", async () => {
        const response = await request(app).get("/").set("Cookie", `__pp__token__=${expiredToken(SECRET)}`);

        expect(response.statusCode).toBe(303);
    });

    it("passes request through to endpoint if token is valid", async () => {
        const response = await request(app).get("/").set("Cookie", `__pp__token__=${validToken(SECRET)}`);

        expect(response.statusCode).toBe(200);
    });
});