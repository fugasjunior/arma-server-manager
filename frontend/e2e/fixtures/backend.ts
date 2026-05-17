import { APIRequestContext, request } from '@playwright/test';
import { BACKEND_URL as BACKEND, E2E_USERNAME as USERNAME, E2E_PASSWORD as PASSWORD } from '../config';

export class BackendHelper {
    private token: string | null = null;

    constructor(private readonly ctx: APIRequestContext) {}

    async getToken(): Promise<string> {
        if (this.token) return this.token;

        const params = new URLSearchParams();
        params.set('username', USERNAME);
        params.set('password', PASSWORD);

        const res = await this.ctx.post(`${BACKEND}/api/login`, {
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            data: params.toString(),
        });
        const body = await res.json();
        this.token = body.token as string;
        return this.token;
    }

    async dispose(): Promise<void> {
        await this.ctx.dispose();
    }

    async reset({ seedSteamAuth = true }: { seedSteamAuth?: boolean } = {}): Promise<void> {
        const token = await this.getToken();
        const res = await this.ctx.post(
            `${BACKEND}/api/test/reset?seedSteamAuth=${seedSteamAuth}`,
            { headers: { Authorization: `Bearer ${token}` } },
        );
        if (!res.ok()) {
            throw new Error(`Reset failed: ${res.status()} ${await res.text()}`);
        }
    }

    async markInstalled(type: string): Promise<void> {
        const token = await this.getToken();
        const res = await this.ctx.post(`${BACKEND}/api/test/seed/installed?type=${type}`, {
            headers: { Authorization: `Bearer ${token}` },
        });
        if (!res.ok()) {
            throw new Error(`markInstalled failed: ${res.status()} ${await res.text()}`);
        }
    }

    async scriptSteamCmdSuccess(): Promise<void> {
        await this.scriptSteamCmd({ alive: false, exitCode: 0, output: '' });
    }

    async scriptSteamCmdFailure(exitCode = 1): Promise<void> {
        await this.scriptSteamCmd({ alive: false, exitCode, output: 'Error: connection failed' });
    }

    async scriptSteamCmd(opts: { alive: boolean; exitCode: number; output: string }): Promise<void> {
        const token = await this.getToken();
        await this.ctx.post(`${BACKEND}/api/test/fakes/steamcmd`, {
            headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
            data: opts,
        });
    }

    async scriptServerProcessAlive(): Promise<void> {
        await this.scriptServerProcess({ alive: true, exitCode: 0, output: '' });
    }

    async scriptServerProcess(opts: { alive: boolean; exitCode: number; output: string }): Promise<void> {
        const token = await this.getToken();
        await this.ctx.post(`${BACKEND}/api/test/fakes/server-process`, {
            headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
            data: opts,
        });
    }

    async scriptSteamAuthVerify(opts: {
        status: 'SUCCESS' | 'REQUIRES_2FA' | 'INVALID_CREDENTIALS' | 'ERROR';
        authType?: 'NONE' | 'EMAIL' | 'MOBILE' | 'UNKNOWN';
        message?: string;
    }): Promise<void> {
        const token = await this.getToken();
        const res = await this.ctx.post(`${BACKEND}/api/test/fakes/steam-auth-verify`, {
            headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
            data: opts,
        });
        if (!res.ok()) {
            throw new Error(`scriptSteamAuthVerify failed: ${res.status()} ${await res.text()}`);
        }
    }
}

export async function createBackendHelper(): Promise<BackendHelper> {
    const ctx = await request.newContext();
    return new BackendHelper(ctx);
}
