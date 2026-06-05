import {test, expect} from '@playwright/test';
import {getAuthenticatedPage} from '../fixtures/auth';
import {BikeysPage} from '../pages/BikeysPage';
import {createBackendHelper} from '../fixtures/backend';
import {BACKEND_URL} from '../config';
import path from 'path';

const TEST_BIKEY = path.join(__dirname, '../fixtures/test.bikey');

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    await backend.dispose();
});

test('upload bikey appears in list', async ({browser}) => {
    const backend = await createBackendHelper();
    const token = await backend.getToken();
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);

    const res = await page.request.post(`${BACKEND_URL}/api/server`, {
        headers: {Authorization: `Bearer ${token}`, 'Content-Type': 'application/json'},
        data: {name: 'Bikey E2E Server', type: 'ARMA3', port: 2302, queryPort: 2303, maxPlayers: 32},
    });
    const {id} = await res.json();

    const bikeysPage = new BikeysPage(page);
    await bikeysPage.openForServer(id);

    await bikeysPage.uploadFile(TEST_BIKEY);

    await bikeysPage.waitForKey('test.bikey');
    await expect(page.getByText('test.bikey', {exact: true})).toBeVisible();

    await page.close();
});

test('select and delete bikey removes it from list', async ({browser}) => {
    const backend = await createBackendHelper();
    const token = await backend.getToken();
    await backend.dispose();

    const page = await getAuthenticatedPage(browser);

    const res = await page.request.post(`${BACKEND_URL}/api/server`, {
        headers: {Authorization: `Bearer ${token}`, 'Content-Type': 'application/json'},
        data: {name: 'Bikey E2E Server', type: 'ARMA3', port: 2302, queryPort: 2303, maxPlayers: 32},
    });
    const {id} = await res.json();

    const bikeysPage = new BikeysPage(page);
    await bikeysPage.openForServer(id);

    await bikeysPage.uploadFile(TEST_BIKEY);
    await bikeysPage.waitForKey('test.bikey');

    await bikeysPage.selectKey('test.bikey');
    await bikeysPage.deleteSelected();

    await bikeysPage.waitForKeyGone('test.bikey');
    await expect(page.getByText('test.bikey', {exact: true})).toHaveCount(0);

    await page.close();
});
