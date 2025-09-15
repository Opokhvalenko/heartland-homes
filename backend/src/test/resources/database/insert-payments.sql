INSERT INTO payments (id, status, payment_type, booking_id, session_url, session_id, amount_to_pay, is_deleted)
VALUES
    (1, 'PAID', 'PAYMENT', 1, 'http://example.com/success1', 'sess_1', 400.00, false),
    (2, 'PENDING', 'PAYMENT', 2, 'http://example.com/success2', 'sess_2', 200.00, false),
    (3, 'PAID', 'FINE', 2, 'http://example.com/success3', 'sess_3', 50.00, false);
