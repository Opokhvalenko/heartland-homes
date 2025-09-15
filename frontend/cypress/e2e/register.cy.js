describe('Форма реєстрації', () => {
  beforeEach(() => {
    cy.visit('/register');
  });

  it.skip('Успішна реєстрація нового користувача', () => {
    const uniqueEmail = `test_${Date.now()}@booking.com`;

    cy.get('input[name="firstName"]').type('Іван');
    cy.get('input[name="lastName"]').type('Тестовий');
    cy.get('input[name="email"]').type(uniqueEmail);
    cy.get('input[name="password"]').type('password123');
    cy.get('input[name="confirmPassword"]').type('password123');
    cy.get('button[type="submit"]').click();

    //  редірект на сторінку логіна
    cy.url().should('include', '/login');
  });

  it('Помилка якщо email уже зайнятий', () => {
    cy.get('input[name="firstName"]').type('Іван');
    cy.get('input[name="lastName"]').type('Тестовий');
    cy.get('input[name="email"]').type('admin@booking.com');
    cy.get('input[name="password"]').type('password123');
    cy.get('input[name="confirmPassword"]').type('password123');
    cy.get('button[type="submit"]').click();

    cy.get('.notification-error').should('exist');
  });

  it('Помилка якщо паролі не співпадають', () => {
    cy.get('input[name="firstName"]').type('Іван');
    cy.get('input[name="lastName"]').type('Тестовий');
    cy.get('input[name="email"]').type('someuser@booking.com');
    cy.get('input[name="password"]').type('password123');
    cy.get('input[name="confirmPassword"]').type('wrongpass');
    cy.get('button[type="submit"]').click();

    cy.on('window:alert', (txt) => {
      expect(txt).to.contains('Паролі не співпадають');
    });
  });
});