import React, { useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Link, useNavigate } from 'react-router-dom';
import { FaClipboardList, FaUsers, FaBuilding, FaMoneyBillWave } from 'react-icons/fa';
import '../../styles/components/admin/_admin-dashboard.scss';

const AdminDashboard = () => {
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);

  useEffect(() => {
    if (!user || user.cleanRole !== 'MANAGER') {
      navigate('/');
    }
  }, [user, navigate]);

  return (
    <div className="container page admin-dashboard">
      <div className="dashboard-grid mt-5">
        <Link to="/admin/accommodations" className="dashboard-card accent-green fade-in">
          <FaBuilding className="dashboard-icon" />
          <h3>Помешкання</h3>
          <p>Керуйте всіма доступними об’єктами</p>
        </Link>

        <Link to="/admin/bookings" className="dashboard-card accent-blue fade-in delay-1">
          <FaClipboardList className="dashboard-icon" />
          <h3>Бронювання</h3>
          <p>Перегляд і підтвердження заявок</p>
        </Link>

        <Link
          to="/admin/payments"
          className="dashboard-card accent-orange fade-in delay-2"
        >
          <FaMoneyBillWave className="dashboard-icon" />
          <h3>Платежі</h3>
          <p>Перегляд та управління оплатами</p>
        </Link>

        <Link to="/admin/users" className="dashboard-card accent-purple fade-in delay-3">
          <FaUsers className="dashboard-icon" />
          <h3>Користувачі</h3>
          <p>Управління правами доступу</p>
        </Link>
      </div>
    </div>
  );
};

export default AdminDashboard;
