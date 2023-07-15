import { test } from "@playwright/test";
import { AufgabePage } from "./pages/AufgabePage.js";
import { NutzerPage } from "./pages/NutzerPage.js";
import { ProjektPage } from "./pages/ProjektPage.js";

test.describe("Regression", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("/");
  });

  test("Nutzer", async ({ page }, testInfo) => {
    const nutzer = new NutzerPage(page);
    await nutzer.goto();
    await nutzer.create();
    await nutzer.filter();
    await nutzer.updateName();
    await nutzer.updateMail();
    await nutzer.updateSprache();
    await nutzer.delete();
  });

  test("Projekt", async ({ page }, testInfo) => {
    const projekt = new ProjektPage(page);
    await projekt.goto();
    await projekt.create();
    await projekt.filter();
    await projekt.updateName();
    await projekt.updateSprache();
    await projekt.updateTeam();
    await projekt.delete();
  });

  test("Aufgabe", async ({ page }, testInfo) => {
    const projekt = new ProjektPage(page);
    const aufgabe = new AufgabePage(page);
    await projekt.goto();
    await projekt.create();
    await aufgabe.goto();
    await aufgabe.create(projekt);
    await projekt.goto();
    await projekt.delete();
  });
});
