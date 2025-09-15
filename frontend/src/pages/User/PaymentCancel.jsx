import React from 'react';
import PageWrapper from '../../components/PageWrapper';
import { Link } from 'react-router-dom';

const PaymentCancel = () => {
  return (
    <PageWrapper title="Оплату скасовано">
      <div className="payment-page">
        <div className="payment-card">
          <h2 className="payment-title" style={{ color: '#dc2626' }}>
            Оплату скасовано
          </h2>
          <p className="payment-subtitle">
            Ви скасували процес оплати. Ви можете спробувати ще раз.
          </p>
          <Link to="/my-bookings" className="payment-button">
            Повернутися до моїх бронювань
          </Link>
        </div>
      </div>
    </PageWrapper>
  );
};

export default PaymentCancel;
