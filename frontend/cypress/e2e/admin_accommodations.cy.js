describe('Admin – Помешкання', () => {
  beforeEach(() => {
    cy.loginAsAdminSession();
    cy.intercept('GET', '**/accommodations*').as('getAccommodations');
    cy.goTo('/admin/accommodations');
    cy.wait('@getAccommodations');
  });

  it('бачить список помешкань і може змінити статус', () => {
    cy.contains('Управління помешканнями');
    cy.get('table tbody tr').first().within(() => {
      cy.get('select').then(($select) => {
        const current = $select.val();
        cy.wrap($select)
          .find('option')
          .not(`[value="${current}"]`)
          .first()
          .then((option) => {
            cy.wrap($select).select(option.val());
          });
      });
    });
  });

  it('може відкрити форму додавання помешкання', () => {
    cy.contains('Додати помешкання').click();
    cy.url().should('include', '/admin/accommodations/new');
    cy.get('form').should('exist');
  });
});