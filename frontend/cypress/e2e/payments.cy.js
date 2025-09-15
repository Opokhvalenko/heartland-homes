describe('Мої платежі', () => {
  beforeEach(() => {
    cy.loginAsUserSession();
    cy.intercept('GET', '**/payments*').as('getPayments');

    cy.visit('/');
    cy.window().then((win) => {
      win.history.pushState({}, '', '/my-payments');
    });
    cy.reload();

    cy.wait('@getPayments');
  });

  it('Відображення списку платежів', () => {
    cy.contains('Мої платежі');
    cy.get('.payment-card').should('exist');
  });

  it('Оплата відкритого платежу', () => {
    cy.get('body').then(($body) => {
      if ($body.text().includes('Оплатити')) {
        cy.get('.payment-card').contains('Оплатити').click();
        cy.url().should('include', 'http'); // редірект на сторонній платіж
      } else {
        cy.log('✅ Усі платежі вже оплачені');
      }
    });
  });
});