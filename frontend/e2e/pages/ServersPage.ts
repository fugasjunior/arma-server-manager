import { Page, Locator } from '@playwright/test';

export class ServersPage {
    constructor(private readonly page: Page) {}

    async goto() {
        await this.page.goto('/servers');
    }

    serverRow(id: number): Locator {
        return this.page.locator(`#server-${id}-list-entry`);
    }

    startBtn(id: number): Locator {
        return this.page.locator(`#server-${id}-start-btn`);
    }

    stopBtn(id: number): Locator {
        return this.page.locator(`#server-${id}-stop-btn`);
    }

    settingsBtn(id: number): Locator {
        return this.page.locator(`#server-${id}-settings-btn`);
    }

    deleteBtn(id: number): Locator {
        return this.page.locator(`#server-${id}-delete-btn`);
    }

    async clickDeleteAndConfirm(id: number) {
        await this.deleteBtn(id).click();
        await this.page.locator('#dialog-confirm-btn').click();
    }

    async waitForServerRunning(id: number) {
        await this.stopBtn(id).waitFor({ state: 'visible', timeout: 20_000 });
    }

    async waitForServerStopped(id: number) {
        await this.startBtn(id).waitFor({ state: 'visible', timeout: 20_000 });
    }
}
