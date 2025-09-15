describe('User flow', () => {
  beforeEach(() => {
    cy.loginAsUserSession();

    cy.intercept('GET', '**/bookings/my*').as('getBookings');

    cy.visit('/');
    cy.window().then((win) => {
      win.history.pushState({}, '', '/my-bookings');
    });
    cy.reload();

    cy.wait('@getBookings');
  });

  it('бачить свої бронювання', () => {
    cy.contains('Мої бронювання');
    cy.get('.booking-card').should('exist');
  });

 it('не має кнопки "Оплатити" для оплаченого бронювання', () => {
  cy.get('.booking-card')
    .contains(/оплачено/i) 
    .parents('.booking-card')
    .within(() => {
      cy.contains('Оплатити').should('not.exist');
      cy.contains(/оплачено/i).should('exist');
    });
});
});
