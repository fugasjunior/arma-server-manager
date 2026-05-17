import { Page, Locator } from '@playwright/test';

export class DashboardPage {
    constructor(private readonly page: Page) {}

    async goto() {
        await this.page.goto('/');
    }

    installCard(type: string): Locator {
        return this.page.getByTestId(`install-card-${type}`);
    }

    installBtn(type: string): Locator {
        return this.page.getByTestId(`install-update-btn-${type}`);
    }

    installStatus(type: string): Locator {
        return this.page.getByTestId(`install-status-${type}`);
    }

    async triggerInstall(type: string) {
        await this.installBtn(type).click();
    }

    async waitForInstallInProgress(type: string) {
        await this.installStatus(type).locator('button[disabled]').waitFor({ state: 'visible', timeout: 10_000 });
    }

    async waitForInstallFinished(type: string) {
        await this.installBtn(type).waitFor({ state: 'visible', timeout: 30_000 });
    }

    uninstallMenuBtn(type: string): Locator {
        return this.page.getByTestId(`install-menu-btn-${type}`);
    }

    uninstallMenuItem(type: string): Locator {
        return this.page.getByTestId(`uninstall-btn-${type}`);
    }

    uninstallConfirmBtn(type: string): Locator {
        return this.page.getByTestId(`uninstall-confirm-${type}`);
    }

    async openInstallationMenu(type: string) {
        await this.uninstallMenuBtn(type).click();
    }

    async triggerUninstall(type: string) {
        await this.openInstallationMenu(type);
        await this.uninstallMenuItem(type).click();
    }

    async confirmUninstall(type: string) {
        await this.uninstallConfirmBtn(type).click();
    }

    async waitForUninstalled(type: string) {
        await this.uninstallMenuBtn(type).waitFor({ state: 'hidden', timeout: 10_000 });
    }
}
