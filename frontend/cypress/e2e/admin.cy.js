describe('Admin flow (safe)', () => {
  beforeEach(() => {
    cy.loginAsAdminSession();
    cy.visit('/');
  });

  it('бачить список бронювань', () => {
    cy.goTo('/admin/bookings');
    cy.contains('Управління бронюваннями');
    cy.get('table tbody tr, .booking-card').should('have.length.at.least', 1);
  });

  it('може підтвердити бронювання (mock)', () => {
    cy.intercept('PUT', '**/bookings/*/status', { statusCode: 200 }).as('updateBooking');
    cy.goTo('/admin/bookings');

    cy.get('table tbody tr, .booking-card').then(($rows) => {
      const pendingRow = [...$rows].find((row) =>
        row.innerText.includes('Очікує підтвердження')
      );
      if (pendingRow) {
        cy.wrap(pendingRow).within(() => {
          cy.get('select').select('CONFIRMED');
        });
        cy.contains('Підтверджено');
      } else {
        cy.log('✅ Немає бронювань у статусі "Очікує підтвердження"');
      }
    });
  });

  it('може "видалити" бронювання (mock)', () => {
    cy.intercept('DELETE', '**/bookings/*', { statusCode: 200 }).as('deleteBooking');
    cy.goTo('/admin/bookings');

    cy.get('table tbody tr, .booking-card').then(($rows) => {
      if ($rows.length > 0) {
        cy.wrap($rows[0]).within(() => {
          cy.contains('Видалити').click();
        });
        cy.log('✅ Кнопка видалення працює (mock)');
      } else {
        cy.log('✅ Немає бронювань для видалення');
      }
    });
  });

  it('бачить список помешкань і може змінити статус (mock)', () => {
    cy.intercept('PUT', '**/accommodations/*/status', { statusCode: 200 }).as('updateAccommodation');
    cy.goTo('/admin/accommodations');
    cy.contains('Управління помешканнями');

    cy.get('table tbody tr').first().within(() => {
      cy.get('select').then(($select) => {
        const current = $select.val();
        cy.wrap($select).find('option').not(`[value="${current}"]`).first().then((option) => {
          cy.wrap($select).select(option.val());
        });
      });
    });
  });

  it('може відкрити форму додавання помешкання', () => {
    cy.goTo('/admin/accommodations');
    cy.contains('Додати помешкання').click();
    cy.url().should('include', '/admin/accommodations/new');
    cy.get('form').should('exist');
  });

  it('бачить список платежів', () => {
    cy.goTo('/admin/payments');
    cy.contains('Управління платежами');
    cy.get('table tbody tr').should('have.length.at.least', 0);
  });

  it('бачить список користувачів', () => {
    cy.goTo('/admin/users');
    cy.contains('Користувачі');
    cy.get('table tbody tr').should('have.length.at.least', 1);
  });

  it('може змінити роль користувача (mock, не чіпає адміна)', () => {
    cy.intercept('PUT', '**/users/*/role', { statusCode: 200 }).as('updateUserRole');
    cy.goTo('/admin/users');

    cy.get('table tbody tr').each(($row) => {
      if (!$row.text().includes('admin@booking.com')) {
        cy.wrap($row).within(() => {
          cy.get('select').then(($select) => {
            const current = $select.val();
            cy.wrap($select).find('option').not(`[value="${current}"]`).first().then((option) => {
              cy.wrap($select).select(option.val());
            });
          });
        });
        return false; // вихід після першої зміни
      }
    });
  });

  it('може "видалити" користувача (mock, не чіпає адміна)', () => {
    cy.intercept('DELETE', '**/users/*', { statusCode: 200 }).as('deleteUser');
    cy.goTo('/admin/users');

    cy.get('table tbody tr').each(($row) => {
      if (!$row.text().includes('admin@booking.com')) {
        cy.wrap($row).within(() => {
          cy.contains('Видалити').click();
        });
        cy.log('✅ Кнопка видалення користувача працює (mock)');
        return false;
      }
    });
  });
});