const path = require('path');

module.exports = {
  testEnvironment: 'node',
  transform: {
    '^.+\\.tsx?$': 'ts-jest',
  },
  testMatch: ['<rootDir>/__tests__/**/*.test.ts'],
  moduleFileExtensions: ['ts', 'tsx', 'js', 'jsx', 'json', 'node'],
  globals: {
    'ts-jest': {
      tsconfig: path.resolve(__dirname, './tsconfig.json'),
    },
  },
  rootDir: './src',
  testOutputDir: './build/__tests__', // Specify your desired output directory here
};