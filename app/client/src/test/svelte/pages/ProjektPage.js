import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class ProjektPage {
  constructor(page) {
    this.page = page;
  }

  name;
  sprache;

  async goto() {
    await this.page.locator("header").getByRole("button").click();
    await this.page.getByRole("link", { name: "Projekt" }).click();
    await expect(this.page).toHaveURL("/projekt");
  }

  async create() {
    await this.page.locator("th").getByRole("button").getByText("add").click();
    this.name = "Pa" + chance.word({ length: 5 });
    const SELECT_NAME = '[aria-label="Name"]';
    await this.page.locator(SELECT_NAME).fill(this.name);
    await this.page.locator(SELECT_NAME).press("Enter");
    this.sprache = "DE";
    const SELECT_SPRACHE = '[aria-label="Sprache"]';
    await this.page
      .locator(SELECT_SPRACHE)
      .selectOption({ value: this.sprache });
    const SELECT_BESITZER = '[aria-label="Besitzer"]';
    await this.page.locator(SELECT_BESITZER).selectOption({ index: 0 });
    await this.page.getByRole("button", { name: "Ok" }).click();
  }

  async filter() {
    const SELECT_FILTER = '[aria-label="Filter"]';
    await this.page.locator(SELECT_FILTER).fill(this.name);
    await this.page.locator(SELECT_FILTER).press("Enter");
    const allRow = await this.page.locator("table tr").allTextContents();
    await expect(allRow).toHaveLength(2);
    await this.page.locator(SELECT_FILTER).fill("");
    await this.page.locator(SELECT_FILTER).press("Enter");
  }

  async updateName() {
    await this.page
      .getByRole("row", { name: this.name })
      .getByRole("button")
      .getByText("edit")
      .click();
    this.name = "Pe" + chance.word({ length: 5 });
    const SELECT_NAME = '[aria-label="Name"]';
    await this.page.locator(SELECT_NAME).fill(this.name);
    await this.page.locator(SELECT_NAME).press("Enter");
    await this.page.getByRole("button", { name: "Ok" }).click();
  }

  async updateSprache() {
    await this.page
      .getByRole("row", { name: this.name })
      .getByRole("button")
      .getByText("edit")
      .click();
    this.sprache = "EN";
    const SELECT_SPRACHE = '[aria-label="Sprache"]';
    await this.page
      .locator(SELECT_SPRACHE)
      .selectOption({ value: this.sprache });
    await this.page.getByRole("button", { name: "Ok" }).click();
  }

  async updateTeam() {
    await this.page
      .getByRole("row", { name: this.name })
      .getByRole("button")
      .getByText("edit")
      .click();
    await this.page
      .locator("td")
      .getByRole("combobox", { name: "Neues Mitglied" })
      .selectOption({ index: 1 });
    await this.page.locator("td").getByRole("button").getByText("add").click();
    await this.page
      .locator("td")
      .getByRole("button")
      .getByText("delete")
      .click();
    await this.page.getByRole("button", { name: "Ok" }).click();
  }

  async delete() {
    const SELECT_FILTER = '[aria-label="Filter"]';
    await this.page.locator(SELECT_FILTER).fill(this.name);
    await this.page.locator(SELECT_FILTER).press("Enter");
    await this.page
      .getByRole("row", { name: this.name })
      .getByRole("checkbox")
      .uncheck();
    await this.page
      .getByRole("row", { name: this.name })
      .getByRole("button")
      .getByText("edit")
      .click();
    this.page.once("dialog", (dialog) => dialog.accept());
    await this.page.getByRole("button", { name: "Löschen" }).click();
    await this.page.locator(SELECT_FILTER).fill("");
    await this.page.locator(SELECT_FILTER).press("Enter");
  }
}
