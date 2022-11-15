import { GetServerSidePropsContext, NextApiRequest, NextApiResponse } from "next";
import { NextRequest, NextResponse } from "next/server";
import { jwtVerify } from "jose";

export type QloudToken = {
  sub: string;
  aud: string;
  email: string;
  exp: number;
  nbf: number;
  "q:idp": string;
  "q:idp-sub": string;
  "q:udb": string;
};

export type Auth = { auth: QloudToken };
export type AuthOrNull = { auth: QloudToken | null };
export type NextApiRequestWithAuthOrNull = NextApiRequest & AuthOrNull;
export type NextApiRequestWithAuth = NextApiRequest & Auth;
type ApiRouteOptions = {
  authRequired: boolean;
};
type ApiRouteHandler<RouteOptions extends ApiRouteOptions> = (
  req: RouteOptions["authRequired"] extends true ? NextApiRequestWithAuth : NextApiRequestWithAuthOrNull,
  res: NextApiResponse
) => void | Promise<void>;

type MiddlewareOptions = {
  authRequired: boolean;
  logoutUrlPath?: string;
};

const LOQAL_HOST_PATTERN = /^loqal.host(:\d+)?$/i;
const QLOUD_TOKEN_COOKIE = "__q__token__";

function getHost(request: NextRequest): string | null {
  const headers = request.headers;
  return headers.get("x-forwarded-host") || headers.get("host");
}

function getQloudBaseUrl(request: NextRequest): string {
  const host = getHost(request);
  return !host || LOQAL_HOST_PATTERN.test(host) ? "https://login.loqal.host" : `https://${host}/.q`;
}

function getLoginUrl(request: NextRequest): string {
  return `${getQloudBaseUrl(request)}/`;
}

function getLogoutUrl(request: NextRequest): string {
  return `${getQloudBaseUrl(request)}/logout${request.nextUrl.search}`;
}

function escapeRegExp(regex: string): string {
  return regex.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

interface QloudConfig {
  secret: string;
}

export class Qloud {
  private readonly secret: string;

  constructor({ secret }: QloudConfig) {
    this.secret = secret;
  }

  middleware = ({ authRequired = false, logoutUrlPath = "/logout" }: MiddlewareOptions = { authRequired: false }) => {
    const logoutUrlPathRegex = new RegExp(`^${escapeRegExp(logoutUrlPath)}$`, "i");
    return async (request: NextRequest): Promise<NextResponse> => {
      if (logoutUrlPathRegex.test(request.nextUrl.pathname)) {
        return NextResponse.redirect(getLogoutUrl(request));
      }

      if (!authRequired) {
        return NextResponse.next();
      } else {
        const token = await this.verifyToken(request.cookies.get(QLOUD_TOKEN_COOKIE)?.value);
        if (token) {
          return NextResponse.next();
        } else {
          return NextResponse.redirect(getLoginUrl(request));
        }
      }
    };
  };

  getServerSideProps: (context: GetServerSidePropsContext) => Promise<{ props: AuthOrNull }> = async (
    context
  ): Promise<{ props: AuthOrNull }> => {
    const token = await this.verifyToken(context.req.cookies[QLOUD_TOKEN_COOKIE]);
    return { props: { auth: token } };
  };

  apiRoute = <RouteOptions extends ApiRouteOptions>(
    handler: ApiRouteHandler<RouteOptions>,
    options?: RouteOptions
  ): ((req: NextApiRequest, res: NextApiResponse) => Promise<void>) => {
    return async (req, res): Promise<void> => {
      const token = await this.verifyToken(req.cookies[QLOUD_TOKEN_COOKIE]);
      if (!token && options?.authRequired) {
        return res.status(401).json({ message: "Unauthorized" });
      }

      Object.defineProperty(req, "auth", {
        value: token,
        enumerable: true,
        writable: false,
        configurable: false,
      });
      return handler(req as Parameters<ApiRouteHandler<RouteOptions>>[0], res);
    };
  };

  verifyToken = async (token: string | undefined | null): Promise<QloudToken | null> => {
    if (!token) return null;
    try {
      const result = await jwtVerify(token, new TextEncoder().encode(this.secret));
      return result.payload as QloudToken;
    } catch (error) {
      return null;
    }
  };
}
