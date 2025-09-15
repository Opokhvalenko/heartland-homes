import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import authService from '../../api/auth/authService';
import { setUser } from '../../store/slices/authSlice';

const LoginSuccess = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const token = params.get('token');

    if (token) {
      // зберігаємо токен
      localStorage.setItem('auth', JSON.stringify({ token }));

      // тягнемо профіль користувача
      authService.getProfile().then((profile) => {
        let rawRole = profile.role || (profile.roles?.[0] ?? null);
        let cleanRole = rawRole?.startsWith('ROLE_')
          ? rawRole.replace('ROLE_', '')
          : rawRole;

        const userData = { token, ...profile, cleanRole };

        // зберігаємо у localStorage
        localStorage.setItem('userProfile', JSON.stringify(profile));

        // оновлюємо Redux
        dispatch(setUser(userData));

        // редірект на головну
        navigate('/', { replace: true });
      });
    } else {
      navigate('/login', { replace: true });
    }
  }, [location, navigate, dispatch]);

  return (
    <div style={{ textAlign: 'center', marginTop: '50px' }}>
      <h2>Обробка входу...</h2>
      <p>Зачекайте, будь ласка ⏳</p>
    </div>
  );
};

export default LoginSuccess;
