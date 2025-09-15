import { useState, useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { FaEnvelope, FaLock, FaHome } from 'react-icons/fa';
import { login } from '../../store/slices/authSlice';
import GoogleLoginButton from '../../components/GoogleLoginButton';
import '../../styles/components/_auth.scss';

const Login = () => {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();

  const { isAuthenticated, isLoading, isError, message } = useSelector((s) => s.auth);

  const handleChange = (e) =>
    setFormData({ ...formData, [e.target.name]: e.target.value });

  const handleSubmit = (e) => {
    e.preventDefault();
    dispatch(login(formData));
  };

  //  Якщо користувач вже залогінений → редіректимо
  useEffect(() => {
    if (isAuthenticated) {
      const redirectPath = location.state?.from?.pathname || '/';
      navigate(redirectPath, { replace: true });
    }
  }, [isAuthenticated, navigate, location]);

  return (
    <div className="auth-layout">
      <div className="auth-card">
        <h2 className="auth-title">Вхід</h2>
        <p className="form-subtitle">
          Немає акаунта? <Link to="/register">Зареєструватися</Link>
        </p>

        <form onSubmit={handleSubmit}>
          <div className="form-group with-icon">
            <FaEnvelope className="input-icon" />
            <input
              type="email"
              name="email"
              className="form-control"
              placeholder="Електронна пошта"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group with-icon">
            <FaLock className="input-icon" />
            <input
              type="password"
              name="password"
              className="form-control"
              placeholder="Пароль"
              value={formData.password}
              onChange={handleChange}
              required
            />
          </div>

          {isError && (
            <p className="form-error" data-testid="login-error">
              {message || 'Невірний логін або пароль'}
            </p>
          )}

          <button type="submit" disabled={isLoading}>
            {isLoading ? 'Зачекайте...' : 'Увійти'}
          </button>
        </form>

        {/* Кнопка Google Login */}
        <GoogleLoginButton />
      </div>

      <div className="auth-side login">
        <FaHome className="auth-icon" />
        <h2 className="auth-title">Ласкаво просимо 👋</h2>
        <p className="auth-subtitle">
          Увійдіть, щоб забронювати свій наступний будинок мрії з{' '}
          <strong>Heartland Homes</strong>.
        </p>
      </div>
    </div>
  );
};

export default Login;
