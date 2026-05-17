import { test, expect } from '@playwright/test';
import { getAuthenticatedPage } from '../fixtures/auth';
import { ModsPage } from '../pages/ModsPage';
import { PresetsPage } from '../pages/PresetsPage';
import { createBackendHelper } from '../fixtures/backend';
import { BACKEND_URL } from '../config';
import path from 'path';

async function seedPreset(backend: Awaited<ReturnType<typeof createBackendHelper>>, name: string, modId: number) {
    const token = await backend.getToken();
    const res = await fetch(`${BACKEND_URL}/api/mod/preset`, {
        method: 'POST',
        headers: {Authorization: `Bearer ${token}`, 'Content-Type': 'application/json'},
        body: JSON.stringify({name, mods: [modId], type: 'ARMA3'}),
    });
    return await res.json() as {id: number};
}

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

    await presetsPage.deleteAction(id);

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

    const downloadPromise = page.waitForEvent('download');
    await presetsPage.exportAction(id);
    const download = await downloadPromise;

    expect(download.suggestedFilename()).toMatch(/\.html$/);

    await page.close();
});

test('import preset shows name dialog with meta tag name pre-filled', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.markInstalled('ARMA3');
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const presetsPage = new PresetsPage(page);
    await presetsPage.goto();

    const fileInput = page.getByTestId('preset-import-input');
    await fileInput.setInputFiles(ARMA_LAUNCHER_PRESET);

    // Dialog should open with E2E Test Preset pre-filled from meta tag
    await expect(page.getByRole('dialog')).toBeVisible();
    await expect(page.getByRole('textbox')).toHaveValue('E2E Test Preset');

    await page.getByRole('button', {name: /^import$/i}).click();
    await presetsPage.waitForPresetByName('E2E Test Preset');

    await page.close();
});

test('rename preset updates its name in the list', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.markInstalled('ARMA3');
    await seedMod(backend);
    const {id} = await seedPreset(backend, 'Original Preset', CBA_A3_ID);
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const presetsPage = new PresetsPage(page);
    await presetsPage.goto();

    await presetsPage.renameAction(id);

    const dialog = page.getByRole('dialog');
    await expect(dialog).toBeVisible();
    await expect(page.getByRole('textbox')).toHaveValue('Original Preset');

    const nameField = page.getByRole('textbox');
    await nameField.clear();
    await nameField.fill('Renamed Preset');

    await page.getByRole('button', {name: /^rename$/i}).click();
    await expect(dialog).not.toBeVisible();
    await presetsPage.waitForPresetByName('Renamed Preset');

    await page.close();
});

test('rename to taken name shows error and disables Rename button', async ({ browser }) => {
    const backend = await createBackendHelper();
    await backend.markInstalled('ARMA3');
    await seedMod(backend);
    await seedPreset(backend, 'Preset Alpha', CBA_A3_ID);
    const {id: idBeta} = await seedPreset(backend, 'Preset Beta', CBA_A3_ID);
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const presetsPage = new PresetsPage(page);
    await presetsPage.goto();

    await presetsPage.renameAction(idBeta);

    const nameField = page.getByRole('textbox');
    await nameField.clear();
    await nameField.fill('Preset Alpha');

    await expect(page.getByText('This preset name is already in use')).toBeVisible();
    await expect(page.getByRole('button', {name: /^rename$/i})).toBeDisabled();

    await page.close();
});
