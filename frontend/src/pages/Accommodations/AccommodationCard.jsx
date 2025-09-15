import React from 'react';
import { Link } from 'react-router-dom';
import { mapType } from '../../utils/translations/index';
import { getSafeImageUrl } from '../../utils/getSafeImageUrl';

const fallbackImage = '/no-image.png';

const AccommodationCard = ({ accommodation }) => {
  const imageUrl = getSafeImageUrl(accommodation.image);

  const { label, icon, color } = mapType(accommodation.type);

  return (
    <div className="card-custom">
      <img
        src={imageUrl}
        alt={accommodation.location || 'Зображення житла'}
        className="card-img-top-custom"
        onError={(e) => (e.currentTarget.src = fallbackImage)}
      />

      <div className="card-body">
        <h3 className="card-title">{accommodation?.name || 'Без назви'}</h3>

        <div className="card-badges">
          <span className="badge badge-type" style={{ backgroundColor: color }}>
            {icon} {label}
          </span>

          {accommodation.size && (
            <span className="badge badge-size">
              {parseInt(accommodation.size, 10)}{' '}
              {parseInt(accommodation.size, 10) > 1 ? 'Спальні' : 'Спальня'}
            </span>
          )}
        </div>

        <p className="city-label">
          {accommodation?.location?.includes(accommodation?.city)
            ? accommodation?.location
            : `${accommodation?.city}, ${accommodation?.location}`}
        </p>

        <p className="card-price">{accommodation.dailyRate} грн / доба</p>

        <Link
          to={`/accommodations/${accommodation.id}`}
          className="btn btn-primary w-100"
        >
          Детальніше
        </Link>
      </div>
    </div>
  );
};

//  мемоізація для запобігання зайвим ререндерам
export default React.memo(AccommodationCard);
