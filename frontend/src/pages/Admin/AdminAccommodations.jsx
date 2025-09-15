import React, { useEffect, useState, useMemo } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { FaPlus, FaEdit, FaTrash } from 'react-icons/fa';

import AdminTable from '../Admin/AdminTable';
import AdminAccommodationCard from './AdminAccommodationCard';
import StatusSelect from '../../components/selects/StatusSelect';
import Notification from '../../components/Notification';
import Pagination from '../../components/Pagination';

import {
  loadAdminAccommodations,
  removeAccommodation,
  setPage,
  updateAccommodationStatusAsync
} from '../../store/slices/accommodationsSlice';

import { fixDropboxUrl } from '../../utils/fixDropboxUrl';
import { mapType } from '../../utils/translations';

import '../../styles/components/admin/_admin.scss';
import '../../styles/components/badges/_badges.scss';
import '../../styles/components/_status-select.scss';
import '../../styles/components/admin/_admin-tables.scss';
import '../../styles/components/admin/_admin-accommodations.scss';

const fallbackImage = '/assets/no-image.svg';

const AdminAccommodations = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { user } = useSelector((state) => state.auth);
  const {
    items: accommodations,
    loading,
    error,
    page,
    totalPages
  } = useSelector((s) => s.accommodations);

  const [isMobile, setIsMobile] = useState(window.innerWidth < 768);

  useEffect(() => {
    if (!user || user.cleanRole !== 'MANAGER') {
      navigate('/');
      return;
    }
    dispatch(loadAdminAccommodations({ page }));
  }, [user, navigate, dispatch, page]);

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth < 768);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  const handleDelete = (id) => {
    if (window.confirm('Видалити помешкання?')) {
      dispatch(removeAccommodation(id));
    }
  };

  const handleStatusChange = (id, status) => {
    dispatch(updateAccommodationStatusAsync({ id, status }));
  };

  const columns = useMemo(
    () => [
      { key: 'id', label: 'ID' },
      { key: 'name', label: 'Назва' },
      { key: 'city', label: 'Місто' },
      {
        key: 'type',
        label: 'Тип',
        render: (acc) => {
          const { label, icon, color } = mapType(acc.type);
          return (
            <span className="badge badge-type" style={{ backgroundColor: color }}>
              {icon} {label}
            </span>
          );
        }
      },
      {
        key: 'dailyRate',
        label: 'Ціна',
        className: 'price',
        render: (acc) => <span>{acc.dailyRate} грн</span>
      },
      {
        key: 'image',
        label: 'Зображення',
        render: (acc) => (
          <img
            src={acc.image ? fixDropboxUrl(acc.image) : fallbackImage}
            alt={acc.name || 'Зображення помешкання'}
            className="table-img"
            onError={(e) => (e.currentTarget.src = fallbackImage)}
          />
        )
      },
      {
        key: 'accommodationStatus',
        label: 'Статус',
        render: (acc) => (
          <StatusSelect
            type="accommodation"
            value={acc.accommodationStatus}
            onChange={(newStatus) => handleStatusChange(acc.id, newStatus)}
          />
        )
      }
    ],
    [handleStatusChange]
  );

  if (loading) return <p className="text-center mt-5">Завантаження...</p>;

  return (
    <div className="container admin-page-container">
      <h1 className="section-heading text-center">Управління помешканнями</h1>

      {error && <Notification message={error} type="danger" />}

      <div className="text-end mb-3">
        <Link to="/accommodations/new" className="btn-primary">
          <FaPlus /> Додати помешкання
        </Link>
      </div>

      {accommodations.length > 0 ? (
        <>
          {isMobile ? (
            <div className="admin-accommodations-cards">
              {accommodations.map((acc) => (
                <AdminAccommodationCard
                  key={acc.id}
                  acc={acc}
                  onStatusChange={handleStatusChange}
                  onDelete={handleDelete}
                />
              ))}
            </div>
          ) : (
            <AdminTable
              columns={columns}
              data={accommodations}
              actions={(acc) => (
                <div className="action-buttons">
                  <Link
                    to={`/admin/accommodations/edit/${acc.id}`}
                    className="btn-icon btn-secondary"
                    title="Редагувати"
                  >
                    <FaEdit />
                  </Link>
                  <button
                    className="btn-icon btn-danger"
                    onClick={() => handleDelete(acc.id)}
                    title="Видалити"
                  >
                    <FaTrash />
                  </button>
                </div>
              )}
            />
          )}

          <Pagination
            page={page}
            totalPages={totalPages}
            onPageChange={(newPage) => dispatch(setPage(newPage))}
          />
        </>
      ) : (
        <p className="text-center">Помешкань ще немає.</p>
      )}
    </div>
  );
};

export default AdminAccommodations;
