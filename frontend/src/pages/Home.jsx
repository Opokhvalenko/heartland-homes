import React, { useEffect, useCallback, useMemo } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  loadAccommodations,
  setFilters,
  resetFilters
} from '../store/slices/accommodationsSlice';
import { useNavigate } from 'react-router-dom';

import HeroSection from '../components/Home/HeroSection';
import AccommodationsSection from '../components/Home/AccommodationsSection';
import OffersSection from '../components/Home/OffersSection';

const Home = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { items, loading, error } = useSelector((state) => state.accommodations);

  useEffect(() => {
    dispatch(resetFilters());
    dispatch(loadAccommodations({ pageable: { page: 0, size: 4 } }));
  }, [dispatch]);

  const handleSearch = useCallback(
    (e, formData) => {
      e.preventDefault();
      dispatch(setFilters(formData));

      const queryParams = new URLSearchParams(
        Object.fromEntries(
          Object.entries(formData).filter(([_, v]) => v !== null && v !== '')
        )
      ).toString();

      navigate(`/accommodations?${queryParams}`);
    },
    [dispatch, navigate]
  );

  const topAccommodations = useMemo(() => items.slice(0, 4), [items]);

  return (
    <div className="home-page">
      <HeroSection onSearch={handleSearch} />
      <AccommodationsSection
        loading={loading}
        error={error}
        accommodations={topAccommodations}
      />
      <OffersSection />
    </div>
  );
};

export default React.memo(Home);
