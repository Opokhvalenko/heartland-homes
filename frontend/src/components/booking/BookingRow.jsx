import React from 'react';
import { Link } from 'react-router-dom';
import { FaTrash, FaInfoCircle } from 'react-icons/fa';
import { mapStatus } from '../../utils/translations';

const BookingRow = ({ booking, onStatusChange, onDelete }) => {
  const { label, color, slug } = mapStatus(booking.status);

  return (
    <tr>
      <td>{booking.id}</td>
      <td>{booking.user ? `${booking.user.firstName} ${booking.user.lastName}` : '—'}</td>
      <td>
        {booking.accommodation
          ? `${booking.accommodation.name} (${booking.accommodation.city})`
          : '—'}
      </td>
      <td>{booking.checkInDate}</td>
      <td>{booking.checkOutDate}</td>
      <td>
        <span
          className={`status-badge status-${slug}`}
          style={{ backgroundColor: color }}
        >
          {label}
        </span>
      </td>
      <td className="actions">
        <select
          value={booking.status}
          onChange={(e) => onStatusChange(booking.id, e.target.value)}
        >
          <option value="PENDING">Очікує</option>
          <option value="CONFIRMED">Підтверджено</option>
          <option value="CANCELED">Скасовано</option>
          <option value="EXPIRED">Прострочено</option>
        </select>

        <Link to={`/admin/bookings/${booking.id}`} className="btn-secondary btn-sm">
          <FaInfoCircle />
        </Link>

        <button className="btn-danger btn-sm" onClick={() => onDelete(booking.id)}>
          <FaTrash />
        </button>
      </td>
    </tr>
  );
};

export default BookingRow;
