import { test, expect } from '@playwright/test';
import { getAuthenticatedPage } from '../fixtures/auth';
import { ModsPage } from '../pages/ModsPage';
import { createBackendHelper } from '../fixtures/backend';
import { BACKEND_URL } from '../config';

// CBA_A3 — known to FakeSteamApiConfig
const CBA_A3_ID = 450814997;

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    await backend.dispose();
});

test('install mod by workshop ID shows it in the list', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.markInstalled('ARMA3');
    await backend.scriptSteamCmdSuccess();
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const modsPage = new ModsPage(page);
    await modsPage.goto();

    await modsPage.installMod(CBA_A3_ID);

    await modsPage.waitForModVisible(CBA_A3_ID);
    await expect(page.getByText('CBA_A3', { exact: true })).toBeVisible();

    await page.close();
});

test('update selected mod changes status to in-progress', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.markInstalled('ARMA3');

    // Pre-seed mod via API
    const token = await backend.getToken();
    await fetch(`${BACKEND_URL}/api/mod?modIds=${CBA_A3_ID}`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
    });

    await backend.scriptSteamCmdSuccess();
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const modsPage = new ModsPage(page);
    await modsPage.goto();

    await modsPage.waitForModVisible(CBA_A3_ID);
    await modsPage.selectMod(CBA_A3_ID);
    await modsPage.updateSelected();

    // Status shows installing spinner
    await expect(
        modsPage.modRow(CBA_A3_ID).locator('span[role="progressbar"]'),
    ).toBeVisible({ timeout: 10_000 });

    await page.close();
});

test('uninstall selected mod removes it from list', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.markInstalled('ARMA3');

    const token = await backend.getToken();
    await fetch(`${BACKEND_URL}/api/mod?modIds=${CBA_A3_ID}`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
    });

    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const modsPage = new ModsPage(page);
    await modsPage.goto();

    await modsPage.waitForModVisible(CBA_A3_ID);
    await modsPage.selectMod(CBA_A3_ID);
    await modsPage.uninstallSelected();

    await modsPage.waitForModGone(CBA_A3_ID);

    await page.close();
});
