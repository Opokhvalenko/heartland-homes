import React from 'react';
import { Link } from 'react-router-dom';
import { FaEdit, FaTrash } from 'react-icons/fa';
import StatusSelect from '../../components/selects/StatusSelect';
import { fixDropboxUrl } from '../../utils/fixDropboxUrl';
import { mapType } from '../../utils/translations';

const fallbackImage = '/assets/no-image.svg';

const AdminAccommodationCard = ({ acc, onStatusChange, onDelete }) => {
  const image = acc.image ? fixDropboxUrl(acc.image) : fallbackImage;
  const { label, icon, color } = mapType(acc.type);

  return (
    <div className="admin-accommodation-card">
      <img
        src={image}
        alt={acc.name || 'Помешкання'}
        className="accommodation-card-img"
        onError={(e) => (e.currentTarget.src = fallbackImage)}
      />

      <div className="accommodation-card-content">
        <div className="card-header">
          <h3 className="accommodation-title">{acc.name}</h3>
          <StatusSelect
            type="accommodation"
            value={acc.accommodationStatus}
            onChange={(newStatus) => onStatusChange(acc.id, newStatus)}
          />
        </div>

        {/*  Badge типу житла */}
        <div className="badge badge-type" style={{ backgroundColor: color }}>
          {icon} {label}
        </div>

        <div className="card-body">
          <p>
            <strong>Місто:</strong> {acc.city}
          </p>
          <p className="price">
            <strong>Ціна:</strong> {acc.dailyRate} грн
          </p>
        </div>

        <div className="admin-card-actions">
          <Link
            to={`/admin/accommodations/edit/${acc.id}`}
            className="btn-inline btn-secondary"
          >
            <FaEdit /> Редагувати
          </Link>
          <button className="btn-inline btn-danger" onClick={() => onDelete(acc.id)}>
            <FaTrash /> Видалити
          </button>
        </div>
      </div>
    </div>
  );
};

export default AdminAccommodationCard;
