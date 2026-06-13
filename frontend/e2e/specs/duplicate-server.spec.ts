import {test, expect} from '@playwright/test';
import {getAuthenticatedPage} from '../fixtures/auth';
import {ServersPage} from '../pages/ServersPage';
import {createBackendHelper} from '../fixtures/backend';

let serverId: number;

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    const server = await backend.postJson<{id: number}>('/api/server', {
        name: 'Dup E2E Server',
        type: 'ARMA3',
        port: 2302,
        queryPort: 2303,
        maxPlayers: 32,
    });
    serverId = server.id;
    await backend.dispose();
});

test('duplicate server creates copy with name suffix', async ({browser}) => {
    const page = await getAuthenticatedPage(browser);
    const serversPage = new ServersPage(page);

    await serversPage.goto();
    await serversPage.openActionsMenu(serverId);
    await page.getByRole('menuitem', {name: 'Duplicate'}).click();

    await expect(page.getByText("Server 'Dup E2E Server' successfully duplicated")).toBeVisible();
    await expect(page.getByText('Dup E2E Server (copy)')).toBeVisible();

    await page.close();
});
