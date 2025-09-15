import React, { useEffect, useState } from 'react';
import { NavLink, Link, useNavigate, useLocation } from 'react-router-dom';
import { useSelector, useDispatch } from 'react-redux';
import { logout } from '../store/slices/authSlice';

import {
  FaHome,
  FaUser,
  FaSignOutAlt,
  FaSignInAlt,
  FaClipboardList,
  FaUserPlus,
  FaCreditCard,
  FaUserCog,
  FaBuilding
} from 'react-icons/fa';

import '../styles/components/header/_index.scss';
import '../styles/components/_buttons.scss';

const Header = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const { isAuthenticated, user } = useSelector((s) => s.auth);
  const [open, setOpen] = useState(false);
  const [adminMode, setAdminMode] = useState(false);

  const handleLogout = () => {
    dispatch(logout());
    setOpen(false);
    navigate('/login');
  };

  const closeDrawer = () => {
    setOpen(false);
  };

  useEffect(() => {
    document.body.classList.toggle('no-scroll', open);
    return () => document.body.classList.remove('no-scroll');
  }, [open]);

  // роль користувача
  const userRole =
    user?.cleanRole || (Array.isArray(user?.roles) ? user.roles[0] : user?.role);

  const isManager = userRole === 'MANAGER';
  const isCustomer = userRole === 'CUSTOMER';

  // якщо ми в адмінці — автоматично включаємо adminMode
  useEffect(() => {
    if (location.pathname.startsWith('/admin')) {
      setAdminMode(true);
    } else {
      setAdminMode(false);
    }
  }, [location]);

  return (
    <>
      <header className="main-header" role="banner">
        <div className="container header-container">
          <Link className="brand" to="/" aria-label="Heartland Homes">
            <svg width="24" height="24" viewBox="0 0 24 24" aria-hidden="true">
              <path
                fill="currentColor"
                d="M12 2.4c-3.9 0-7 3-7 6.7 0 5.5 7 12.3 7 12.3s7-6.8 7-12.3c0-3.7-3.1-6.7-7-6.7Zm0 9.1a2.7 2.7 0 1 1 0-5.4 2.7 2.7 0 0 1 0 5.4Z"
              />
            </svg>
            <span>Heartland Homes</span>
          </Link>

          {/* Desktop nav */}
          <nav className="nav-desktop" aria-label="Головна навігація">
            <ul>
              <li>
                <NavLink to="/" end className="nav-link">
                  <FaHome className="nav-icon" /> Головна
                </NavLink>
              </li>
              <li>
                <NavLink to="/my-bookings" className="nav-link">
                  <FaClipboardList className="nav-icon" /> Мої бронювання
                </NavLink>
              </li>
              <li>
                <NavLink to="/my-payments" className="nav-link">
                  <FaCreditCard className="nav-icon" /> Мої платежі
                </NavLink>
              </li>

              {/* ✅ Мої помешкання тільки для CUSTOMER */}
              {isCustomer && (
                <li>
                  <NavLink to="/my-accommodations" className="nav-link">
                    <FaBuilding className="nav-icon" /> Мої помешкання
                  </NavLink>
                </li>
              )}

              {isManager && (
                <li>
                  <NavLink to="/admin" className="nav-link">
                    <FaUserCog className="nav-icon" /> Адмін-панель
                  </NavLink>
                </li>
              )}
              <li>
                <NavLink to="/profile" className="nav-link">
                  <FaUser className="nav-icon" /> Профіль
                </NavLink>
              </li>
            </ul>

            {isAuthenticated ? (
              <div className="nav-actions">
                <button className="btn-chip logout" onClick={handleLogout}>
                  <FaSignOutAlt className="nav-icon" /> Вийти
                </button>
              </div>
            ) : (
              <div className="nav-actions">
                <NavLink to="/login" className="btn-chip primary">
                  <FaSignInAlt className="nav-icon" /> Увійти
                </NavLink>
                <NavLink to="/register" className="btn-chip secondary">
                  <FaUserPlus className="nav-icon" /> Реєстрація
                </NavLink>
              </div>
            )}
          </nav>

          {/* Burger */}
          <button
            className={`burger ${open ? 'open' : ''}`}
            aria-label="Меню"
            aria-controls="mobile-drawer"
            aria-expanded={open}
            onClick={() => setOpen((v) => !v)}
          >
            <span />
            <span />
            <span />
          </button>
        </div>
      </header>

      {/* Drawer */}
      <div
        id="mobile-drawer"
        className={`drawer ${open ? 'open' : ''} ${adminMode ? 'admin-mode' : ''}`}
        role="dialog"
        aria-modal="true"
        aria-label="Мобільне меню"
      >
        <div className={`drawer-content ${open ? 'open' : ''}`}>
          <button
            className="drawer-close"
            aria-label="Закрити меню"
            onClick={closeDrawer}
          />

          {adminMode ? (
            <>
              <h2>Адмін-панель</h2>
              <ul className="drawer-nav">
                <li>
                  <NavLink onClick={closeDrawer} to="/admin">
                    <FaHome className="nav-icon" /> Головна
                  </NavLink>
                </li>
                <li>
                  <NavLink onClick={closeDrawer} to="/admin/accommodations">
                    <FaClipboardList className="nav-icon" /> Помешкання
                  </NavLink>
                </li>
                <li>
                  <NavLink onClick={closeDrawer} to="/admin/bookings">
                    <FaCreditCard className="nav-icon" /> Бронювання
                  </NavLink>
                </li>
                <li>
                  <NavLink onClick={closeDrawer} to="/admin/users">
                    <FaUser className="nav-icon" /> Користувачі
                  </NavLink>
                </li>
              </ul>
              <div className="drawer-actions">
                <NavLink to="/" onClick={() => setOpen(false)} className="btn-primary">
                  ← На головну
                </NavLink>
              </div>
            </>
          ) : (
            <>
              <ul className="drawer-nav" role="navigation">
                <li>
                  <NavLink onClick={closeDrawer} to="/" end>
                    <FaHome className="nav-icon" /> Головна
                  </NavLink>
                </li>
                <li>
                  <NavLink onClick={closeDrawer} to="/my-bookings">
                    <FaClipboardList className="nav-icon" /> Мої бронювання
                  </NavLink>
                </li>
                <li>
                  <NavLink onClick={closeDrawer} to="/my-payments">
                    <FaCreditCard className="nav-icon" /> Мої платежі
                  </NavLink>
                </li>

                {/* ✅ у Drawer теж */}
                {isCustomer && (
                  <li>
                    <NavLink onClick={closeDrawer} to="/my-accommodations">
                      <FaBuilding className="nav-icon" /> Мої помешкання
                    </NavLink>
                  </li>
                )}

                {isManager && (
                  <li>
                    <NavLink onClick={closeDrawer} to="/admin">
                      <FaUserCog className="nav-icon" /> Адмін-панель
                    </NavLink>
                  </li>
                )}
                <li>
                  <NavLink onClick={closeDrawer} to="/profile">
                    <FaUser className="nav-icon" /> Профіль
                  </NavLink>
                </li>
              </ul>

              {isAuthenticated ? (
                <div className="drawer-actions">
                  <button className="btn-primary" onClick={handleLogout}>
                    <FaSignOutAlt className="nav-icon" /> Вийти
                  </button>
                </div>
              ) : (
                <div className="drawer-actions">
                  <NavLink onClick={closeDrawer} to="/login" className="btn-primary">
                    <FaSignInAlt className="nav-icon" /> Увійти
                  </NavLink>
                  <NavLink onClick={closeDrawer} to="/register" className="btn-secondary">
                    <FaUserPlus className="nav-icon" /> Реєстрація
                  </NavLink>
                </div>
              )}
            </>
          )}
        </div>

        <button
          className="drawer-backdrop"
          onClick={closeDrawer}
          aria-label="Закрити меню"
        />
      </div>
    </>
  );
};

export default Header;
