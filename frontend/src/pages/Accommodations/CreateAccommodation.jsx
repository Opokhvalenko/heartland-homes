import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Notification from '../../components/Notification';
import { createAccommodation } from '../../api/accommodations/accommodationService';
import { MapContainer, TileLayer, Marker, useMapEvents } from 'react-leaflet';
import L from 'leaflet';
import setupLeaflet from '../../utils/leafletConfig';
import 'leaflet/dist/leaflet.css';

setupLeaflet();

// стандартна іконка
const defaultIcon = new L.Icon({
  iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon.png',
  iconRetinaUrl:
    'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-icon-2x.png',
  shadowUrl:
    'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.7.1/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41]
});

// компонент для кліку по карті
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

const CreateAccommodation = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    type: 'HOUSE',
    street: '',
    houseNumber: '',
    apartment: '',
    city: '',
    size: '',
    latitude: '',
    longitude: '',
    amenities: '',
    dailyRate: '',
    image: ''
  });

  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }));
  };

  const setCoordinates = ({ latitude, longitude }) => {
    setFormData((prev) => ({
      ...prev,
      latitude,
      longitude,
      city: 'Київ' // 🔧 тестове значення для навчального проєкту
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    try {
      // зібрана адреса
      const location = `${formData.street || ''} ${formData.houseNumber || ''}${
        formData.apartment ? ', кв. ' + formData.apartment : ''
      }`.trim();

      const payload = {
        name: formData.name,
        type: formData.type,
        location,
        city: formData.city,
        size: formData.size,
        latitude: formData.latitude,
        longitude: formData.longitude,
        amenities: formData.amenities
          .split(',')
          .map((a) => a.trim())
          .filter(Boolean),
        dailyRate: Number(formData.dailyRate),
        image: formData.image
      };

      await createAccommodation(payload);
      navigate('/accommodations');
    } catch (err) {
      setError(err.response?.data?.message || 'Помилка при створенні');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container page">
      <form onSubmit={handleSubmit} className="admin-form">
        <h1>✨ Створити помешкання</h1>
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
            <option value="HOUSE">Будинок</option>
            <option value="APARTMENT">Квартира</option>
            <option value="HOTEL">Готель</option>
            <option value="VACATION_HOME">Дім для відпочинку</option>
            <option value="HOSTEL">Хостел</option>
          </select>
        </div>

        {/* Вулиця */}
        <div className="form-group">
          <label>Вулиця / район</label>
          <input
            type="text"
            name="street"
            value={formData.street}
            onChange={handleChange}
          />
        </div>

        {/* Номер будинку */}
        <div className="form-group">
          <label>Номер будинку</label>
          <input
            type="text"
            name="houseNumber"
            value={formData.houseNumber}
            onChange={handleChange}
          />
        </div>

        {/* Квартира */}
        <div className="form-group">
          <label>Квартира / офіс</label>
          <input
            type="text"
            name="apartment"
            value={formData.apartment}
            onChange={handleChange}
          />
        </div>

        {/* Місто */}
        <div className="form-group">
          <label>Місто</label>
          <input type="text" name="city" value={formData.city} onChange={handleChange} />
        </div>

        {/* Розмір */}
        <div className="form-group">
          <label>Розмір (наприклад, 50м²)</label>
          <input type="text" name="size" value={formData.size} onChange={handleChange} />
        </div>

        {/* Карта */}
        <div className="form-group">
          <label>Виберіть розташування на карті</label>
          <div style={{ height: '300px', width: '100%', marginBottom: '1rem' }}>
            <MapContainer
              center={[50.45, 30.52]}
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
            value={formData.amenities}
            onChange={handleChange}
            placeholder="Wi-Fi, кухня, кондиціонер..."
          />
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
            placeholder="https://example.com/image.jpg"
          />
        </div>

        <button type="submit" className="btn-primary" disabled={loading}>
          {loading ? 'Створення...' : 'Створити'}
        </button>
      </form>
    </div>
  );
};

export default CreateAccommodation;
