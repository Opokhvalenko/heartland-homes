import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Notification from '../../components/Notification';
import {
  getAccommodationById,
  updateAccommodation
} from '../../api/accommodations/accommodationService.js';
import { mapType, typeTranslations, mapAmenity } from '../../utils/translations/index.js';
import { getSafeImageUrl } from '../../utils/getSafeImageUrl';

import '../../styles/components/admin/_admin-form.scss';

const AdminEditAccommodation = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    type: '',
    location: '',
    city: '',
    latitude: '',
    longitude: '',
    size: '',
    amenities: '',
    dailyRate: '',
    image: ''
  });

  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  // завантаження даних
  useEffect(() => {
    const fetchAccommodation = async () => {
      setLoading(true);
      try {
        const data = await getAccommodationById(id);
        setFormData({
          ...data,
          amenities: data.amenities?.join(', ') || ''
        });
      } catch {
        setError('Не вдалося завантажити дані помешкання');
      } finally {
        setLoading(false);
      }
    };
    fetchAccommodation();
  }, [id]);

  //  обробка змін
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // сабміт форми
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const payload = {
        ...formData,
        amenities: formData.amenities
          .split(',')
          .map((a) => a.trim())
          .filter(Boolean)
      };
      await updateAccommodation(id, payload);
      navigate('/admin/accommodations');
    } catch (err) {
      setError(err.response?.data?.message || 'Помилка при оновленні');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <p className="text-center">⏳ Завантаження...</p>;

  return (
    <div className="container page">
      <form onSubmit={handleSubmit} className="admin-form">
        <h1>Редагувати помешкання</h1>
        {error && <Notification message={error} type="danger" />}

        {/* Назва */}
        <div className="form-group">
          <label>Назва</label>
          <input type="text" name="name" value={formData.name} onChange={handleChange} />
        </div>

        {/* Тип */}
        <div className="form-group">
          <label>Тип</label>
          <select name="type" value={formData.type} onChange={handleChange}>
            <option value="">— Оберіть тип —</option>
            {Object.entries(typeTranslations).map(([key, { label }]) => (
              <option key={key} value={key}>
                {label}
              </option>
            ))}
          </select>

          {formData.type && (
            <div className="badge-group" style={{ marginTop: '0.5rem' }}>
              {(() => {
                const type = mapType(formData.type);
                return (
                  <span className={`badge badge-type-${formData.type.toLowerCase()}`}>
                    {type.icon} {type.label}
                  </span>
                );
              })()}
            </div>
          )}
        </div>

        {/* Локація */}
        <div className="form-group">
          <label>Локація</label>
          <input
            type="text"
            name="location"
            value={formData.location}
            onChange={handleChange}
          />
        </div>

        {/* Місто */}
        <div className="form-group">
          <label>Місто</label>
          <input type="text" name="city" value={formData.city} onChange={handleChange} />
        </div>

        {/* Latitude */}
        <div className="form-group">
          <label>Широта (Latitude)</label>
          <input
            type="text"
            name="latitude"
            value={formData.latitude}
            onChange={handleChange}
          />
        </div>

        {/* Longitude */}
        <div className="form-group">
          <label>Довгота (Longitude)</label>
          <input
            type="text"
            name="longitude"
            value={formData.longitude}
            onChange={handleChange}
          />
        </div>

        {/* Size */}
        <div className="form-group">
          <label>Кількість спалень</label>
          <input
            type="number"
            name="size"
            value={formData.size}
            onChange={handleChange}
          />
        </div>

        {/* Amenities */}
        <div className="form-group">
          <label>Зручності (через кому)</label>
          <input
            type="text"
            name="amenities"
            value={formData.amenities}
            onChange={handleChange}
          />
          <div className="badge-group" style={{ marginTop: '0.5rem' }}>
            {formData.amenities
              .split(',')
              .map((a) => a.trim())
              .filter(Boolean)
              .map((a, idx) => {
                const { label, icon, slug, color } = mapAmenity(a);
                return (
                  <span
                    key={idx}
                    className={`badge badge-amenity ${slug}`}
                    style={{ backgroundColor: color, color: '#fff' }}
                  >
                    {icon} {label}
                  </span>
                );
              })}
          </div>
        </div>

        {/* Daily rate */}
        <div className="form-group">
          <label>Ціна за добу</label>
          <input
            type="number"
            name="dailyRate"
            value={formData.dailyRate}
            onChange={handleChange}
          />
        </div>

        {/* Image */}
        <div className="form-group">
          <label>URL зображення</label>
          <input
            type="text"
            name="image"
            value={formData.image}
            onChange={handleChange}
          />
          {formData.image && (
            <div className="image-preview">
              <img
                src={getSafeImageUrl(formData.image)}
                alt="Превʼю зображення"
                className="preview-img"
              />
            </div>
          )}
        </div>

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Оновлення...' : 'Зберегти'}
        </button>
      </form>
    </div>
  );
};

export default AdminEditAccommodation;
