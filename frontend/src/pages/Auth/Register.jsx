import React, { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, Link } from 'react-router-dom';
import { FaUser, FaEnvelope, FaLock } from 'react-icons/fa';
import { register, reset } from '../../store/slices/authSlice';
import Notification from '../../components/Notification';
import '../../styles/components/_auth.scss';

const Register = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    confirmPassword: '',
    firstName: '',
    lastName: ''
  });

  const { email, password, confirmPassword, firstName, lastName } = formData;

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { isLoading, isError, isSuccess, message } = useSelector((state) => state.auth);

  //  Редірект після успішної реєстрації
  useEffect(() => {
    if (isSuccess) {
      navigate('/login');
    }
    dispatch(reset());
  }, [isSuccess, navigate, dispatch]);

  const onChange = (e) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const onSubmit = (e) => {
    e.preventDefault();
    if (password !== confirmPassword) {
      alert('Паролі не співпадають');
      return;
    }
    dispatch(register({ email, password, firstName, lastName }));
  };

  return (
    <div className="auth-layout">
      <div className="auth-card">
        <h2 className="auth-title">Реєстрація</h2>

        {isError && <Notification message={message} type="error" />}
        {isSuccess && (
          <Notification
            message="Реєстрація успішна! Тепер ви можете увійти."
            type="success"
          />
        )}

        <form onSubmit={onSubmit}>
          <div className="form-group with-icon">
            <FaUser className="input-icon" />
            <input
              type="text"
              id="firstName"
              name="firstName"
              className="form-control"
              placeholder="Ім’я"
              value={firstName}
              onChange={onChange}
              required
            />
          </div>

          <div className="form-group with-icon">
            <FaUser className="input-icon" />
            <input
              type="text"
              id="lastName"
              name="lastName"
              className="form-control"
              placeholder="Прізвище"
              value={lastName}
              onChange={onChange}
              required
            />
          </div>

          <div className="form-group with-icon">
            <FaEnvelope className="input-icon" />
            <input
              type="email"
              id="email"
              name="email"
              className="form-control"
              placeholder="Електронна пошта"
              value={email}
              onChange={onChange}
              required
            />
          </div>

          <div className="form-group with-icon">
            <FaLock className="input-icon" />
            <input
              type="password"
              id="password"
              name="password"
              className="form-control"
              placeholder="Пароль"
              value={password}
              onChange={onChange}
              required
            />
          </div>

          <div className="form-group with-icon">
            <FaLock className="input-icon" />
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              className="form-control"
              placeholder="Підтвердіть пароль"
              value={confirmPassword}
              onChange={onChange}
              required
            />
          </div>

          <button type="submit" className="btn btn-primary" disabled={isLoading}>
            {isLoading ? 'Реєстрація...' : 'Зареєструватися'}
          </button>
        </form>

        <p className="form-subtitle text-center">
          Вже маєте акаунт? <Link to="/login">Увійти</Link>
        </p>
      </div>

      {/*  Додаємо модифікатор register */}
      <div className="auth-side register">
        <span className="auth-icon">✨</span>
        <h2 className="auth-title">Ласкаво просимо!</h2>
        <p className="auth-subtitle">
          Зареєструйтесь, щоб знайти свій ідеальний дім разом з{' '}
          <strong>Heartland Homes</strong>.
        </p>
      </div>
    </div>
  );
};

export default Register;
