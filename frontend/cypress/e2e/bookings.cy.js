describe('Мої бронювання', () => {
  beforeEach(() => {
    cy.loginAsUserSession();
    cy.intercept('GET', '**/bookings/my*').as('getBookings');
    cy.visit('/');
    cy.window().then((win) => {
      win.history.pushState({}, '', '/my-bookings');
    });
    cy.reload();
    cy.wait('@getBookings').its('response.statusCode').should('eq', 200);
  });

  it('Відображення списку бронювань', () => {
    cy.contains('Мої бронювання');
    cy.get('.booking-card').should('have.length.at.least', 1);
  });

  it('Скасування бронювання', () => {
    cy.get('.booking-card').then(($cards) => {
      const cancelable = $cards.filter((i, el) =>
        el.innerText.includes('очікує оплату')
      );

      if (cancelable.length > 0) {
        cy.wrap(cancelable[0]).within(() => {
          cy.contains('Скасувати').click();
        });
        cy.contains('Бронювання успішно скасовано!');
      } else {
        cy.log('✅ Немає бронювань, які можна скасувати');
      }
    });
  });

  it('Оплата бронювання', () => {
    cy.get('.booking-card').then(($cards) => {
      const payable = $cards.filter((i, el) =>
        el.innerText.includes('очікує оплату')
      );

      if (payable.length > 0) {
        cy.wrap(payable[0]).within(() => {
          cy.contains('Оплатити').click();
        });
        cy.url().should('include', '/payment/');
      } else {
        cy.log('✅ Немає бронювань для оплати');
      }
    });
  });
});
