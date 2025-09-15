import api from '../axios';

const register = async (userData) => {
  const response = await api.post('/auth/registration', userData);
  return response.data;
};

const login = async (userData) => {
  try {
    const response = await api.post('/auth/login', userData);

    const authData = { token: response.data.token };
    localStorage.setItem('auth', JSON.stringify(authData));

    return authData;
  } catch (err) {
    // ⚡ прокидаємо помилку далі у slice
    throw err.response?.data?.message || 'Невірний логін або пароль';
  }
};

const logout = () => {
  localStorage.removeItem('auth');
  localStorage.removeItem('userProfile');
};

const getProfile = async () => {
  const response = await api.get('/users/me');
  return response.data;
};

const authService = { register, login, logout, getProfile };
export default authService;
