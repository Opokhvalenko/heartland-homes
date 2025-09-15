import React from 'react';
import PropTypes from 'prop-types';

const ACCOMMODATION_TYPES = [
  { value: 'HOUSE', label: 'Будинок' },
  { value: 'APARTMENT', label: 'Квартира' },
  { value: 'HOTEL', label: 'Готель' },
  { value: 'VACATION_HOME', label: 'Дім для відпочинку' },
  { value: 'HOSTEL', label: 'Хостел' }
];

const AccommodationFormFields = ({
  formData,
  handleChange,
  showLocation = true,
  showDailyRate = true,
  showDailyRateRange = false,
  showImage = true,
  showAmenities = true,
  showLatitude = true,
  showLongitude = true
}) => {
  return (
    <>
      <div className="form-group">
        <label>Місто</label>
        <input
          type="text"
          name="city"
          value={formData.city}
          onChange={handleChange}
          className="form-control"
          placeholder="Наприклад, Київ"
        />
      </div>

      <div className="form-group">
        <label>Тип житла</label>
        <select
          name="type"
          value={formData.type}
          onChange={handleChange}
          className="form-control"
        >
          <option value="">Будь-який</option>
          {ACCOMMODATION_TYPES.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      </div>

      {showDailyRateRange && (
        <div className="price-range-group">
          <div className="form-group">
            <label>Ціна від</label>
            <input
              type="number"
              name="minDailyRate"
              value={formData.minDailyRate}
              onChange={handleChange}
              className="form-control"
              placeholder="грн"
            />
          </div>
          <div className="form-group">
            <label>Ціна до</label>
            <input
              type="number"
              name="maxDailyRate"
              value={formData.maxDailyRate}
              onChange={handleChange}
              className="form-control"
              placeholder="грн"
            />
          </div>
        </div>
      )}
    </>
  );
};

AccommodationFormFields.propTypes = {
  formData: PropTypes.object.isRequired,
  handleChange: PropTypes.func.isRequired,
  showLocation: PropTypes.bool,
  showDailyRate: PropTypes.bool,
  showDailyRateRange: PropTypes.bool,
  showImage: PropTypes.bool,
  showAmenities: PropTypes.bool,
  showLatitude: PropTypes.bool,
  showLongitude: PropTypes.bool
};

export default AccommodationFormFields;
