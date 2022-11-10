import { SignJWT } from "jose";

export const SECRET = "00000000000000000000000000000000";
export const SECRET_1 = "00000000000000000000000000000001";
export const SECRET_2 = "00000000000000000000000000000002";

export async function createJwt(secret: string): Promise<string> {
  return await new SignJWT({
    email: "user@example.com",
    "q:idp": "EMAIL",
    "q:idp-sub": "user@example.com",
    "q:udb": "1d4f4a83-44ba-4373-bae4-ae5df2026bde",
  })
    .setSubject("102af90e-76e7-4b0d-b69a-af449775668b")
    .setProtectedHeader({ alg: "HS256" })
    .setAudience("example.qloud.space")
    .setIssuedAt()
    .setExpirationTime("2h")
    .sign(new TextEncoder().encode(secret));
}
