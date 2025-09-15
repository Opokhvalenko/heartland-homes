import api from '../axios';

// 🔍 Пошук житла з фільтрами (GET)
export const fetchAccommodations = async (params = {}) => {
  const cleanedParams = Object.fromEntries(
    Object.entries(params).filter(
      ([, value]) =>
        value != null &&
        value !== '' &&
        !(typeof value === 'object' && Object.keys(value).length === 0)
    )
  );

  const response = await api.get('/accommodations/search', { params: cleanedParams });
  return response.data;
};

// 📄 Отримати деталі житла
export const getAccommodationById = async (id) => {
  const response = await api.get(`/accommodations/${id}`);
  return response.data;
};

// ➕ Створити житло (для користувача / менеджера)
export const createAccommodation = async (formData) => {
  const response = await api.post('/accommodations', formData);
  return response.data;
};

// ✏️ Оновити житло (адмін редагує будь-яке)
export const updateAccommodation = async (id, formData) => {
  const response = await api.put(`/accommodations/${id}`, formData);
  return response.data;
};

// ✏️ Оновити моє житло (користувач / менеджер редагує своє)
export const updateMyAccommodation = async (id, formData) => {
  const response = await api.put(`/accommodations/me/${id}`, formData);
  return response.data;
};

// 📋 Для адміна (список без фільтрів, пагінація)
export const fetchAdminAccommodations = async (page = 0, size = 10) => {
  const response = await api.get('/accommodations', { params: { page, size } });
  return response.data;
};

// 📋 Для користувача (мої помешкання)
export const fetchMyAccommodations = async (page = 0, size = 10) => {
  const response = await api.get('/accommodations/me', { params: { page, size } });
  return response.data;
};

// ❌ Видалити житло (адмін)
export const deleteAccommodation = async (id) => {
  const response = await api.delete(`/accommodations/${id}`);
  return response.data;
};

// 🔄 Оновити статус житла (PATCH /accommodations/{id}/status)
export const updateAccommodationStatus = async (id, status) => {
  const response = await api.patch(`/accommodations/${id}/status`, { status });
  return response.data;
};
