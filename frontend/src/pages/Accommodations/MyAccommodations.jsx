import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { FaPlus, FaEdit, FaTrash } from 'react-icons/fa';

import {
  loadMyAccommodations,
  removeAccommodation
} from '../../store/slices/accommodationsSlice';

import Notification from '../../components/Notification';
import '../../styles/components/admin/_admin-tables.scss';

const MyAccommodations = () => {
  const dispatch = useDispatch();
  const { items, loading, error } = useSelector((s) => s.accommodations);

  useEffect(() => {
    dispatch(loadMyAccommodations({ page: 0, size: 10 }));
  }, [dispatch]);

  const handleDelete = (id) => {
    if (window.confirm('Видалити ваше помешкання?')) {
      dispatch(removeAccommodation(id));
    }
  };

  if (loading) return <p className="text-center mt-5">Завантаження...</p>;

  return (
    <div className="container page">
      <h1 className="section-heading">Мої помешкання</h1>

      {error && <Notification message={error} type="danger" />}

      <div className="text-end mb-3">
        <Link to="/accommodations/new" className="btn-primary">
          <FaPlus /> Додати нове
        </Link>
      </div>

      {items.length > 0 ? (
        <table className="admin-table">
          <thead>
            <tr>
              <th>Назва</th>
              <th>Місто</th>
              <th>Ціна</th>
              <th>Статус</th>
              <th>Дії</th>
            </tr>
          </thead>
          <tbody>
            {items.map((acc) => (
              <tr key={acc.id}>
                <td>{acc.name}</td>
                <td>{acc.city}</td>
                <td>{acc.dailyRate} грн</td>
                <td>{acc.accommodationStatus}</td>
                <td>
                  <Link
                    to={`/my-accommodations/edit/${acc.id}`}
                    className="btn-icon btn-secondary"
                  >
                    <FaEdit />
                  </Link>
                  <button
                    className="btn-icon btn-danger"
                    onClick={() => handleDelete(acc.id)}
                  >
                    <FaTrash />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p className="text-center">Ви ще не зареєстрували жодного помешкання.</p>
      )}
    </div>
  );
};

export default MyAccommodations;
