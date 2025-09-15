import React from 'react';
import StatusBadge from './status/StatusBadge';
import { normalizeBooking } from '../utils/normalizeBooking';

const BookingStatusBlock = ({ booking }) => {
  const enrichedBooking = normalizeBooking(booking);

  if (!enrichedBooking) return null;

  return (
    <div className="booking-status-block">
      <p>
        <strong>Статус бронювання:</strong>{' '}
        <StatusBadge status={enrichedBooking.status} />
      </p>

      <p>
        <strong>Оплата:</strong>{' '}
        {enrichedBooking.payment?.status ? (
          <StatusBadge status={enrichedBooking.payment.status} />
        ) : (
          <span className="badge badge-status badge-status-unknown">—</span>
        )}
      </p>
    </div>
  );
};

export default BookingStatusBlock;
