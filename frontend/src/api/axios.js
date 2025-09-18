import axios from 'axios';
import qs from 'qs';

// ===== helpers =====
const normalizeBaseUrl = (url) => {
  if (!url) throw new Error('VITE_API_URL is missing');
  // Забираємо фінальний слеш, щоб не було // у запитах
  return url.replace(/\/+$/, '');
};

const API_BASE = normalizeBaseUrl(import.meta.env.VITE_API_URL);

const instance = axios.create({
  baseURL: API_BASE,
  // ВАЖЛИВО: дає змогу браузеру відправляти/приймати cookies з бекенду
  withCredentials: true,
  headers: {
    Accept: 'application/json',
  },
  paramsSerializer: (params) =>
    qs.stringify(params, { arrayFormat: 'repeat', allowDots: true }),
});

// ===== token from localStorage (якщо є) =====
const getAuthData = () => {
  try {
    return JSON.parse(localStorage.getItem('auth')) || null;
  } catch (e) {
    console.error('Помилка при парсингу токена з localStorage:', e);
    return null;
  }
};

// ===== request interceptor =====
instance.interceptors.request.use((config) => {
  const auth = getAuthData();
  const token = auth?.token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  // маркер, корисний для CORS/CSRF політик
  config.headers['X-Requested-With'] = 'XMLHttpRequest';
  return config;
});

// ===== response interceptor =====
instance.interceptors.response.use(
  (res) => res,
  (err) => {
    // якщо бек ще не готовий/мертво — не зносимо сесію
    const status = err.response?.status;

    if (status === 401) {
      const current = window.location.pathname;
      // уникаємо нескінченного циклу під час /auth/success
      if (!['/login', '/auth/success'].includes(current)) {
        localStorage.removeItem('auth');
        localStorage.removeItem('userProfile');
        // replace, щоб не повертатися назад на помилкову сторінку
        window.location.replace('/login');
      }
    }
    return Promise.reject(err);
  }
);

export default instance;