import type { NextApiResponse } from "next";
import { qloud } from "../../lib/qloud";
import { NextApiRequestWithAuth } from "@qloud/next";

export type UserData = {
  email: string;
};

function handler(
  req: NextApiRequestWithAuth,
  res: NextApiResponse<UserData>
) {
  res.status(200).json({ email: req.auth.email })
}

export default qloud.apiRoute(handler, { authRequired: true });
