const { expect } = require("@playwright/test");
const chance = require("chance").Chance();

exports.AufgabePage = class AufgabePage {
  constructor(page) {
    this.page = page;
  }

  text;

  async goto() {
    await this.page.locator("header").getByRole("button").click();
    await this.page.getByRole("link", { name: "Aufgabe" }).click();
    await expect(this.page).toHaveURL("/aufgabe");
  }

  async create(projekt) {
    const SELECT_PROJEKT = '[aria-label="Projekt"]';
    await this.page
      .locator(SELECT_PROJEKT)
      .selectOption({ label: projekt.name });
    await expect(this.page.getByText("Keine Aufgaben")).toBeVisible();
    this.text = chance.sentence();
    const SELECT_AUFGABE = '[aria-label="Aufgabe"]';
    await this.page.locator(SELECT_AUFGABE).fill(this.text);
    await this.page.locator(SELECT_AUFGABE).press("Enter");
    await expect(this.page.getByText("Keine Aufgaben")).not.toBeVisible();
  }
};
