import { NextRequest } from "next/server";
import { serverEnv } from "@/lib/env/server";

export async function backendFetch(path: string, request: NextRequest) {
  const url = new URL(path, serverEnv.backendBaseUrl);
  url.search = request.nextUrl.search;

  return fetch(url, {
    method: request.method,
    headers: request.headers,
    body: request.method === "GET" || request.method === "HEAD" ? undefined : await request.text(),
  });
}
