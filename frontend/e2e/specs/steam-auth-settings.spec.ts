import { test, expect } from '@playwright/test';
import { getAuthenticatedPage } from '../fixtures/auth';
import { createBackendHelper } from '../fixtures/backend';
import { FRONTEND_URL as FRONTEND } from '../config';

test.describe('Steam Auth settings form', () => {
    test('saving with username and empty password succeeds (no prior password)', async ({ browser }) => {
        const backend = await createBackendHelper();
        await backend.reset({ seedSteamAuth: false }); // no previously saved password
        await backend.dispose();

        const page = await getAuthenticatedPage(browser);
        await page.goto(`${FRONTEND}/settings`);

        await page.locator('#username').fill('steam_user');
        // password left empty on purpose

        await page.getByRole('button', { name: 'Submit' }).click();

        await expect(page.getByText('Steam Auth successfully set.')).toBeVisible();
        await expect(page.getByText('Setting Steam Auth failed.')).not.toBeVisible();
        await page.close();
    });

    test('saving with username and password succeeds', async ({ browser }) => {
        const backend = await createBackendHelper();
        await backend.reset({ seedSteamAuth: false });
        await backend.dispose();

        const page = await getAuthenticatedPage(browser);
        await page.goto(`${FRONTEND}/settings`);

        await page.locator('#username').fill('steam_user');
        await page.locator('#password').fill('hunter2');

        await page.getByRole('button', { name: 'Submit' }).click();

        await expect(page.getByText('Steam Auth successfully set.')).toBeVisible();
        await page.close();
    });
});
