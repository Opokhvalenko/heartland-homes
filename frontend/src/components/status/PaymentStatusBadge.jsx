import React from 'react';
import StatusBadge from './StatusBadge';

const PaymentStatusBadge = ({ status }) => {
  // Кастомні переклади саме для платежів
  const labels = {
    PAID: { text: 'Оплачено', className: 'success' },
    PENDING: { text: 'Очікує', className: 'warning' },
    FAILED: { text: 'Помилка', className: 'danger' }
  };

  const label = labels[status] || { text: status, className: 'secondary' };

  return <span className={`badge badge-status ${label.className}`}>{label.text}</span>;
};

export default PaymentStatusBadge;
