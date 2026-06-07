import { Browser, Page } from '@playwright/test';
import { FRONTEND_URL as FRONTEND, E2E_USERNAME as USERNAME, E2E_PASSWORD as PASSWORD } from '../config';

async function loginInBrowser(browser: Browser, initScript?: () => void): Promise<Page> {
    const browserContext = await browser.newContext();
    const page = await browserContext.newPage();

    if (initScript) {
        await page.addInitScript(initScript);
    }

    await page.goto(`${FRONTEND}/login`);
    await page.locator('#username').fill(USERNAME);
    await page.locator('#password').fill(PASSWORD);
    await page.getByTestId('login-submit').click();
    await page.waitForURL(`${FRONTEND}/`, { timeout: 15_000 });

    return page;
}

export async function getAuthenticatedPage(browser: Browser): Promise<Page> {
    return loginInBrowser(browser, () => {
        localStorage.setItem('wizardCompleted', 'true');
    });
}

export async function getAuthenticatedPageWithWizard(browser: Browser): Promise<Page> {
    return loginInBrowser(browser);
}
