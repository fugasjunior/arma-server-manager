import js from '@eslint/js';
import tseslint from 'typescript-eslint';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';

export default tseslint.config(
    {ignores: ['dist', 'src/api/generated', 'build']},
    js.configs.recommended,
    ...tseslint.configs.recommended,
    reactHooks.configs.flat['recommended-latest'],
    {
        plugins: {'react-refresh': reactRefresh},
        rules: {
            'react-refresh/only-export-components': 'warn',
            '@typescript-eslint/no-explicit-any': 'warn',
            '@typescript-eslint/no-wrapper-object-types': 'warn',
            'react-hooks/exhaustive-deps': 'warn',
            'react-hooks/immutability': 'warn',
            'react-hooks/set-state-in-effect': 'warn',
            'react-hooks/refs': 'warn',
            'react-hooks/purity': 'warn',
            'no-useless-assignment': 'warn',
            '@typescript-eslint/no-unused-vars': ['error', {argsIgnorePattern: '^_', varsIgnorePattern: '^_'}],
        },
    }
);
