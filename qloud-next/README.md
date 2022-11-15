# Qloud Integration for Next.js

Integration to use [Qloud](https://qloud.network) for authentication with [Next.js](https://nextjs.org) >=13.

## Installation

```shell
npm install --save @qloud/next
```

## Example Application

This repository contains a minimal [example application](./example/) demonstrating how to use the library.

## Usage

Instantiate an instance of `Qloud` by providing the site `secret`:

```typescript
import { Qloud } from "@qloud/next";

const DEVELOPMENT_SECRET = "00000000000000000000000000000000";
const SECRET = process.env.QLOUD_SECRET || DEVELOPMENT_SECRET;
const qloud = new Qloud({secret: SECRET});
```

The development secret is supposed to be used for local development with https://login.loqal.host. In a production
enviroment, inject the secret of your site via an environment variable.

### Middleware

The [middleware](https://nextjs.org/docs/advanced-features/middleware) redirects unauthenticated requests to Qloud's
login page if `authRequired` is set to `true`. This is the recommended setting if you are
using [required authentication](https://docs.qloud.network/configuration/authentication-mode) for your site. If you
are using [optional authentication](https://docs.qloud.network/configuration/authentication-mode), you should set this
option to `false`.

The middleware also registers a `logoutUrlPath` (by default `/logout`). You can link there to log out an authenticated
user.

It also takes care for you to redirect to https://login.loqal.host for local development.

```typescript
export const middleware = qloud.middleware({ authRequired: true, logoutUrlPath: "/logout" });

export const config = {
  // It might make sense to exclude certain URL paths.
  // For example, the API routes can have their own protection returning a JSON response.
  matcher: ["/((?!api/|_next/static|favicon.ico).*)"],
};
```

### API routes

Wrap your API route handlers with `apiRoute` function. You can then access the content of the JSON Web Token (JWT) via
the `auth` property on the request:

```typescript
function handler(req: NextApiRequestWithAuth, res: NextApiResponse<UserData>) {
  res.status(200).json({ email: req.auth });
}

export default qloud.apiRoute(handler, { authRequired: true });
```

If `authRequired` is set to `true`, the API will return 401 with `{ message: "Unauthorized }` as the response body for
unauthenticated requests. If `authRequired` is set to `false`, the handler will be called with `auth` set to `null`.

### Server-side props

For server-side rendering, you can use the `getServerSideProps` helper function. It returns an `auth` prop that is
either the `QloudToken` if the request has a valid token or `null` otherwise.

```typescript jsx
import { QloudToken } from "@qloud/next";

const Home: NextPage = function Home({auth}: { auth: QloudToken | null }) {
  const token = JSON.stringify(auth);
  return <pre>{token}</pre>;
};

export const getServerSideProps: GetServerSideProps<HomeProps> = async (context) => {
  return qloud.getServerSideProps(context);
};
```

### Verify Token

If you only want to verify the token yourself without using any of the helper functions above, you can just
call `verifyJwt` with the serialized JWT:

```typescript
const token: QloudToken | null = await qloud.verifyToken("<jwt>");
```
