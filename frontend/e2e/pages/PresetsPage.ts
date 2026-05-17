import { Page, Locator } from '@playwright/test';

export class PresetsPage {
    constructor(private readonly page: Page) {}

    async goto() {
        await this.page.goto('/mods');
        await this.page.getByRole('tab', { name: 'Preset management' }).click();
    }

    presetRow(id: number): Locator {
        return this.page.getByTestId(`preset-row-${id}`);
    }

    editBtn(id: number): Locator {
        return this.page.getByTestId(`preset-edit-${id}`);
    }

    async openMenu(id: number) {
        await this.page.getByTestId(`preset-menu-${id}`).click();
    }

    async deleteAction(id: number) {
        await this.openMenu(id);
        await this.page.getByTestId(`preset-menu-delete-${id}`).click();
    }

    async renameAction(id: number) {
        await this.openMenu(id);
        await this.page.getByTestId(`preset-menu-rename-${id}`).click();
    }

    async exportAction(id: number) {
        await this.openMenu(id);
        await this.page.getByTestId(`preset-menu-download-${id}`).click();
    }

    async importPreset(filePath: string) {
        const fileInput = this.page.getByTestId('preset-import-input');
        await fileInput.setInputFiles(filePath);
        // Dialog opens — wait for Import button and confirm
        await this.page.getByRole('button', {name: /^import$/i}).waitFor({state: 'visible', timeout: 5_000});
        await this.page.getByRole('button', {name: /^import$/i}).click();
    }

    async waitForPresetByName(name: string) {
        await this.page.getByText(name, { exact: true }).first().waitFor({ state: 'visible', timeout: 10_000 });
    }

    async waitForPresetGone(id: number) {
        await this.presetRow(id).waitFor({ state: 'detached', timeout: 10_000 });
    }
}
