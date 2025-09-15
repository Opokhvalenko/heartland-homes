// src/components/Map.jsx
import React from 'react';
import BaseMap from './BaseMap';

const Map = ({ accommodations = [] }) => {
  const items = accommodations
    .filter((a) => a.latitude && a.longitude)
    .map((a) => ({
      id: a.id,
      latitude: a.latitude,
      longitude: a.longitude,
      name: a.name,
      city: a.city
    }));

  return (
    <div style={{ height: '400px', width: '100%' }}>
      <BaseMap items={items} />
    </div>
  );
};

export default Map;
