import { Page } from '@playwright/test';

export class NewServerPage {
    constructor(private readonly page: Page) {}

    async goto(type: 'ARMA3' | 'DAYZ' | 'DAYZ_EXP' | 'REFORGER') {
        await this.page.goto(`/servers/new/${type}`);
    }

    async fillBasicFields(name: string, port: number) {
        await this.page.locator('#name').fill(name);
        await this.page.locator('#port').fill(String(port));
    }

    async submit() {
        await this.page.locator('#submit-btn').click();
    }

    async cancel() {
        await this.page.locator('#cancel-btn').click();
    }
}
