import { Page, Locator } from '@playwright/test';

export class ScenariosPage {
    constructor(private readonly page: Page) {}

    async goto() {
        await this.page.goto('/scenarios');
    }

    async uploadFile(filePath: string) {
        const [fileChooser] = await Promise.all([
            this.page.waitForEvent('filechooser'),
            this.page.getByTestId('scenario-upload-btn').click(),
        ]);
        await fileChooser.setFiles(filePath);
    }

    scenarioRow(name: string): Locator {
        return this.page.getByTestId(`row-${name}`);
    }

    async selectScenario(name: string) {
        await this.scenarioRow(name).locator('input[type="checkbox"]').check();
    }

    async deleteSelected() {
        await this.page.getByTestId('scenario-delete-btn').click();
    }

    async waitForScenario(name: string) {
        await this.page.getByText(name, { exact: true }).waitFor({ state: 'visible', timeout: 10_000 });
    }

    async waitForScenarioGone(name: string) {
        await this.page.getByText(name, { exact: true }).waitFor({ state: 'detached', timeout: 10_000 });
    }
}
