const get = jest.fn();
const post = jest.fn();

jest.mock('../../src/api/client', () => ({
    apiAxiosInstance: {get, post},
}));

import {login} from '../../src/services/authService';

describe('authService.login', () => {
    beforeEach(() => {
        get.mockReset().mockResolvedValue({});
        post.mockReset().mockResolvedValue({});
    });

    it('primes the XSRF-TOKEN cookie with GET /users/me before POST /login', async () => {
        const calls: string[] = [];
        get.mockImplementation(() => {
            calls.push('get');
            return Promise.resolve({});
        });
        post.mockImplementation(() => {
            calls.push('post');
            return Promise.resolve({});
        });

        await login('user', 'pass');

        expect(get).toHaveBeenCalledWith('/users/me');
        expect(post).toHaveBeenCalledWith('/login', expect.anything(), expect.anything());
        expect(calls).toEqual(['get', 'post']);
    });

    it('still posts /login when the priming GET rejects (e.g. 401 pre-login)', async () => {
        get.mockRejectedValue(new Error('401'));

        await login('user', 'pass');

        expect(post).toHaveBeenCalledTimes(1);
        expect(post).toHaveBeenCalledWith('/login', expect.anything(), expect.anything());
    });
});
