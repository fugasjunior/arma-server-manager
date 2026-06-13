import {test, expect} from '@playwright/test';
import {getAuthenticatedPage} from '../fixtures/auth';
import {BikeysPage} from '../pages/BikeysPage';
import {createBackendHelper} from '../fixtures/backend';
import path from 'path';

const TEST_BIKEY = path.join(__dirname, '../fixtures/test.bikey');

let serverId: number;

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    const server = await backend.postJson<{id: number}>('/api/server', {
        name: 'Bikey E2E Server',
        type: 'ARMA3',
        port: 2302,
        queryPort: 2303,
        maxPlayers: 32,
    });
    serverId = server.id;
    await backend.dispose();
});

test('upload bikey appears in list', async ({browser}) => {
    const page = await getAuthenticatedPage(browser);

    const bikeysPage = new BikeysPage(page);
    await bikeysPage.openForServer(serverId);

    await bikeysPage.uploadFile(TEST_BIKEY);

    await bikeysPage.waitForKey('test.bikey');
    await expect(page.getByText('test.bikey', {exact: true})).toBeVisible();

    await page.close();
});

test('select and delete bikey removes it from list', async ({browser}) => {
    const page = await getAuthenticatedPage(browser);

    const bikeysPage = new BikeysPage(page);
    await bikeysPage.openForServer(serverId);

    await bikeysPage.uploadFile(TEST_BIKEY);
    await bikeysPage.waitForKey('test.bikey');

    await bikeysPage.selectKey('test.bikey');
    await bikeysPage.deleteSelected();

    await bikeysPage.waitForKeyGone('test.bikey');
    await expect(page.getByText('test.bikey', {exact: true})).toHaveCount(0);

    await page.close();
});
