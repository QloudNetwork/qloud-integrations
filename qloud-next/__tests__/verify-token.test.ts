import "./util/fetch-polyfill";
import { Qloud } from "../src";
import { createJwt, SECRET, SECRET_1, SECRET_2 } from "./util/jwt";
import { decodeJwt } from "jose";

describe("verify-token", () => {
  test("returns decoded token if token is valid", async () => {
    const verifyToken = new Qloud({ secret: SECRET }).verifyToken;
    const token = await createJwt(SECRET);

    expect(await verifyToken(token)).toStrictEqual(decodeJwt(token));
  });

  test("returns null if token is signed with wrong secret", async () => {
    const verifyToken = new Qloud({ secret: SECRET_1 }).verifyToken;
    const token = await createJwt(SECRET_2);

    expect(await verifyToken(token)).toBeNull();
  });

  test.each([null, undefined])("returns null if token is null or undefined", async (token: null | undefined) => {
    const verifyToken = new Qloud({ secret: SECRET }).verifyToken;

    expect(await verifyToken(token)).toBeNull();
  });
});
