import "./util/fetch-polyfill";
import { NextApiRequest, NextApiResponse } from "next";
import { createMocks } from "node-mocks-http";
import { Qloud } from "../src";
import { createJwt, SECRET, SECRET_1, SECRET_2 } from "./util/jwt";
import { decodeJwt } from "jose";

// eslint-disable-next-line @typescript-eslint/no-empty-function
const ANY_ROUTE_HANDLER = () => {};

describe("api-route", () => {
  test("calls handler with token as auth property on reqest if token is valid", async () => {
    const qloud = new Qloud({ secret: SECRET });
    const token = await createJwt(SECRET);
    const { req, res } = createMocks<NextApiRequest, NextApiResponse>({
      method: "GET",
      cookies: { __q__token__: token },
    });
    const apiRoute = qloud.apiRoute((req, res) => {
      res.status(200).json({ auth: req.auth });
    });

    await apiRoute(req, res);

    expect(res.statusCode).toBe(200);
    expect(res._getJSONData()).toStrictEqual({ auth: decodeJwt(token) });
  });

  test("calls handler with auth property set to null if token is missing", async () => {
    const qloud = new Qloud({ secret: SECRET });
    const { req, res } = createMocks<NextApiRequest, NextApiResponse>({ method: "GET" });
    const apiRoute = qloud.apiRoute((req, res) => {
      res.status(200).json({ auth: req.auth });
    });

    await apiRoute(req, res);

    expect(res.statusCode).toBe(200);
    expect(res._getJSONData()).toStrictEqual({ auth: null });
  });

  test("calls handler with auth property set to null if token is signed with wrong secret", async () => {
    const qloud = new Qloud({ secret: SECRET_1 });
    const token = await createJwt(SECRET_2);
    const { req, res } = createMocks<NextApiRequest, NextApiResponse>({
      method: "GET",
      headers: { cookie: `__q__token__=${token}` },
    });
    const apiRoute = qloud.apiRoute((req, res) => {
      res.status(200).json({ auth: req.auth });
    });

    await apiRoute(req, res);

    expect(res.statusCode).toBe(200);
    expect(res._getJSONData()).toStrictEqual({ auth: null });
  });

  test("responds with status 401 if token is signed with wrong secret and authRequired=true", async () => {
    const qloud = new Qloud({ secret: SECRET_1 });
    const token = await createJwt(SECRET_2);
    const { req, res } = createMocks<NextApiRequest, NextApiResponse>({
      method: "GET",
      cookies: { __q__token__: token },
    });
    const apiRoute = qloud.apiRoute(ANY_ROUTE_HANDLER, { authRequired: true });

    await apiRoute(req, res);

    expect(res.statusCode).toBe(401);
    expect(res._getJSONData()).toStrictEqual({ message: "Unauthorized" });
  });

  test("responds with status 401 if token is missing and authRequired=true", async () => {
    const qloud = new Qloud({ secret: SECRET });
    const { req, res } = createMocks<NextApiRequest, NextApiResponse>({ method: "GET" });
    const apiRoute = qloud.apiRoute(ANY_ROUTE_HANDLER, { authRequired: true });

    await apiRoute(req, res);

    expect(res.statusCode).toBe(401);
    expect(res._getJSONData()).toStrictEqual({ message: "Unauthorized" });
  });
});
