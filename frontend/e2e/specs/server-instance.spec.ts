import { test, expect } from '@playwright/test';
import { getAuthenticatedPage } from '../fixtures/auth';
import { ServersPage } from '../pages/ServersPage';
import { NewServerPage } from '../pages/NewServerPage';
import { createBackendHelper } from '../fixtures/backend';

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    await backend.dispose();
});

test('create Arma3 server and verify it appears in list', async ({ browser }) => {
    const page = await getAuthenticatedPage(browser);
    const newServerPage = new NewServerPage(page);

    await newServerPage.goto('ARMA3');
    await newServerPage.fillBasicFields('Test Arma3 Server', 2302);
    await newServerPage.submit();

    await expect(page).toHaveURL('/servers');
    await expect(page.locator('.server-list-entry')).toHaveCount(1);
    await expect(page.locator('.server-list-entry')).toContainText('Test Arma3 Server');

    await page.close();
});

test('start and stop server', async ({ browser }) => {
    const page = await getAuthenticatedPage(browser);
    const backend = await createBackendHelper();

    // Create server via API
    const { id } = await backend.postJson<{ id: number }>('/api/server', {
        name: 'Arma3 E2E',
        type: 'ARMA3',
        port: 2302,
        queryPort: 2303,
        maxPlayers: 32,
    });

    await backend.scriptServerProcessAlive();
    await backend.dispose();

    const serversPage = new ServersPage(page);
    await serversPage.goto();

    await serversPage.startBtn(id).click();
    await serversPage.waitForServerRunning(id);
    await expect(serversPage.stopBtn(id)).toBeVisible();

    await serversPage.stopBtn(id).click();
    await serversPage.waitForServerStopped(id);
    await expect(serversPage.startBtn(id)).toBeVisible();

    await page.close();
});

test('delete server removes it from the list', async ({ browser }) => {
    const page = await getAuthenticatedPage(browser);
    const backend = await createBackendHelper();

    const { id } = await backend.postJson<{ id: number }>('/api/server', {
        name: 'Delete Me',
        type: 'ARMA3',
        port: 2302,
        queryPort: 2303,
        maxPlayers: 32,
    });
    await backend.dispose();

    const serversPage = new ServersPage(page);
    await serversPage.goto();

    await serversPage.clickDeleteAndConfirm(id);

    await expect(serversPage.serverRow(id)).toHaveCount(0);

    await page.close();
});

test('edit server persists changes', async ({ browser }) => {
    const page = await getAuthenticatedPage(browser);
    const backend = await createBackendHelper();

    const { id } = await backend.postJson<{ id: number }>('/api/server', {
        name: 'Original Name',
        type: 'ARMA3',
        port: 2302,
        queryPort: 2303,
        maxPlayers: 32,
    });
    await backend.dispose();

    await page.goto(`/servers/${id}`);
    await page.locator('#name').fill('Updated Name');
    await page.locator('#submit-btn').click();

    await page.goto(`/servers/${id}`);
    await expect(page.locator('#name')).toHaveValue('Updated Name');

    await page.close();
});
