import "./util/fetch-polyfill";
import { Qloud } from "../src";
import { NextRequest, NextResponse } from "next/server";
import { createJwt, SECRET, SECRET_1, SECRET_2 } from "./util/jwt";

describe("middleware", () => {
  test("does not redirect if token is valid", async () => {
    const middleware = new Qloud({ secret: SECRET }).middleware({ authRequired: true });
    const token = await createJwt(SECRET);
    const request = new NextRequest("https://example.qloud.space/any-url-path", {
      headers: {
        "x-forwarded-host": "example.qloud.space",
        cookie: `__q__token__=${token}`,
      },
    });

    const response = await middleware(request);

    expect(response).toStrictEqual(NextResponse.next());
  });

  test("redirects to login URL if token is missing and authRequired=true", async () => {
    const middleware = new Qloud({ secret: SECRET }).middleware({ authRequired: true });
    const request = new NextRequest("https://example.qloud.space/any-url-path", {
      headers: { "x-forwarded-host": "example.qloud.space" },
    });

    const response = await middleware(request);

    expect(response.status).toBe(307);
    expect(response.headers.get("location")).toEqual("https://example.qloud.space/.q/");
  });

  test("redirects to login URL if token is signed with wrong secret and authRequired=true", async () => {
    const middleware = new Qloud({ secret: SECRET_1 }).middleware({ authRequired: true });
    const token = await createJwt(SECRET_2);
    const request = new NextRequest("https://example.qloud.space/any-url-path", {
      headers: {
        "x-forwarded-host": "example.qloud.space",
        cookie: `__q__token__=${token}`,
      },
    });

    const response = await middleware(request);

    expect(response.status).toBe(307);
    expect(response.headers.get("location")).toEqual("https://example.qloud.space/.q/");
  });

  test("does not redirect to login URL if token is missing if authRequired=false", async () => {
    const middleware = new Qloud({ secret: SECRET_1 }).middleware({ authRequired: false });
    const request = new NextRequest("https://example.qloud.space/any-url-path");

    const response = await middleware(request);

    expect(response.status).toBe(200);
  });

  test.each([
    ["/logout", "/logout", ""],
    ["/logout", "/logout", "?return_path=%2Fgoodbye"],
    ["/auth/logout", "/auth/logout", ""],
  ])(
    "redirects to logout URL if request url path is equal to logoutUrlPath",
    async (logoutUrlPath, requestUrlPath, requestQueryString) => {
      const middleware = new Qloud({ secret: SECRET }).middleware({ authRequired: false, logoutUrlPath });
      const request = new NextRequest(`https://app.example.com${requestUrlPath}${requestQueryString}`, {
        headers: { "x-forwarded-host": "example.qloud.space" },
      });

      const response = await middleware(request);

      expect(response.status).toBe(307);
      expect(response.headers.get("location")).toEqual(`https://example.qloud.space/.q/logout${requestQueryString}`);
    }
  );
});
