import { test, expect } from '@playwright/test';
import { getAuthenticatedPage } from '../fixtures/auth';
import { ScenariosPage } from '../pages/ScenariosPage';
import { createBackendHelper } from '../fixtures/backend';
import path from 'path';

const TEST_PBO = path.join(__dirname, '../fixtures/test.pbo');

let serverId: number;

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    const server = await backend.postJson<{ id: number }>('/api/server', {
        name: 'Scenario E2E Server',
        type: 'ARMA3',
        port: 2302,
        queryPort: 2303,
        maxPlayers: 32,
    });
    serverId = server.id;
    await backend.dispose();
});

test('upload scenario appears in list', async ({ browser }) => {
    const page = await getAuthenticatedPage(browser);
    const scenariosPage = new ScenariosPage(page);
    await scenariosPage.goto(serverId);

    await scenariosPage.uploadFile(TEST_PBO);

    await scenariosPage.waitForScenario('test.pbo');
    await expect(page.getByText('test.pbo', { exact: true })).toBeVisible();

    await page.close();
});

test('select and delete scenario removes it', async ({ browser }) => {
    const page = await getAuthenticatedPage(browser);
    const scenariosPage = new ScenariosPage(page);
    await scenariosPage.goto(serverId);

    await scenariosPage.uploadFile(TEST_PBO);
    await scenariosPage.waitForScenario('test.pbo');

    await scenariosPage.selectScenario('test.pbo');
    await scenariosPage.deleteSelected();

    await scenariosPage.waitForScenarioGone('test.pbo');
    await expect(page.getByText('test.pbo', { exact: true })).toHaveCount(0);

    await page.close();
});

test('download scenario returns a file', async ({ browser }) => {
    const page = await getAuthenticatedPage(browser);
    const scenariosPage = new ScenariosPage(page);
    await scenariosPage.goto(serverId);

    await scenariosPage.uploadFile(TEST_PBO);
    await scenariosPage.waitForScenario('test.pbo');

    const [download] = await Promise.all([
        page.waitForEvent('download'),
        page.getByText('test.pbo', { exact: true }).first().click(),
    ]);

    expect(download.suggestedFilename()).toBe('test.pbo');

    await page.close();
});
