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

    exportBtn(id: number): Locator {
        return this.page.getByTestId(`preset-export-${id}`);
    }

    deleteBtn(id: number): Locator {
        return this.page.getByTestId(`preset-delete-${id}`);
    }

    async importPreset(filePath: string) {
        const fileInput = this.page.getByTestId('preset-import-input');
        await fileInput.setInputFiles(filePath);
    }

    async waitForPresetByName(name: string) {
        await this.page.getByText(name, { exact: true }).first().waitFor({ state: 'visible', timeout: 10_000 });
    }

    async waitForPresetGone(id: number) {
        await this.presetRow(id).waitFor({ state: 'detached', timeout: 10_000 });
    }
}
