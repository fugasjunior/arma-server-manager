// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })

Cypress.Commands.add('loginViaAPI', (
        username = Cypress.env('username'),
        password = Cypress.env('password')
) => {
    const formData = new FormData();
    formData.append("username", username);
    formData.append("password", password);

    cy.request('POST', `${Cypress.env('apiUrl')}/login`, formData)
    .then((resp) => {
        const bodyString = Cypress.Blob.arrayBufferToBinaryString(resp.body);
        const body = JSON.parse(bodyString);
        let date = new Date();
        date = new Date(+date + body['expiresIn']);
        localStorage.setItem('token', body['token']);
        localStorage.setItem('expirationTime', date.toISOString());
    });
})