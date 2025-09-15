import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { BookingForm } from '../../components/booking';
import BaseMap from '../../components/BaseMap';
import AccommodationGallery from './AccommodationGallery';
import { getAccommodationById } from '../../api/accommodations/accommodationService';
import { useSelector } from 'react-redux';

import { mapType, mapAmenity } from '../../utils/translations';
import { getSafeImageUrl } from '../../utils/getSafeImageUrl';

import '../../styles/components/accommodation/_accommodation-details.scss';
import '../../styles/components/accommodation/_accommodation-gallery.scss';
import '../../styles/components/badges/_badges.scss';

const AccommodationDetails = ({ id: propId }) => {
  //  або беремо id з props, або з useParams
  const { id: routeId } = useParams();
  const id = propId || routeId;

  const [accommodation, setAccommodation] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const { isAuthenticated } = useSelector((state) => state.auth);

  useEffect(() => {
    if (!id) return;
    const fetchAccommodation = async () => {
      try {
        const data = await getAccommodationById(id);
        if (!data) {
          setError('Помешкання не знайдено.');
          return;
        }
        setAccommodation(data);
      } catch (err) {
        console.error('❌ Помилка завантаження житла:', err);
        setError('Не вдалося завантажити деталі помешкання.');
      } finally {
        setLoading(false);
      }
    };
    fetchAccommodation();
  }, [id]);

  if (loading) return <div className="loading">Завантаження...</div>;
  if (error) return <div className="error">{error}</div>;
  if (!accommodation) return <div className="not-found">Помешкання не знайдено.</div>;

  const {
    label: typeLabel,
    icon: typeIcon,
    color: typeColor
  } = mapType(accommodation.type);

  return (
    <div className="accommodation-details-page">
      {/* Заголовок */}
      <div className="page-header">
        <h2 className="page-title">Деталі помешкання</h2>
        <h3 className="page-subtitle">
          <strong>{accommodation?.name || 'Без назви'}</strong>
        </h3>
        <p className="page-subtitle">
          {accommodation?.city}, {accommodation?.location}
        </p>
      </div>

      {/* Контент */}
      <div className="details-grid">
        {/* Ліва частина */}
        <div className="left-column">
          <div className="gallery-wrapper">
            <AccommodationGallery
              images={
                accommodation.images?.length
                  ? accommodation.images.map((img) => getSafeImageUrl(img))
                  : accommodation.image
                    ? [getSafeImageUrl(accommodation.image)]
                    : []
              }
            />
          </div>

          <div className="details-card">
            <h4 className="details-section-title">Інформація</h4>

            <div className="characteristics-inline">
              <span className="badge badge-type" style={{ backgroundColor: typeColor }}>
                {typeIcon} {typeLabel}
              </span>
              <span className="badge badge-size">
                {parseInt(accommodation.size, 10)}{' '}
                {parseInt(accommodation.size, 10) > 1 ? 'Спальні' : 'Спальня'}
              </span>
            </div>

            <div className="mt-3">
              <strong>Зручності:</strong>
              <div className="badge-group mt-2">
                {accommodation?.amenities?.length > 0 ? (
                  accommodation.amenities.map((amenity, index) => {
                    const { label, icon, slug, color } = mapAmenity(amenity);
                    return (
                      <span
                        key={index}
                        className={`badge badge-amenity ${slug}`}
                        style={{ backgroundColor: color, color: '#fff' }}
                      >
                        {icon} {label}
                      </span>
                    );
                  })
                ) : (
                  <span className="text-muted">немає даних</span>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* Права частина */}
        <div className="right-column">
          <div className="booking-details-card">
            <h5 className="booking-title">Забронювати</h5>

            <div className="booking-price">
              <div className="price-row">
                <span className="price">{accommodation?.dailyRate || '—'}</span>
                <span className="currency">грн</span>
              </div>
              <div className="per-night">/ доба</div>
            </div>

            {isAuthenticated ? (
              <BookingForm accommodation={accommodation} />
            ) : (
              <div className="text-center">
                <p className="text-muted mb-2">
                  Для бронювання необхідно увійти в систему.
                </p>
                <Link to="/login" className="btn btn-primary w-100">
                  Увійти
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Карта вниз на всю ширину */}
      {accommodation?.latitude && accommodation?.longitude && (
        <div className="location-map-full">
          <h4 className="details-section-title">Розташування</h4>
          <BaseMap
            items={[accommodation]}
            renderPopup={(acc) => (
              <div style={{ width: '150px' }}>
                <img
                  src={getSafeImageUrl(acc.image) || '/no-image.png'}
                  alt={acc.name}
                  style={{ width: '100%', borderRadius: '6px', marginBottom: '6px' }}
                  onError={(e) => (e.currentTarget.src = '/no-image.png')}
                />
                <strong>{acc.name}</strong>
                <div>{acc.city}</div>
              </div>
            )}
          />
        </div>
      )}
    </div>
  );
};

export default AccommodationDetails;
