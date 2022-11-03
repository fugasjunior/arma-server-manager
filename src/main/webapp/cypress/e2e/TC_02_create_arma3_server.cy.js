describe('Create and manage Arma 3 server', () => {

    const values = {
        name: 'Test Arma 3 server',
        description: 'Test Arma 3 server created by Cypress',
        port: '2402',
        maxPlayers: '50',
        password: 'hunter2',
        adminPassword: 'sup3rs3cr3t4dm1nP4ssw0rd',
        clientFilePatching: true,
        serverFilePatching: true,
        verifySignatures: true,
        vonEnabled: true,
        battlEye: true,
        persist: true,
        additionalOptions: 'voteMissionPlayers = 1;\nvoteThreshold= 0.33;'
    }

    const updatedValues = {
        name: 'Updated Arma 3 server',
        description: 'Test Arma 3 server created and updated by Cypress',
        port: '2302',
        maxPlayers: '30',
        password: 'hunter3',
        adminPassword: 'sup3rs3cr3t4dm1nP4ssw0rd2022',
        clientFilePatching: false,
        serverFilePatching: false,
        verifySignatures: false,
        vonEnabled: false,
        battlEye: false,
        persist: false,
        additionalOptions: 'voteMissionPlayers = 4;\nvoteThreshold= 0.5;'
    }

    function validateForm(expectedData) {
        cy.get('#name').should('have.value', expectedData.name);
        cy.get('#description').should('have.value', expectedData.description);
        cy.get('#port').should('have.value', expectedData.port);
        cy.get('#maxPlayers').should('have.value', expectedData.maxPlayers);
        cy.get('#password').should('have.value', expectedData.password);
        cy.get('#adminPassword').should('have.value', expectedData.adminPassword);
        cy.get('#clientFilePatching').should(`${!expectedData.clientFilePatching && 'not.'}be.checked`);
        cy.get('#serverFilePatching').should(`${!expectedData.serverFilePatching && 'not.'}be.checked`);
        cy.get('#verifySignatures').should(`${!expectedData.verifySignatures && 'not.'}be.checked`);
        cy.get('#vonEnabled').should(`${!expectedData.vonEnabled && 'not.'}be.checked`);
        cy.get('#battlEye').should(`${!expectedData.battlEye && 'not.'}be.checked`);
        cy.get('#persist').should(`${!expectedData.persist && 'not.'}be.checked`);
        cy.get('#additionalOptions').should('have.value', values.additionalOptions);
    }

    before(() => {
        cy.loginViaAPI();
    });

    it('Creates Arma 3 server', () => {
        cy.visit(Cypress.env('webAppUrl') + '/servers');
        cy.get('#new-server-btn').click();
        cy.get('#new-arma3-server-btn').should('be.visible').click();

        cy.url().should('include', '/servers/new/ARMA3');
        cy.get('#manage-mods-btn').should('be.visible');
        cy.get('#manage-dlcs-btn').should('be.visible');

        cy.get('#name').type(values.name);
        cy.get('#description').type(values.description);
        cy.get('#port').clear().type(values.port);
        cy.get('#maxPlayers').clear().type(values.maxPlayers);
        cy.get('#password').type(values.password);
        cy.get('#adminPassword').type(values.adminPassword);
        cy.get('#clientFilePatching').click();
        cy.get('#serverFilePatching').click();
        cy.get('#additionalOptions').type(values.additionalOptions);
        cy.get('button[type="submit"').click();

        cy.url().should('include', '/servers');
        cy.get('.Toastify__toast--success').should("be.visible");
    });

    it('Verifies the created server', () => {
        cy.get('')
        validateForm(values);
    });

    it('Updates the created server', () => {
        cy.get('#name').clear().type(updatedValues.name);
        cy.get('#description').clear().type(updatedValues.description);
        cy.get('#port').clear().type(updatedValues.port);
        cy.get('#maxPlayers').clear().type(updatedValues.maxPlayers);
        cy.get('#password').clear().type(updatedValues.password);
        cy.get('#adminPassword').clear().type(updatedValues.adminPassword);
        cy.get('#clientFilePatching').click();
        cy.get('#serverFilePatching').click();
        cy.get('#verifySignatures').click();
        cy.get('#vonEnabled').click();
        cy.get('#battlEye').click();
        cy.get('#persist').click();
        cy.get('#additionalOptions').clear().type(updatedValues.additionalOptions);
        cy.get('button[type="submit"').click();

        cy.url().should('include', '/servers');
        cy.get('.Toastify__toast--success').should("be.visible");
    });

    it('Verifies the updated server', () => {
        // TODO
        validateForm(updatedValues);
    });

    it('Activates mods and DLCs for the created server', () => {
        //TODO
    });

    it('Verifies mods and DLCs still active for the created server', () => {
        //TODO
    });

    it('Deletes the created server', () => {
        //TODO
    });
})