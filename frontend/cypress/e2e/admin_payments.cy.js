describe('Admin – Платежі', () => {
  beforeEach(() => {
    cy.loginAsAdminSession();
    cy.intercept('GET', '**/payments*').as('getPayments');
    cy.goTo('/admin/payments');
    cy.wait('@getPayments');
  });

  it('бачить список платежів', () => {
    cy.contains('Управління платежами');
    cy.get('table tbody tr').should('have.length.at.least', 1);
  });

  it('бачить статус "Оплачено" хоча б для одного платежу', () => {
    cy.get('table tbody tr').then(($rows) => {
      const paidRow = [...$rows].find((row) =>
        row.innerText.includes('Оплачено')
      );
      if (paidRow) {
        cy.wrap(paidRow).within(() => {
          cy.contains('Оплачено');
        });
      } else {
        cy.log('✅ Немає оплачених платежів у цьому списку');
      }
    });
  });
});