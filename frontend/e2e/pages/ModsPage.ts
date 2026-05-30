import { Page, Locator } from '@playwright/test';

export class ModsPage {
    constructor(private readonly page: Page) {}

    async goto() {
        await this.page.goto('/mods');
    }

    async installMod(modId: number) {
        await this.page.getByTestId('mod-install-input').fill(String(modId));
        await this.page.getByTestId('mod-install-submit').click();
    }

    modRow(id: number): Locator {
        return this.page.getByTestId(`row-${id}`);
    }

    async selectMod(id: number) {
        await this.modRow(id).locator('input[type="checkbox"][data-indeterminate]').check();
    }

    async updateSelected() {
        await this.page.getByTestId('mod-update-btn').click();
    }

    async uninstallSelected() {
        await this.page.getByTestId('mod-uninstall-btn').click();
    }

    async saveAsPreset() {
        await this.page.getByTestId('mod-save-preset-btn').click();
    }

    async waitForModVisible(id: number) {
        await this.modRow(id).waitFor({ state: 'visible', timeout: 15_000 });
    }

    async waitForModGone(id: number) {
        await this.modRow(id).waitFor({ state: 'detached', timeout: 10_000 });
    }

    async waitForModStatus(id: number, status: string) {
        await this.modRow(id).locator(`[title="${status}"]`).waitFor({ state: 'visible', timeout: 20_000 });
    }
}
