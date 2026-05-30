import { test, expect } from '@playwright/test';
import { getAuthenticatedPage } from '../fixtures/auth';
import { DashboardPage } from '../pages/DashboardPage';
import { createBackendHelper } from '../fixtures/backend';

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    await backend.dispose();
});

test('uninstall after successful install resets card to not-installed state', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.scriptSteamCmdSuccess();
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const dashboard = new DashboardPage(page);
    await dashboard.goto();

    await dashboard.triggerInstall('ARMA3');
    await dashboard.waitForInstallFinished('ARMA3');

    await dashboard.triggerUninstall('ARMA3');
    await dashboard.confirmUninstall('ARMA3');

    await dashboard.waitForUninstalled('ARMA3');

    // Install/Update button shows "Install" (not "Update") — server is no longer installed
    await expect(dashboard.installBtn('ARMA3')).toHaveText(/Install/i, { timeout: 10_000 });

    await page.close();
});
