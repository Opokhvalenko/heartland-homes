import React, { useEffect, useState, useMemo } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { fetchBookingById, cancelBooking } from '../api/bookings/bookingsService';
import { getAccommodationById } from '../api/accommodations/accommodationService';
import { useDispatch, useSelector } from 'react-redux';
import { deleteBooking } from '../store/slices/bookingsSlice';
import { fetchPaymentsByUser } from '../store/slices/paymentsSlice';
import { fixDropboxUrl } from '../utils/fixDropboxUrl';
import { mapStatus } from '../utils/translations';
import Notification from '../components/Notification';
import { FaTrash } from 'react-icons/fa';

import '../styles/components/booking/_booking-card.scss';
import '../styles/components/booking/_booking-details.scss';

const fallbackImage = '/no-image.png';

const BookingDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { user } = useSelector((state) => state.auth);
  const { payments } = useSelector((state) => state.payments);

  const [booking, setBooking] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // завантаження бронювання
  useEffect(() => {
    const fetchBooking = async () => {
      try {
        const bookingData = await fetchBookingById(id);
        if (!bookingData) {
          setError('Бронювання не знайдено.');
          setLoading(false);
          return;
        }

        let accommodation = null;
        try {
          accommodation = await getAccommodationById(bookingData.accommodationId);
        } catch (err) {
          console.warn('⚠️ Не вдалося отримати помешкання:', err);
        }

        setBooking({ ...bookingData, accommodation });
      } catch (err) {
        console.error('❌ Помилка завантаження бронювання:', err);
        setError('Не вдалося завантажити деталі бронювання.');
      } finally {
        setLoading(false);
      }
    };

    fetchBooking();
  }, [id]);

  // завантаження платежів користувача
  useEffect(() => {
    if (user?.id) {
      dispatch(fetchPaymentsByUser({ userId: user.id, pageable: { page: 0, size: 50 } }));
    }
  }, [user, dispatch]);

  // об’єднання бронювання з оплатою
  const enrichedBooking = useMemo(() => {
    if (!booking) return null;
    const payment = payments.find((p) => p.bookingId === booking.id);

    let fixedStatus = booking.status;
    if (payment?.status === 'PAID' && booking.status === 'PENDING') {
      fixedStatus = 'CONFIRMED';
    }

    return { ...booking, payment, status: fixedStatus };
  }, [booking, payments]);

  const isPaid = enrichedBooking?.payment?.status === 'PAID';
  const imageUrl = enrichedBooking?.accommodation?.image
    ? fixDropboxUrl(enrichedBooking.accommodation.image)
    : fallbackImage;

  const { label: statusLabel, color: statusColor } = enrichedBooking
    ? mapStatus(enrichedBooking.status)
    : { label: '—', color: '#ccc' };

  // розрахунок ціни
  const checkIn = enrichedBooking ? new Date(enrichedBooking.checkInDate) : null;
  const checkOut = enrichedBooking ? new Date(enrichedBooking.checkOutDate) : null;
  const nights = checkIn && checkOut ? (checkOut - checkIn) / (1000 * 60 * 60 * 24) : 0;

  const totalPrice =
    enrichedBooking?.totalPrice ||
    (enrichedBooking?.accommodation?.dailyRate
      ? enrichedBooking.accommodation.dailyRate * nights
      : null);

  const handleCancel = async () => {
    if (window.confirm('Ви впевнені, що хочете скасувати бронювання?')) {
      try {
        await cancelBooking(id);
        setBooking((prev) => ({ ...prev, status: 'CANCELED' }));
      } catch (err) {
        console.error('❌ Помилка скасування бронювання:', err);
        setError('Не вдалося скасувати бронювання.');
      }
    }
  };

  const handleDelete = async () => {
    if (window.confirm('Ви впевнені, що хочете видалити бронювання?')) {
      try {
        await dispatch(deleteBooking(id)).unwrap();
        navigate('/my-bookings');
      } catch (err) {
        console.error('❌ Помилка видалення бронювання:', err);
        setError('Не вдалося видалити бронювання.');
      }
    }
  };

  const handlePay = () => {
    navigate(`/payment/${enrichedBooking.id}`, {
      state: {
        amount: totalPrice,
        accommodation: enrichedBooking.accommodation,
        dates: {
          checkIn: enrichedBooking.checkInDate,
          checkOut: enrichedBooking.checkOutDate
        }
      }
    });
  };

  if (loading) return <p className="text-center">Завантаження...</p>;
  if (error) return <Notification message={error} type="danger" />;
  if (!enrichedBooking) return <p className="text-center">Бронювання не знайдено</p>;

  return (
    <div className="container booking-details-page">
      <h1 className="section-heading">Деталі бронювання</h1>

      <div className="details-grid">
        {/* Ліва частина */}
        <div className="booking-info-card">
          <img src={imageUrl} alt="Помешкання" className="booking-image" />
          <div className="booking-info-header">
            <h3 className="card-title">
              {enrichedBooking.accommodation?.name || 'Помешкання'}
            </h3>
            <p className="card-subtitle">
              {enrichedBooking.accommodation?.city || '—'},{' '}
              {enrichedBooking.accommodation?.location || '—'}
            </p>
          </div>

          <div className="booking-details-content">
            <p>
              <strong>Дата заїзду:</strong> {enrichedBooking.checkInDate}
            </p>
            <p>
              <strong>Дата виїзду:</strong> {enrichedBooking.checkOutDate}
            </p>
            <p>
              <strong>Статус:</strong>{' '}
              <span className="badge" style={{ backgroundColor: statusColor }}>
                {statusLabel}
              </span>
            </p>
            <p>
              <strong>Оплата:</strong>{' '}
              {isPaid ? (
                <span className="badge badge-success">Оплачено</span>
              ) : (
                <span className="badge badge-warning">Не оплачено</span>
              )}
            </p>
            <p className="total-price">
              <strong>Загальна ціна:</strong>{' '}
              {totalPrice ? (
                <span className="price">{totalPrice} грн</span>
              ) : (
                <span className="text-muted">—</span>
              )}
            </p>
          </div>
        </div>

        {/* Права частина */}
        <div className="actions-card">
          <div className="booking-price">
            <span className="price">{totalPrice || '—'}</span>
            <span className="currency">грн</span>
          </div>

          {!isPaid && enrichedBooking.status === 'PENDING' && (
            <button className="btn btn-success" onClick={handlePay}>
              Оплатити
            </button>
          )}
          {!isPaid && enrichedBooking.status !== 'CANCELED' && (
            <button className="btn btn-danger" onClick={handleCancel}>
              Скасувати
            </button>
          )}
          {enrichedBooking.status === 'CANCELED' && (
            <button className="btn btn-secondary" onClick={handleDelete}>
              Видалити <FaTrash />
            </button>
          )}

          <Link to="/my-bookings" className="btn btn-secondary">
            Повернутися до списку
          </Link>
        </div>
      </div>
    </div>
  );
};

export default BookingDetails;
