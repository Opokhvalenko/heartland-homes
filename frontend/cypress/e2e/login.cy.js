describe('Форма логіна', () => {
  context('Desktop (≥1024px)', () => {
    beforeEach(() => {
      cy.viewport(1280, 800); // 💻 десктоп
      cy.visit('http://localhost:5173/login');
    });

    it('Успішний логін адміна', () => {
      cy.get('input[name="email"]').type('admin@booking.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();

      //  Адмін бачить пункт меню "Адмін-панель" у десктопному меню
      cy.get('.nav-desktop').contains('Адмін-панель').should('exist');
    });

    it('Успішний логін юзера', () => {
      cy.get('input[name="email"]').type('john.doe@booking.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();

      //  У юзера немає "Адмін-панель"
      cy.get('.nav-desktop').contains('Адмін-панель').should('not.exist');

      // ✅ Є "Мої бронювання"
      cy.get('.nav-desktop').contains('Мої бронювання').should('exist');
    });
  });

  context('Mobile (<1024px)', () => {
    beforeEach(() => {
      cy.viewport(375, 812); // 📱 iPhone X
      cy.visit('http://localhost:5173/login');
    });

    it('Успішний логін адміна', () => {
      cy.get('input[name="email"]').type('admin@booking.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();

      //  відкриваємо бургер
      cy.get('.burger').click();

      //  Адмін бачить "Адмін-панель" у drawer
      cy.get('.drawer').contains('Адмін-панель').should('exist');
    });

    it('Успішний логін юзера', () => {
      cy.get('input[name="email"]').type('john.doe@booking.com');
      cy.get('input[name="password"]').type('password123');
      cy.get('button[type="submit"]').click();

      //  відкриваємо бургер
      cy.get('.burger').click();

      //  у drawer немає "Адмін-панель"
      cy.get('.drawer').contains('Адмін-панель').should('not.exist');

      //  є "Мої бронювання"
      cy.get('.drawer').contains('Мої бронювання').should('exist');
    });
  });

  it('Помилка при неправильному паролі', () => {
  cy.visit('/login');

  cy.get('input[name="email"]').type('john.doe@booking.com');
  cy.get('input[name="password"]').type('wrongpass');
  cy.get('button[type="submit"]').click();

  cy.get('[data-testid="login-error"]', { timeout: 6000 })
    .should('exist');
})
});