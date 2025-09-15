// Отримати токен з localStorage
export const getAuthToken = () => {
  try {
    const auth = JSON.parse(localStorage.getItem('auth'));
    return auth?.token || null;
  } catch {
    return null;
  }
};

// Очистити токен
export const clearAuthToken = () => {
  localStorage.removeItem('auth');
};
