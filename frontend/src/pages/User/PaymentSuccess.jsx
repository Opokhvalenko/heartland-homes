import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../styles/components/payment/_payment-checkout.scss';

const PaymentSuccess = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // перенаправлення через 2 секунди
    const timer = setTimeout(() => {
      navigate('/my-payments');
    }, 2000);

    return () => clearTimeout(timer);
  }, [navigate]);

  return (
    <div className="payment-page">
      <div className="payment-card payment-checkout">
        <h2 className="payment-title" style={{ color: '#16a34a' }}>
          🎉 Оплату успішно завершено! 🎉
        </h2>
        <p className="payment-subtitle">
          Дякуємо за вашу оплату. Ви будете перенаправлені на сторінку &quot;Мої
          платежі&quot;.
        </p>
      </div>
    </div>
  );
};

export default PaymentSuccess;
