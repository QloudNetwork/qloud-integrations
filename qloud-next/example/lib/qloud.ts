import { NextQloud } from 'qloud-next';

const DEVELOPMENT_SECRET = "00000000000000000000000000000000";
const SECRET = process.env.QLOUD_SECRET || DEVELOPMENT_SECRET;

export const LOGOUT_PATH = "/logout";
export const nextQloud = new NextQloud({ secret: SECRET });
