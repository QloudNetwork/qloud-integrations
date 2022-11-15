import { LOGOUT_PATH, qloud } from "./lib/qloud";

export const middleware = qloud.middleware({ authRequired: true, logoutUrlPath: LOGOUT_PATH });

export const config = {
  matcher: [
    /*
     * Match all request paths except for the ones starting with:
     * - api/ (they are protected by the apiRoute wrapper)
     * - _next/static
     * - favicon.ico
     */
    "/((?!api/|_next/static|favicon.ico).*)",
  ],
};
