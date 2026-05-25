import {expect, test} from '@playwright/test';
import {getAuthenticatedPage} from '../fixtures/auth';
import {LocalModsPage} from '../pages/LocalModsPage';
import {createBackendHelper} from '../fixtures/backend';

const MOD_NAME = '@TestLocalMod';

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    await backend.markInstalled('ARMA3');
    await backend.dispose();
});

test('sync local mod from filesystem — appears in list', async ({browser}) => {
    const backend = await createBackendHelper();
    await backend.seedLocalMod(MOD_NAME, 'ARMA3');
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const modsPage = new LocalModsPage(page);
    await modsPage.goto();

    await modsPage.clickSync();
    await modsPage.waitForSyncComplete();
    await modsPage.waitForModVisible(MOD_NAME);
    await expect(modsPage.modRow(MOD_NAME)).toBeVisible();

    await page.close();
});

test('remove local mod from filesystem — removed from list after sync', async ({browser}) => {
    const backend = await createBackendHelper();
    await backend.seedLocalMod(MOD_NAME, 'ARMA3');
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    const modsPage = new LocalModsPage(page);
    await modsPage.goto();

    await modsPage.clickSync();
    await modsPage.waitForSyncComplete();
    await modsPage.waitForModVisible(MOD_NAME);

    const backend2 = await createBackendHelper();
    await backend2.removeLocalMod(MOD_NAME, 'ARMA3');
    await backend2.dispose();

    await modsPage.clickSync();
    await modsPage.waitForSyncComplete();
    await modsPage.waitForModGone(MOD_NAME);

    await expect(page.getByText('No local mods uploaded yet')).toBeVisible();

    await page.close();
});

test('local mod appears in list after sync', async ({browser}) => {
    const backend = await createBackendHelper();
    await backend.seedLocalMod(MOD_NAME, 'ARMA3');
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);
    await page.goto('/mods/LOCAL');

    const modsPage = new LocalModsPage(page);
    await modsPage.clickSync();
    await modsPage.waitForSyncComplete();

    await expect(page.getByText(MOD_NAME)).toBeVisible({timeout: 10_000});

    await page.close();
});
