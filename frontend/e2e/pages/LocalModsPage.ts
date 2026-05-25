import {Locator, Page} from '@playwright/test';

export class LocalModsPage {
    constructor(private readonly page: Page) {}

    async goto() {
        await this.page.goto('/mods/LOCAL');
    }

    async clickSync() {
        await this.page.getByRole('button', {name: 'Sync local mods'}).click();
    }

    async waitForSyncComplete() {
        // "Syncing..." button detaches when sync finishes; detached is immediate if already done
        await this.page.getByRole('button', {name: 'Syncing...'})
            .waitFor({state: 'detached', timeout: 30_000});
    }

    async waitForModVisible(name: string) {
        await this.page.getByText(name, {exact: true}).waitFor({state: 'visible', timeout: 15_000});
    }

    async waitForModGone(name: string) {
        await this.page.getByText(name, {exact: true}).waitFor({state: 'detached', timeout: 10_000});
    }

    modRow(name: string): Locator {
        return this.page.locator('tr').filter({hasText: name});
    }
}
