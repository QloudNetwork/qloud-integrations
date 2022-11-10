import "./util/fetch-polyfill";
import { NextQloud } from "../src";
import { createJwt, SECRET, SECRET_1, SECRET_2 } from "./util/jwt";
import { GetServerSidePropsContext } from "next";
import { decodeJwt } from "jose";

describe("server-side-props", () => {
  test("returns auth prop set to decoded token if token is valid", async () => {
    const getServerSideProps = new NextQloud({ secret: SECRET }).getServerSideProps;
    const token = await createJwt(SECRET);
    const context: unknown = { req: { cookies: { __q__token__: token } } };

    const { props } = await getServerSideProps(context as GetServerSidePropsContext);

    expect(props.auth).toStrictEqual(decodeJwt(token));
  });

  test("returns auth prop set to null if token is missing", async () => {
    const getServerSideProps = new NextQloud({ secret: SECRET }).getServerSideProps;
    const context: unknown = { req: { cookies: {} } };

    const { props } = await getServerSideProps(context as GetServerSidePropsContext);

    expect(props.auth).toBeNull();
  });

  test("returns auth prop set to null if token is signed with wrong secret", async () => {
    const getServerSideProps = new NextQloud({ secret: SECRET_1 }).getServerSideProps;
    const token = await createJwt(SECRET_2);
    const context: unknown = { req: { cookies: { __q__token__: token } } };

    const { props } = await getServerSideProps(context as GetServerSidePropsContext);

    expect(props.auth).toBeNull();
  });
});
