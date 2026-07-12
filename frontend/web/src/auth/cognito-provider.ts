import CognitoProvider from "next-auth/providers/cognito";

export const cognitoProvider = CognitoProvider({
  clientId: process.env.COGNITO_CLIENT_ID ?? "local-client",
  clientSecret: process.env.COGNITO_CLIENT_SECRET ?? "local-secret",
  issuer: process.env.COGNITO_ISSUER,
});
