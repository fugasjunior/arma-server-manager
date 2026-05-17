import { test, expect } from '@playwright/test';
import { getAuthenticatedPage } from '../fixtures/auth';
import { DashboardPage } from '../pages/DashboardPage';
import { createBackendHelper } from '../fixtures/backend';

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    await backend.dispose();
});

test('install Arma3 server transitions from idle to in-progress then finishes', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.scriptSteamCmdSuccess();
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const dashboard = new DashboardPage(page);
    await dashboard.goto();

    const installCard = dashboard.installCard('ARMA3');
    await expect(installCard).toBeVisible();

    await dashboard.triggerInstall('ARMA3');

    // Status transitions to in-progress (button disabled)
    await dashboard.waitForInstallInProgress('ARMA3');

    // Polling eventually shows completion (up to 30s with 5s poll interval)
    await dashboard.waitForInstallFinished('ARMA3');

    await page.close();
});

test('failed installation shows error state on the card', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.scriptSteamCmdFailure();
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const dashboard = new DashboardPage(page);
    await dashboard.goto();

    await dashboard.triggerInstall('ARMA3');

    // Wait for error button to appear (red "Retry Install" / "Retry Update")
    await expect(dashboard.installBtn('ARMA3')).toHaveClass(/MuiButton-colorError/, { timeout: 30_000 });

    await page.close();
});
