import { test, expect } from "@playwright/test";

test("public home renders", async ({ page }) => {
  await page.goto("/");
  await expect(page.getByText("marketplace Platform")).toBeVisible();
});
