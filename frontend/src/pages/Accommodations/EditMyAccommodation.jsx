import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Notification from '../../components/Notification';
import {
  getAccommodationById,
  updateMyAccommodation
} from '../../api/accommodations/accommodationService';

import { MapContainer, TileLayer, Marker, useMapEvents } from 'react-leaflet';
import L from 'leaflet';
import setupLeaflet from '../../utils/leafletConfig';
import 'leaflet/dist/leaflet.css';

setupLeaflet();

const defaultIcon = new L.Icon({
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  iconRetinaUrl:
    'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  shadowUrl:
    'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41]
});

const LocationPicker = ({ setCoordinates }) => {
  useMapEvents({
    click(e) {
      setCoordinates({
        latitude: e.latlng.lat.toFixed(6),
        longitude: e.latlng.lng.toFixed(6)
      });
    }
  });
  return null;
};

const EditMyAccommodation = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    getAccommodationById(id)
      .then((data) => setFormData(data))
      .catch(() => setError('Не вдалося завантажити помешкання'));
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const setCoordinates = ({ latitude, longitude }) => {
    setFormData((prev) => ({
      ...prev,
      latitude,
      longitude
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      const payload = {
        name: formData.name?.trim(),
        type: formData.type,
        location: formData.location?.trim() || '—',
        city: formData.city?.trim() || 'Київ',
        latitude: String(formData.latitude || ''),
        longitude: String(formData.longitude || ''),
        size: formData.size?.trim() || '—',
        amenities: Array.isArray(formData.amenities)
          ? formData.amenities
          : String(formData.amenities || '')
              .split(',')
              .map((a) => a.trim())
              .filter(Boolean),
        dailyRate: Number(formData.dailyRate) || 0,
        image: formData.image?.trim() || ''
      };

      console.log('👉 Payload (редагування):', payload);

      await updateMyAccommodation(id, payload);
      navigate('/my-accommodations');
    } catch (err) {
      console.error('❌ Backend error:', err.response?.data || err);
      setError(err.response?.data?.message || 'Помилка при оновленні');
    } finally {
      setLoading(false);
    }
  };

  if (!formData) return <p>Завантаження...</p>;

  return (
    <div className="container page">
      <form onSubmit={handleSubmit} className="admin-form">
        <h1>✏️ Редагувати моє помешкання</h1>
        {error && <Notification message={error} type="danger" />}

        {/* Назва */}
        <div className="form-group">
          <label>Назва</label>
          <input
            type="text"
            name="name"
            value={formData.name || ''}
            onChange={handleChange}
          />
        </div>

        {/* Тип */}
        <div className="form-group">
          <label>Тип</label>
          <select name="type" value={formData.type || 'HOUSE'} onChange={handleChange}>
            <option value="HOUSE">Будинок</option>
            <option value="APARTMENT">Квартира</option>
            <option value="HOTEL">Готель</option>
            <option value="VACATION_HOME">Дім для відпочинку</option>
            <option value="HOSTEL">Хостел</option>
          </select>
        </div>

        {/* Локація */}
        <div className="form-group">
          <label>Локація</label>
          <input
            type="text"
            name="location"
            value={formData.location || ''}
            onChange={handleChange}
          />
        </div>

        {/* Місто */}
        <div className="form-group">
          <label>Місто</label>
          <input
            type="text"
            name="city"
            value={formData.city || ''}
            onChange={handleChange}
          />
        </div>

        {/* Розмір */}
        <div className="form-group">
          <label>Розмір (наприклад, 50м²)</label>
          <input
            type="text"
            name="size"
            value={formData.size || ''}
            onChange={handleChange}
          />
        </div>

        {/* Карта */}
        <div className="form-group">
          <label>Виберіть розташування на карті</label>
          <div style={{ height: '300px', width: '100%', marginBottom: '1rem' }}>
            <MapContainer
              center={[formData.latitude || 50.45, formData.longitude || 30.52]}
              zoom={12}
              style={{ height: '100%', width: '100%' }}
            >
              <TileLayer
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                attribution="&copy; OpenStreetMap contributors"
              />
              <LocationPicker setCoordinates={setCoordinates} />
              {formData.latitude && formData.longitude && (
                <Marker
                  position={[formData.latitude, formData.longitude]}
                  icon={defaultIcon}
                />
              )}
            </MapContainer>
          </div>
          {formData.latitude && formData.longitude && (
            <p>
              📍 Обрані координати: {formData.latitude}, {formData.longitude}
            </p>
          )}
        </div>

        {/* Зручності */}
        <div className="form-group">
          <label>Зручності (через кому)</label>
          <input
            type="text"
            name="amenities"
            value={
              Array.isArray(formData.amenities)
                ? formData.amenities.join(', ')
                : formData.amenities || ''
            }
            onChange={(e) =>
              setFormData((prev) => ({
                ...prev,
                amenities: e.target.value.split(',').map((a) => a.trim())
              }))
            }
          />
        </div>

        {/* Daily rate */}
        <div className="form-group">
          <label>Ціна за добу</label>
          <input
            type="number"
            name="dailyRate"
            value={formData.dailyRate || ''}
            onChange={handleChange}
          />
        </div>

        {/* Image */}
        <div className="form-group">
          <label>URL зображення</label>
          <input
            type="text"
            name="image"
            value={formData.image || ''}
            onChange={handleChange}
            placeholder="https://example.com/image.jpg"
          />
        </div>

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Оновлення...' : 'Оновити'}
        </button>
      </form>
    </div>
  );
};

export default EditMyAccommodation;
