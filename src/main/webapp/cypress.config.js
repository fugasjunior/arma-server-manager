const {defineConfig} = require("cypress");

module.exports = defineConfig({
  e2e: {
    setupNodeEvents(on, config) {
      // implement node event listeners here
    },
  },
  env: {
    username: 'test',
    password: 'test',
    apiUrl: 'http://localhost:8080/api',
    webAppUrl: 'http://localhost:3000'
  }
});
