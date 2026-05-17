import { Browser, Page, request } from '@playwright/test';
import { BACKEND_URL as BACKEND, FRONTEND_URL as FRONTEND, E2E_USERNAME as USERNAME, E2E_PASSWORD as PASSWORD } from '../config';

/**
 * Returns a Page with JWT already set in localStorage (bypasses the login UI).
 * Uses addInitScript so localStorage is populated before the React app boots,
 * preventing an auth-redirect race.
 */
export async function getAuthenticatedPage(browser: Browser): Promise<Page> {
    const ctx = await request.newContext();

    const params = new URLSearchParams();
    params.set('username', USERNAME);
    params.set('password', PASSWORD);

    const res = await ctx.post(`${BACKEND}/api/login`, {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        data: params.toString(),
    });
    const body = await res.json();
    const token: string = body.token;
    const expiresIn: number = body.expiresIn;
    const expirationTime = new Date().getTime() + expiresIn * 1000;

    await ctx.dispose();

    const page = await browser.newPage();
    await page.addInitScript(
        ({ token, expirationTime }: { token: string; expirationTime: number }) => {
            localStorage.setItem('token', token);
            localStorage.setItem('expirationTime', String(expirationTime));
            localStorage.setItem('wizardCompleted', 'true');
        },
        { token, expirationTime },
    );
    await page.goto(FRONTEND);

    return page;
}
