import React, { useEffect, useRef, useState, useCallback, useMemo } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useLocation } from 'react-router-dom';
import AccommodationList from './AccommodationList';
import Notification from '../../components/Notification';
import AccommodationFilters from './AccommodationFilters';
import Pagination from '../../components/Pagination';
import BaseMap from '../../components/BaseMap';
import { getSafeImageUrl } from '../../utils/getSafeImageUrl';
import AccommodationDetails from './AccommodationDetails';

import {
  loadAccommodations,
  setFilters,
  resetFilters,
  setPage
} from '../../store/slices/accommodationsSlice';

import '../../styles/components/accommodation/_accommodations-map.scss';
import '../../styles/components/accommodation/_accommodations-list.scss';

// Простий компонент модалки
const Modal = React.memo(({ isOpen, onClose, children }) => {
  if (!isOpen) return null;

  return (
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(0,0,0,0.6)',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        zIndex: 2000
      }}
      onClick={onClose}
    >
      <div
        style={{
          background: '#fff',
          borderRadius: '12px',
          width: '90%',
          maxWidth: '1000px',
          maxHeight: '90vh',
          overflowY: 'auto',
          padding: '1rem'
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <button
          onClick={onClose}
          style={{
            float: 'right',
            border: 'none',
            background: 'transparent',
            fontSize: '1.5rem',
            cursor: 'pointer'
          }}
        >
          ✖
        </button>
        {children}
      </div>
    </div>
  );
});

const Accommodations = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const { items, loading, error, filters, page, totalPages } = useSelector(
    (state) => state.accommodations
  );

  const accommodationListRef = useRef(null);
  const [highlightedId, setHighlightedId] = useState(null);
  const [selectedAccommodation, setSelectedAccommodation] = useState(null);

  // Зчитування фільтрів з URL
  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const newFilters = {};
    for (const [key, value] of queryParams.entries()) {
      if (value !== 'null' && value !== '') {
        newFilters[key] = value;
      }
    }
    dispatch(setFilters(newFilters));
  }, [dispatch, location.search]);

  // Завантаження даних
  useEffect(() => {
    dispatch(loadAccommodations());
  }, [dispatch, filters, page]);

  // Скидання фільтрів і сторінки при виході зі сторінки
  useEffect(() => {
    return () => {
      dispatch(resetFilters());
      dispatch(setPage(0));
    };
  }, [dispatch]);

  //  Мемоізація queryParams
  const queryParams = useMemo(() => new URLSearchParams(filters).toString(), [filters]);

  const handleApplyFilters = useCallback(
    (e, formData) => {
      e.preventDefault();
      const newFilters = Object.fromEntries(
        Object.entries(formData).filter(([_, v]) => v !== null && v !== '')
      );
      navigate(`/accommodations?${new URLSearchParams(newFilters)}`);
    },
    [navigate]
  );

  const handleResetFilters = useCallback(() => {
    dispatch(resetFilters());
    dispatch(setPage(0));
    navigate('/accommodations');
  }, [dispatch, navigate]);

  const handleCardHover = useCallback((id) => {
    setHighlightedId(id);
  }, []);

  return (
    <div className="accommodations-page-container accommodations-list-page">
      <div className="accommodations-list-wrapper">
        <div className="container mt-4">
          <h2 ref={accommodationListRef} className="section-heading mt-5">
            Доступні помешкання
          </h2>
          <AccommodationFilters
            city={filters.city || ''}
            type={filters.type || ''}
            size={filters.accommodationSize || ''}
            minDailyRate={filters.minDailyRate || ''}
            maxDailyRate={filters.maxDailyRate || ''}
            setCity={(val) => dispatch(setFilters({ city: val }))}
            setType={(val) => dispatch(setFilters({ type: val }))}
            setSize={(val) => dispatch(setFilters({ accommodationSize: val }))}
            setMinDailyRate={(val) =>
              dispatch(setFilters({ minDailyRate: val ? Number(val) : null }))
            }
            setMaxDailyRate={(val) =>
              dispatch(setFilters({ maxDailyRate: val ? Number(val) : null }))
            }
            onApplyFilters={handleApplyFilters}
            onResetFilters={handleResetFilters}
          />

          {loading && <p className="text-center">Завантаження...</p>}
          {error && <Notification message={error} type="danger" />}

          {!loading && !error && items.length > 0 ? (
            <>
              <AccommodationList accommodations={items} onCardHover={handleCardHover} />
              <Pagination
                page={page}
                totalPages={totalPages}
                onPageChange={(newPage) => dispatch(setPage(newPage))}
              />
            </>
          ) : (
            <p className="text-center">Помешкань не знайдено</p>
          )}
        </div>
      </div>

      <div className="accommodations-map-wrapper">
        <BaseMap
          items={items}
          highlightedId={highlightedId}
          renderPopup={(acc) => (
            <div style={{ width: '150px' }}>
              <img
                src={acc.image ? getSafeImageUrl(acc.image) : '/no-image.png'}
                alt={acc.name || 'Помешкання'}
                style={{
                  width: '100%',
                  borderRadius: '6px',
                  marginBottom: '6px',
                  cursor: 'pointer'
                }}
                onError={(e) => (e.currentTarget.src = '/no-image.png')}
                onClick={() => setSelectedAccommodation(acc)}
              />
              <strong>{acc.name}</strong>
              <div>{acc.city}</div>
            </div>
          )}
        />
      </div>

      {/* Модалка */}
      <Modal
        isOpen={!!selectedAccommodation}
        onClose={() => setSelectedAccommodation(null)}
      >
        {selectedAccommodation && <AccommodationDetails id={selectedAccommodation.id} />}
      </Modal>
    </div>
  );
};

export default React.memo(Accommodations);
