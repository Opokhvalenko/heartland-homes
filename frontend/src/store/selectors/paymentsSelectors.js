export const selectPaymentByBookingId = (state, bookingId) => {
  return state.payments.payments.find((p) => p.bookingId === bookingId);
};
