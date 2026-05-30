import { defineConfig, devices } from '@playwright/test';
import { BACKEND_URL, FRONTEND_URL } from './config';

export default defineConfig({
    testDir: './specs',
    fullyParallel: false,
    forbidOnly: !!process.env.CI,
    retries: process.env.CI ? 1 : 0,
    workers: 1,
    reporter: process.env.CI ? 'github' : 'list',
    timeout: 30_000,
    use: {
        baseURL: FRONTEND_URL,
        trace: 'on-first-retry',
        screenshot: 'only-on-failure',
        video: 'retain-on-failure',
    },
    projects: [
        {
            name: 'chromium',
            use: { ...devices['Desktop Chrome'] },
        },
    ],
    webServer: [
        {
            command: './gradlew :backend:e2eApp --no-daemon -DskipFrontendBuild=true',
            cwd: '../../',
            url: `${BACKEND_URL}/api/health`,
            reuseExistingServer: !process.env.CI,
            timeout: 180_000,
            stdout: 'pipe',
            stderr: 'pipe',
        },
        {
            command: 'npm run dev',
            url: FRONTEND_URL,
            reuseExistingServer: !process.env.CI,
            timeout: 30_000,
            cwd: '../',
        },
    ],
    outputDir: './test-results',
});
