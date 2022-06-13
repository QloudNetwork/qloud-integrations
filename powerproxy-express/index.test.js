const express = require("express");
const request = require("supertest");
const powerproxy = require(".");
const {expiredToken, validToken} = require("./test-utils");
const jwt = require('jsonwebtoken');

const SECRET = 'ppsecretppsecretppsecretppsecret';

function createApp({ credentialsRequired }) {
    const app = express();
    app.use(powerproxy({secret: SECRET, credentialsRequired}));

    app.get("/", (req, res) => {
        res.json(req.auth);
    });
    return app;
}

const appRequiringCredentials = createApp({ credentialsRequired: true });
const appNotRequiringCredentials = createApp({ credentialsRequired: false });

describe("powerproxy-express", () => {
    it("redirects to /.pp/login if no token is sent and app requires credentials", async () => {
        const response = await request(appRequiringCredentials).get("/");

        expect(response.statusCode).toBe(303);
        expect(response.headers["location"]).toEqual("/.pp/login");
    });

    it.each([appRequiringCredentials, appNotRequiringCredentials])("redirects to /.pp/login if token is invalid and app requires credentials", async (app) => {
        const response = await request(app).get("/").set("Cookie", `__pp__token__=invalid-token`);

        expect(response.statusCode).toBe(303);
        expect(response.headers["location"]).toEqual("/.pp/login");
    });

    it.each([appRequiringCredentials, appNotRequiringCredentials])("redirects to /.pp/login if token has expired", async (app) => {
        const response = await request(app).get("/").set("Cookie", `__pp__token__=${expiredToken(SECRET)}`);

        expect(response.statusCode).toBe(303);
        expect(response.headers["location"]).toEqual("/.pp/login");
    });

    it("passes request without token through to endpoint if app does not require credentials", async () => {
        const response = await request(appNotRequiringCredentials).get("/");

        expect(response.statusCode).toBe(200);
    });

    it.each([appRequiringCredentials, appNotRequiringCredentials])("passes request through to endpoint if token is valid", async (app) => {
        const response = await request(app).get("/").set("Cookie", `__pp__token__=${validToken(SECRET)}`);

        expect(response.statusCode).toBe(200);
        expect(response.body).toEqual(jwt.decode(validToken(SECRET)));
    });
});
