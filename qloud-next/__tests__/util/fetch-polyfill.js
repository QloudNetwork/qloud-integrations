const { fetch, Headers, Request, Response } = require("node-fetch");

if (!globalThis.fetch) {
  globalThis.fetch = fetch;
  globalThis.Headers = Headers;
  // Remove when https://github.com/vercel/next.js/issues/42374 is fixed
  globalThis.Headers.prototype.getAll = () => []
  globalThis.Request = Request;
  globalThis.Response = Response;
}
