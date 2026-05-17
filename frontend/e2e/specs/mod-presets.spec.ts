import { test, expect } from '@playwright/test';
import { getAuthenticatedPage } from '../fixtures/auth';
import { ModsPage } from '../pages/ModsPage';
import { PresetsPage } from '../pages/PresetsPage';
import { createBackendHelper } from '../fixtures/backend';
import { BACKEND_URL } from '../config';
import path from 'path';

const CBA_A3_ID = 450814997;
const ARMA_LAUNCHER_PRESET = path.join(__dirname, '../fixtures/arma-launcher-preset.html');

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    await backend.dispose();
});

async function seedMod(backend: Awaited<ReturnType<typeof createBackendHelper>>) {
    const token = await backend.getToken();
    await fetch(`${BACKEND_URL}/api/mod?modIds=${CBA_A3_ID}`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}` },
    });
}

test('create preset from selected mods', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.markInstalled('ARMA3');
    await seedMod(backend);
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const modsPage = new ModsPage(page);
    await modsPage.goto();

    await modsPage.waitForModVisible(CBA_A3_ID);
    await modsPage.selectMod(CBA_A3_ID);
    await modsPage.saveAsPreset();

    // Dialog: enter preset name
    await page.locator('[placeholder*="name"], input[type="text"]').last().fill('My Preset');
    await page.getByRole('button', { name: /confirm|create|ok/i }).click();

    const presetsPage = new PresetsPage(page);
    await presetsPage.goto();
    await presetsPage.waitForPresetByName('My Preset');

    await page.close();
});

test('delete preset removes it', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.markInstalled('ARMA3');
    await seedMod(backend);

    const token = await backend.getToken();
    const res = await fetch(`${BACKEND_URL}/api/mod/preset`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: 'Deletable', mods: [CBA_A3_ID], type: 'ARMA3' }),
    });
    const { id } = await res.json();
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const presetsPage = new PresetsPage(page);
    await presetsPage.goto();

    await presetsPage.deleteBtn(id).click();

    await presetsPage.waitForPresetGone(id);

    await page.close();
});

test('import Arma Launcher preset creates a new preset', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.markInstalled('ARMA3');
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const presetsPage = new PresetsPage(page);
    await presetsPage.goto();

    await presetsPage.importPreset(ARMA_LAUNCHER_PRESET);

    await presetsPage.waitForPresetByName('E2E Test Preset');

    await page.close();
});

test('export preset triggers file download', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.markInstalled('ARMA3');
    await seedMod(backend);

    const token = await backend.getToken();
    const res = await fetch(`${BACKEND_URL}/api/mod/preset`, {
        method: 'POST',
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: 'Export Me', mods: [CBA_A3_ID], type: 'ARMA3' }),
    });
    const { id } = await res.json();
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const presetsPage = new PresetsPage(page);
    await presetsPage.goto();

    const [download] = await Promise.all([
        page.waitForEvent('download'),
        presetsPage.exportBtn(id).click(),
    ]);

    expect(download.suggestedFilename()).toMatch(/\.html$/);

    await page.close();
});
