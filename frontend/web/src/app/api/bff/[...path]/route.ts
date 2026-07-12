import { NextRequest } from "next/server";
import { backendFetch } from "@/lib/api/server";

type BffRouteContext = {
  params: Promise<{ path: string[] }>;
};

async function proxy(request: NextRequest, { params }: BffRouteContext) {
  const { path: pathParts } = await params;
  const path = pathParts.join("/");
  return backendFetch(path, request);
}

export { proxy as DELETE, proxy as GET, proxy as PATCH, proxy as POST, proxy as PUT };
