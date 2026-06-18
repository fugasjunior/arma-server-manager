import { test, expect } from '@playwright/test';
import { getAuthenticatedPage, getAuthenticatedPageWithWizard } from '../fixtures/auth';
import { createBackendHelper } from '../fixtures/backend';
import { SteamAuthWizardPage } from '../pages/SteamAuthWizardPage';
import { FRONTEND_URL as FRONTEND } from '../config';

test.describe('SteamAuthWizard', () => {
    test('wizard is hidden when Steam auth is already configured', async ({ browser }) => {
        const backend = await createBackendHelper();
        await backend.reset(); // seeds fake Steam auth → isConfigured=true
        await backend.dispose();

        const page = await getAuthenticatedPageWithWizard(browser);
        const wizard = new SteamAuthWizardPage(page);
        await wizard.expectHidden();
        await page.close();
    });

    test('skip dismisses wizard and it stays hidden on reload', async ({ browser }) => {
        const backend = await createBackendHelper();
        await backend.reset({ seedSteamAuth: false });
        await backend.dispose();

        const page = await getAuthenticatedPageWithWizard(browser);
        const wizard = new SteamAuthWizardPage(page);
        await wizard.expectVisible();

        await wizard.clickSkip();
        await wizard.expectHidden();

        await page.reload();
        await wizard.expectHidden();
        await page.close();
    });

    test('close (X) button dismisses wizard and it stays hidden on reload', async ({ browser }) => {
        const backend = await createBackendHelper();
        await backend.reset({ seedSteamAuth: false });
        await backend.dispose();

        const page = await getAuthenticatedPageWithWizard(browser);
        const wizard = new SteamAuthWizardPage(page);
        await wizard.expectVisible();

        await wizard.clickClose();
        await wizard.expectHidden();

        await page.reload();
        await wizard.expectHidden();
        await page.close();
    });

    test('success without 2FA jumps straight to completion (no token step flicker)', async ({ browser }) => {
        const backend = await createBackendHelper();
        await backend.reset({ seedSteamAuth: false });
        await backend.scriptSteamAuthVerify({ result: 'SUCCESS' });
        await backend.dispose();

        const page = await getAuthenticatedPageWithWizard(browser);
        const wizard = new SteamAuthWizardPage(page);
        await wizard.expectVisible();

        await wizard.clickContinueOnWelcome();
        await wizard.fillCredentials('user@example.com', 'hunter2');
        await wizard.submitCredentials();

        // Should skip token step and land on completion
        await expect(wizard.finishBtn).toBeVisible();
        await expect(wizard.tokenInput).not.toBeVisible();

        await wizard.clickFinish();
        await wizard.expectHidden();

        await page.reload();
        await wizard.expectHidden();
        await page.close();
    });

    test('EMAIL 2FA flow completes successfully', async ({ browser }) => {
        const backend = await createBackendHelper();
        await backend.reset({ seedSteamAuth: false });
        // First verify → CODE_REQUIRED EMAIL, second verify (with token) → SUCCESS
        await backend.scriptSteamAuthVerify({ result: 'CODE_REQUIRED', authType: 'EMAIL' });
        await backend.scriptSteamAuthVerify({ result: 'SUCCESS' });
        await backend.dispose();

        const page = await getAuthenticatedPageWithWizard(browser);
        const wizard = new SteamAuthWizardPage(page);
        await wizard.expectVisible();

        await wizard.clickContinueOnWelcome();
        await wizard.fillCredentials('user@example.com', 'hunter2');
        await wizard.submitCredentials();

        // Should reach the token step
        await expect(wizard.tokenInput).toBeVisible();

        await wizard.fillToken('AB123');
        await wizard.submitToken();

        // Should reach completion
        await expect(wizard.finishBtn).toBeVisible();
        await wizard.clickFinish();
        await wizard.expectHidden();
        await page.close();
    });

    test('invalid credentials shows error and keeps form open', async ({ browser }) => {
        const backend = await createBackendHelper();
        await backend.reset({ seedSteamAuth: false });
        await backend.scriptSteamAuthVerify({ result: 'INVALID_CREDENTIALS' });
        await backend.dispose();

        const page = await getAuthenticatedPageWithWizard(browser);
        const wizard = new SteamAuthWizardPage(page);
        await wizard.expectVisible();

        await wizard.clickContinueOnWelcome();
        await wizard.fillCredentials('bad-user', 'bad-pass');
        await wizard.submitCredentials();

        await wizard.expectCredentialsError();
        await wizard.expectVisible();
        await page.close();
    });

    test('MOBILE 2FA (TOTP) flow completes successfully', async ({ browser }) => {
        const backend = await createBackendHelper();
        await backend.reset({ seedSteamAuth: false });
        // First verify → CODE_REQUIRED MOBILE, second verify (with TOTP code) → SUCCESS
        await backend.scriptSteamAuthVerify({ result: 'CODE_REQUIRED', authType: 'MOBILE' });
        await backend.scriptSteamAuthVerify({ result: 'SUCCESS' });
        await backend.dispose();

        const page = await getAuthenticatedPageWithWizard(browser);
        const wizard = new SteamAuthWizardPage(page);
        await wizard.expectVisible();

        await wizard.clickContinueOnWelcome();
        await wizard.fillCredentials('mobile-user', 'pass');
        await wizard.submitCredentials();

        // Should reach the token step to enter the mobile authenticator code
        await expect(wizard.tokenInput).toBeVisible();

        await wizard.fillToken('12345');
        await wizard.submitToken();

        // Should reach completion
        await expect(wizard.finishBtn).toBeVisible();
        await wizard.clickFinish();
        await wizard.expectHidden();
        await page.close();
    });

    test('re-run wizard button on Settings page reopens wizard', async ({ browser }) => {
        const backend = await createBackendHelper();
        await backend.reset({ seedSteamAuth: false });
        await backend.dispose();

        // Use authenticated page WITH wizardCompleted already set (wizard bypassed on login)
        const page = await getAuthenticatedPage(browser);
        const wizard = new SteamAuthWizardPage(page);
        await wizard.expectHidden();

        // Navigate to Settings and click re-run
        await page.goto(`${FRONTEND}/settings`);
        await page.getByTestId('reopen-steam-auth-wizard').click();

        await wizard.expectVisible();
        await page.close();
    });
});
