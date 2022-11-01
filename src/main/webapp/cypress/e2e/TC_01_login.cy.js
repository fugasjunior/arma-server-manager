describe('Login', () => {
    it('Log in', () => {
        cy.visit('http://localhost:3000');
        cy.get('header').should('not.exist');
        cy.get('#username').type('test');
        cy.get('#password').type('test');
        cy.get('button[type="submit"]').click();
        cy.get('header', {timeout: 5000}).should('be.visible');
    });

    it('Invalid login', () => {
        cy.visit('http://localhost:3000');
        cy.get('header').should('not.exist');
        cy.get('#username').type('test');
        cy.get('#password').type('wrong_password');
        cy.intercept('/api/login').as('login')
        cy.get('button[type="submit"]').click();
        cy.wait('@login').its('response.statusCode').should('equal', 401);
        cy.get('div[role="alert"]').should("be.visible");
        cy.get('header').should('not.exist');
    });
})