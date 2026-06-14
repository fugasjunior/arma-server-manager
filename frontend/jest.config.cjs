const path = require('path');

module.exports = {
  testEnvironment: 'jsdom',
  transform: {
    '^.+\\.tsx?$': ['ts-jest', {
      tsconfig: path.resolve(__dirname, './tsconfig.test.json'),
    }],
  },
  testMatch: ['<rootDir>/__tests__/**/*.test.ts*'],
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
  rootDir: './',
  moduleNameMapper: {
    '^@/(.*)$': '<rootDir>/src/$1',
    '\\.(jpg|jpeg|png|gif|svg|webp)$': '<rootDir>/__mocks__/fileMock.js',
    '\\.module\\.css$': '<rootDir>/__mocks__/styleMock.js',
    '^@uiw/react-codemirror$': '<rootDir>/__mocks__/reactCodemirrorMock.js',
    '^@codemirror/lang-json$': '<rootDir>/__mocks__/langJsonMock.js',
    '^@codemirror/lang-cpp$': '<rootDir>/__mocks__/langCppMock.js',
    '^@codemirror/view$': '<rootDir>/__mocks__/viewMock.js',
    '^@codemirror/lint$': '<rootDir>/__mocks__/lintMock.js'
  },
  setupFilesAfterEnv: ['<rootDir>/jest.setup.cjs']
};