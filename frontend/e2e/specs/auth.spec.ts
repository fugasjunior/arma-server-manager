import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { createBackendHelper } from '../fixtures/backend';
import { E2E_USERNAME, E2E_PASSWORD } from '../config';

test.beforeEach(async () => {
    const backend = await createBackendHelper();
    await backend.reset();
    await backend.dispose();
});

test('successful login redirects to dashboard', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();
    await loginPage.login(E2E_USERNAME, E2E_PASSWORD);
    await expect(page).toHaveURL('/');
});

test('wrong password shows error and stays on login', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.goto();
    await loginPage.login(E2E_USERNAME, 'wrongpassword');
    await expect(page).toHaveURL('/login');
});

test('unauthenticated access to /servers redirects to login', async ({ page }) => {
    await page.goto('/servers');
    await expect(page).toHaveURL('/login');
});
