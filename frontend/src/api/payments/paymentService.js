import api from '../axios';

//  Створити платіж
export const createPayment = async (bookingId, paymentType = 'PAYMENT') => {
  const response = await api.post('/payments', { bookingId, paymentType });
  return response.data;
};

// Отримати платежі користувача
export const fetchPaymentsByUser = async (userId, pageable) => {
  const response = await api.get(`/payments`, {
    params: { userId, ...pageable }
  });
  return response.data;
};

// Скасувати платіж
export const cancelPayment = async (paymentId) => {
  const response = await api.get(`/payments/cancel`, { params: { id: paymentId } });
  return response.data;
};

// Отримати всі платежі (адмін)
export const getAllPaymentsService = async (pageable) => {
  const response = await api.get('/payments', { params: pageable });
  return response.data;
};

export default {
  createPayment,
  fetchPaymentsByUser,
  cancelPayment,
  getAllPaymentsService
};
