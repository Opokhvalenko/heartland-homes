import React from 'react';
import { Link, Outlet } from 'react-router-dom';
import { HomeIcon, UserIcon, CreditCardIcon } from '@heroicons/react/24/outline';
import { MdAdminPanelSettings } from 'react-icons/md';
import { FaMoneyBillWave } from 'react-icons/fa';
import { useDispatch, useSelector } from 'react-redux';
import { logout } from '../store/slices/authSlice';

const AdminLayout = () => {
  const dispatch = useDispatch();
  const { user } = useSelector((state) => state.auth);

  return (
    <div className="admin-layout">
      {/*  Sidebar */}
      <aside className="admin-sidebar">
        <div className="admin-brand">Heartland Admin</div>
        <nav>
          <ul>
            <li>
              <Link to="/admin">
                <HomeIcon className="icon" /> Головна
              </Link>
            </li>
            <li>
              <Link to="/admin/accommodations">
                <MdAdminPanelSettings className="icon" /> Помешкання
              </Link>
            </li>
            <li>
              <Link to="/admin/bookings">
                <CreditCardIcon className="icon" /> Бронювання
              </Link>
            </li>
            <li>
              <Link to="/admin/payments">
                <FaMoneyBillWave className="icon" /> Платежі
              </Link>
            </li>
            <li>
              <Link to="/admin/users">
                <UserIcon className="icon" /> Користувачі
              </Link>
            </li>
          </ul>
        </nav>
      </aside>

      {/*  Контент */}
      <main className="admin-content">
        <header className="admin-header">
          <h2>Адмін-панель</h2>
          <div className="admin-user-actions">
            <span className="admin-user">
              👤 {user?.firstName || user?.email} ({user?.cleanRole})
            </span>
            <button className="btn-logout" onClick={() => dispatch(logout())}>
              Вийти
            </button>
          </div>
        </header>
        <div className="admin-main">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default AdminLayout;
