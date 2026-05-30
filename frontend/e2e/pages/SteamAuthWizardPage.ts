import { Page, Locator, expect } from '@playwright/test';

export class SteamAuthWizardPage {
    readonly dialog: Locator;
    readonly closeBtn: Locator;
    readonly welcomeSkipBtn: Locator;
    readonly welcomeContinueBtn: Locator;
    readonly usernameInput: Locator;
    readonly passwordInput: Locator;
    readonly credentialsBackBtn: Locator;
    readonly credentialsSubmitBtn: Locator;
    readonly credentialsError: Locator;
    readonly tokenInput: Locator;
    readonly tokenBackBtn: Locator;
    readonly tokenSubmitBtn: Locator;
    readonly tokenError: Locator;
    readonly finishBtn: Locator;

    constructor(private readonly page: Page) {
        this.dialog = page.getByTestId('steam-auth-wizard');
        this.closeBtn = page.getByTestId('steam-auth-wizard-close');
        this.welcomeSkipBtn = page.getByTestId('welcome-skip');
        this.welcomeContinueBtn = page.getByTestId('welcome-continue');
        this.usernameInput = page.getByTestId('credentials-username');
        this.passwordInput = page.getByTestId('credentials-password');
        this.credentialsBackBtn = page.getByTestId('credentials-back');
        this.credentialsSubmitBtn = page.getByTestId('credentials-submit');
        this.credentialsError = page.getByTestId('credentials-error');
        this.tokenInput = page.getByTestId('token-input');
        this.tokenBackBtn = page.getByTestId('token-back');
        this.tokenSubmitBtn = page.getByTestId('token-submit');
        this.tokenError = page.getByTestId('token-error');
        this.finishBtn = page.getByTestId('completion-finish');
    }

    async expectVisible() {
        await expect(this.dialog).toBeVisible();
    }

    async expectHidden() {
        await expect(this.dialog).not.toBeVisible();
    }

    async clickSkip() {
        await this.welcomeSkipBtn.click();
    }

    async clickClose() {
        await this.closeBtn.click();
    }

    async clickContinueOnWelcome() {
        await this.welcomeContinueBtn.click();
    }

    async fillCredentials(username: string, password: string) {
        await this.usernameInput.fill(username);
        await this.passwordInput.fill(password);
    }

    async submitCredentials() {
        await this.credentialsSubmitBtn.click();
    }

    async fillToken(token: string) {
        await this.tokenInput.fill(token);
    }

    async submitToken() {
        await this.tokenSubmitBtn.click();
    }

    async clickFinish() {
        await this.finishBtn.click();
    }

    async expectCredentialsError(text?: string) {
        await expect(this.credentialsError).toBeVisible();
        if (text) {
            await expect(this.credentialsError).toContainText(text);
        }
    }

    async expectTokenError(text?: string) {
        await expect(this.tokenError).toBeVisible();
        if (text) {
            await expect(this.tokenError).toContainText(text);
        }
    }
}
