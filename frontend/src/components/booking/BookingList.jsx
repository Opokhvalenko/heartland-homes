import React from 'react';
import BookingCard from './BookingCard';
import BookingRow from './BookingRow';
import Notification from '../Notification';
import Pagination from '../Pagination';

import '../../styles/components/booking/_bookings.scss';

const BookingList = ({
  bookings = [],
  view = 'card',
  onCancel,
  onPay,
  onDelete,
  page,
  totalPages,
  onPageChange,
  error
}) => {
  if (error) return <Notification message={error} type="danger" />;

  if (!bookings || bookings.length === 0) {
    return <p className="text-center mt-5">Бронювань поки що немає.</p>;
  }

  return (
    <div className="booking-list">
      {view === 'card' ? (
        <div className="booking-card-list">
          {bookings.map((booking) => (
            <BookingCard
              key={booking.id}
              booking={booking}
              onCancel={onCancel}
              onPay={onPay}
              onDelete={onDelete}
            />
          ))}
        </div>
      ) : (
        <div className="bookings-table">
          {bookings.map((booking) => (
            <BookingRow
              key={booking.id}
              booking={booking}
              onCancel={onCancel}
              onPay={onPay}
              onDelete={onDelete}
            />
          ))}
        </div>
      )}

      {totalPages > 0 && (
        <div className="pagination-wrapper">
          <Pagination page={page} totalPages={totalPages} onPageChange={onPageChange} />
        </div>
      )}
    </div>
  );
};

export default BookingList;
