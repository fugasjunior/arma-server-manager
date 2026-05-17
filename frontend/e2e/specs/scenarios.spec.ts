import { test, expect } from '@playwright/test';
import { getAuthenticatedPage } from '../fixtures/auth';
import { ScenariosPage } from '../pages/ScenariosPage';
import { createBackendHelper } from '../fixtures/backend';
import path from 'path';

const TEST_PBO = path.join(__dirname, '../fixtures/test.pbo');

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    await backend.dispose();
});

test('upload scenario appears in list', async ({ browser }) => {
    const page = await getAuthenticatedPage(browser);
    const scenariosPage = new ScenariosPage(page);
    await scenariosPage.goto();

    await scenariosPage.uploadFile(TEST_PBO);

    await scenariosPage.waitForScenario('test.pbo');
    await expect(page.getByText('test.pbo', { exact: true })).toBeVisible();

    await page.close();
});

test('select and delete scenario removes it', async ({ browser }) => {
    const page = await getAuthenticatedPage(browser);
    const scenariosPage = new ScenariosPage(page);
    await scenariosPage.goto();

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
    await scenariosPage.goto();

    await scenariosPage.uploadFile(TEST_PBO);
    await scenariosPage.waitForScenario('test.pbo');

    const [download] = await Promise.all([
        page.waitForEvent('download'),
        page.getByText('test.pbo', { exact: true }).first().click(),
    ]);

    expect(download.suggestedFilename()).toBe('test.pbo');

    await page.close();
});
