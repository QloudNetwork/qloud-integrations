import { Qloud } from '@qloud/next';

const DEVELOPMENT_SECRET = "00000000000000000000000000000000";
const SECRET = process.env.QLOUD_SECRET || DEVELOPMENT_SECRET;

export const LOGOUT_PATH = "/logout";
export const qloud = new Qloud({ secret: SECRET });
