// src/components/LocationMap.jsx
import React from 'react';
import BaseMap from './BaseMap';

const LocationMap = ({ location, city, latitude, longitude }) => {
  const items =
    latitude && longitude
      ? [
          {
            id: 'location-marker',
            latitude,
            longitude,
            name: location,
            city
          }
        ]
      : [];

  return (
    <div style={{ height: '300px', width: '100%' }}>
      <BaseMap items={items} highlightedId="location-marker" />
    </div>
  );
};

export default LocationMap;
