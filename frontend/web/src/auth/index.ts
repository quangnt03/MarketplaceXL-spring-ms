import type { AuthOptions } from "next-auth";
import { cognitoProvider } from "./cognito-provider";

export const authOptions: AuthOptions = {
  providers: [cognitoProvider],
  session: {
    strategy: "jwt",
  },
};
