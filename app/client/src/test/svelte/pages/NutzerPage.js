import { expect } from "@playwright/test";

import Chance from "chance";
const chance = new Chance();

export class NutzerPage {
  constructor(page) {
    this.page = page;
  }

  nick;
  name;
  mail;
  sprache;

  async goto() {
    await this.page.locator("header").getByRole("button").click();
    await this.page.getByRole("link", { name: "Nutzer" }).click();
    await expect(this.page).toHaveURL("/nutzer");
  }

  async create() {
    await this.page.locator("th").getByRole("button").getByText("add").click();
    this.nick = "Nu" + chance.word({ length: 5 });
    this.name = [this.nick, chance.name()].join(" ");
    const SELECT_NAME = '[aria-label="Name"]';
    await this.page.locator(SELECT_NAME).fill(this.name);
    await this.page.locator(SELECT_NAME).press("Enter");
    this.mail = chance.email();
    const SELECT_MAIL = '[aria-label="E-Mail"]';
    await this.page.locator(SELECT_MAIL).fill(this.mail);
    await this.page.locator(SELECT_MAIL).press("Enter");
    this.sprache = "DE";
    const SELECT_SPRACHE = '[aria-label="Sprachen"]';
    await this.page
      .locator(SELECT_SPRACHE)
      .selectOption({ value: this.sprache });
    await this.page
      .getByRole("cell")
      .getByRole("button")
      .getByText("Ok")
      .click();
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
    this.name = [this.nick, chance.name()].join(" ");
    const SELECT_NAME = '[aria-label="Name"]';
    await this.page.locator(SELECT_NAME).fill(this.name);
    await this.page.locator(SELECT_NAME).press("Enter");
    await this.page
      .getByRole("cell")
      .getByRole("button")
      .getByText("Ok")
      .click();
  }

  async updateMail() {
    await this.page
      .getByRole("row", { name: this.name })
      .getByRole("button")
      .getByText("edit")
      .click();
    this.mail = chance.email();
    const SELECT_MAIL = '[aria-label="E-Mail"]';
    await this.page.locator(SELECT_MAIL).fill(this.mail);
    await this.page.locator(SELECT_MAIL).press("Enter");
    await this.page
      .getByRole("cell")
      .getByRole("button")
      .getByText("Ok")
      .click();
  }

  async updateSprache() {
    await this.page
      .getByRole("row", { name: this.name })
      .getByRole("button")
      .getByText("edit")
      .click();
    this.sprache = "EN";
    const SELECT_SPRACHE = '[aria-label="Sprachen"]';
    await this.page
      .locator(SELECT_SPRACHE)
      .selectOption({ value: this.sprache });
    await this.page
      .getByRole("cell")
      .getByRole("button")
      .getByText("Ok")
      .click();
  }

  async delete() {
    const SELECT_FILTER = '[aria-label="Filter"]';
    await this.page.locator(SELECT_FILTER).fill(this.nick);
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
    await this.page
      .getByRole("cell")
      .getByRole("button")
      .getByText("Löschen")
      .click();
    await this.page.locator(SELECT_FILTER).fill("");
    await this.page.locator(SELECT_FILTER).press("Enter");
  }
}
