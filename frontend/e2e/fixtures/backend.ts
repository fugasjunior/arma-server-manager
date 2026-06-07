import { APIRequestContext, APIResponse, request } from '@playwright/test';
import { BACKEND_URL as BACKEND, E2E_USERNAME as USERNAME, E2E_PASSWORD as PASSWORD } from '../config';

export class BackendHelper {
    private loggedIn = false;

    constructor(private readonly ctx: APIRequestContext) {}

    private async csrfToken(): Promise<string> {
        // Any GET through CsrfCookieFilter seeds the XSRF-TOKEN cookie.
        // /api/users/me returns 401 pre-login but still triggers the filter.
        await this.ctx.get(`${BACKEND}/api/users/me`);
        const state = await this.ctx.storageState();
        return state.cookies.find(c => c.name === 'XSRF-TOKEN')?.value ?? '';
    }

    async ensureLoggedIn(): Promise<void> {
        if (this.loggedIn) return;

        const csrf = await this.csrfToken();
        const params = new URLSearchParams();
        params.set('username', USERNAME);
        params.set('password', PASSWORD);

        const res = await this.ctx.post(`${BACKEND}/api/login`, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-XSRF-TOKEN': csrf,
            },
            data: params.toString(),
        });
        if (!res.ok()) {
            throw new Error(`Login failed: ${res.status()} ${await res.text()}`);
        }
        this.loggedIn = true;
    }

    async dispose(): Promise<void> {
        await this.ctx.dispose();
    }

    async post(path: string, jsonBody?: unknown): Promise<APIResponse> {
        await this.ensureLoggedIn();
        const csrf = await this.csrfToken();
        const headers: Record<string, string> = { 'X-XSRF-TOKEN': csrf };
        const opts: { headers: Record<string, string>; data?: unknown } = { headers };
        if (jsonBody !== undefined) {
            headers['Content-Type'] = 'application/json';
            opts.data = jsonBody;
        }
        const res = await this.ctx.post(`${BACKEND}${path}`, opts);
        if (!res.ok()) {
            throw new Error(`POST ${path} failed: ${res.status()} ${await res.text()}`);
        }
        return res;
    }

    async postJson<T>(path: string, jsonBody?: unknown): Promise<T> {
        const res = await this.post(path, jsonBody);
        return await res.json() as T;
    }

    async reset({ seedSteamAuth = true }: { seedSteamAuth?: boolean } = {}): Promise<void> {
        await this.ensureLoggedIn();
        const csrf = await this.csrfToken();
        const res = await this.ctx.post(
            `${BACKEND}/api/test/reset?seedSteamAuth=${seedSteamAuth}`,
            { headers: { 'X-XSRF-TOKEN': csrf } },
        );
        if (!res.ok()) {
            throw new Error(`Reset failed: ${res.status()} ${await res.text()}`);
        }
    }

    async markInstalled(type: string): Promise<void> {
        await this.ensureLoggedIn();
        const csrf = await this.csrfToken();
        const res = await this.ctx.post(`${BACKEND}/api/test/seed/installed?type=${type}`, {
            headers: { 'X-XSRF-TOKEN': csrf },
        });
        if (!res.ok()) {
            throw new Error(`markInstalled failed: ${res.status()} ${await res.text()}`);
        }
    }

    async seedLocalMod(name: string, serverType: string = 'ARMA3'): Promise<void> {
        await this.ensureLoggedIn();
        const csrf = await this.csrfToken();
        const res = await this.ctx.post(`${BACKEND}/api/test/seed/local-mod?name=${encodeURIComponent(name)}&serverType=${serverType}`, {
            headers: { 'X-XSRF-TOKEN': csrf },
        });
        if (!res.ok()) {
            throw new Error(`seedLocalMod failed: ${res.status()} ${await res.text()}`);
        }
    }

    async removeLocalMod(name: string, serverType: string = 'ARMA3'): Promise<void> {
        await this.ensureLoggedIn();
        const csrf = await this.csrfToken();
        const res = await this.ctx.delete(`${BACKEND}/api/test/seed/local-mod?name=${encodeURIComponent(name)}&serverType=${serverType}`, {
            headers: { 'X-XSRF-TOKEN': csrf },
        });
        if (!res.ok()) {
            throw new Error(`removeLocalMod failed: ${res.status()} ${await res.text()}`);
        }
    }

    async seedWorkshopMod(id: number, name: string = 'CBA_A3', serverType: string = 'ARMA3'): Promise<void> {
        await this.ensureLoggedIn();
        const csrf = await this.csrfToken();
        const res = await this.ctx.post(
            `${BACKEND}/api/test/seed/workshop-mod?id=${id}&name=${encodeURIComponent(name)}&serverType=${serverType}`,
            { headers: { 'X-XSRF-TOKEN': csrf } },
        );
        if (!res.ok()) {
            throw new Error(`seedWorkshopMod failed: ${res.status()} ${await res.text()}`);
        }
    }

    async scriptSteamCmdSuccess(): Promise<void> {
        await this.scriptSteamCmd({ alive: false, exitCode: 0, output: '' });
    }

    async terminateSteamCmd(): Promise<void> {
        await this.ensureLoggedIn();
        const csrf = await this.csrfToken();
        await this.ctx.post(`${BACKEND}/api/test/fakes/steamcmd/terminate`, {
            headers: { 'X-XSRF-TOKEN': csrf },
        });
    }

    async scriptSteamCmdFailure(exitCode = 1): Promise<void> {
        await this.scriptSteamCmd({ alive: false, exitCode, output: 'Error: connection failed' });
    }

    async scriptSteamCmd(opts: { alive: boolean; exitCode: number; output: string }): Promise<void> {
        await this.ensureLoggedIn();
        const csrf = await this.csrfToken();
        await this.ctx.post(`${BACKEND}/api/test/fakes/steamcmd`, {
            headers: { 'X-XSRF-TOKEN': csrf, 'Content-Type': 'application/json' },
            data: opts,
        });
    }

    async scriptServerProcessAlive(): Promise<void> {
        await this.scriptServerProcess({ alive: true, exitCode: 0, output: '' });
    }

    async scriptServerProcess(opts: { alive: boolean; exitCode: number; output: string }): Promise<void> {
        await this.ensureLoggedIn();
        const csrf = await this.csrfToken();
        await this.ctx.post(`${BACKEND}/api/test/fakes/server-process`, {
            headers: { 'X-XSRF-TOKEN': csrf, 'Content-Type': 'application/json' },
            data: opts,
        });
    }

    async scriptSteamAuthVerify(opts: {
        status: 'SUCCESS' | 'REQUIRES_2FA' | 'INVALID_CREDENTIALS' | 'ERROR';
        authType?: 'NONE' | 'EMAIL' | 'MOBILE' | 'UNKNOWN';
        message?: string;
    }): Promise<void> {
        await this.ensureLoggedIn();
        const csrf = await this.csrfToken();
        const res = await this.ctx.post(`${BACKEND}/api/test/fakes/steam-auth-verify`, {
            headers: { 'X-XSRF-TOKEN': csrf, 'Content-Type': 'application/json' },
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
