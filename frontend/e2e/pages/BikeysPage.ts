import { Page, Locator } from '@playwright/test';

export class BikeysPage {
    constructor(private readonly page: Page) {}

    async openForServer(serverId: number) {
        await this.page.goto('/servers');
        await this.page.getByTestId(`server-${serverId}-expand-btn`).click();
        await this.page.getByRole('button', {name: 'Bikeys'}).click();
    }

    async uploadFile(filePath: string) {
        const [fileChooser] = await Promise.all([
            this.page.waitForEvent('filechooser'),
            this.page.getByTestId('key-upload-btn').click(),
        ]);
        await fileChooser.setFiles(filePath);
    }

    keyRow(name: string): Locator {
        return this.page.getByTestId(`row-${name}`);
    }

    async selectKey(name: string) {
        await this.keyRow(name).locator('input[type="checkbox"]').check();
    }

    async deleteSelected() {
        await this.page.getByTestId('key-delete-btn').click();
    }

    async waitForKey(name: string) {
        await this.page.getByText(name, {exact: true}).waitFor({state: 'visible', timeout: 10_000});
    }

    async waitForKeyGone(name: string) {
        await this.page.getByText(name, {exact: true}).waitFor({state: 'detached', timeout: 10_000});
    }
}
