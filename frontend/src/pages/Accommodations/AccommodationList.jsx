import React, { useCallback } from 'react';
import AccommodationCard from './AccommodationCard';
import '../../styles/components/accommodation/_accommodations-list.scss';

const AccommodationList = ({ accommodations, onCardHover }) => {
  if (!accommodations || accommodations.length === 0) {
    return <p className="text-center">Немає доступних помешкань.</p>;
  }

  //  мемоізуємо обробники
  const handleMouseEnter = useCallback((id) => () => onCardHover(id), [onCardHover]);

  const handleMouseLeave = useCallback(() => onCardHover(null), [onCardHover]);

  return (
    <div className="accommodations-list-grid">
      {accommodations.map((acc) => (
        <div
          key={acc.id}
          className="accommodation-card-wrapper"
          onMouseEnter={handleMouseEnter(acc.id)}
          onMouseLeave={handleMouseLeave}
        >
          <AccommodationCard accommodation={acc} />
        </div>
      ))}
    </div>
  );
};

export default React.memo(AccommodationList);
