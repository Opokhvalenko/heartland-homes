import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import bookingsService from '../../api/bookings/bookingsService';

const initialState = {
  bookings: [],
  currentBooking: null,
  status: 'idle',
  error: null,
  paymentStatus: 'idle',
  page: 0,
  totalPages: 0,
  totalElements: 0
};

//  Хелпер: оновлення сторінки/пагінації
function updatePageState(state, payload) {
  state.bookings = payload.content || [];
  state.page = payload.pageable?.pageNumber ?? 0;
  state.totalPages = payload.totalPages || 0;
  state.totalElements = payload.totalElements || 0;
}

//  Хелпер: видалення бронювання (cancel/delete)
function removeBooking(state, id) {
  state.bookings = state.bookings.filter((b) => b.id !== id);
  state.totalElements = Math.max(0, state.totalElements - 1);
  state.totalPages = Math.ceil(state.totalElements / 5);
}

// Create booking
export const createBooking = createAsyncThunk(
  'bookings/createBooking',
  async (bookingData, { rejectWithValue }) => {
    try {
      return await bookingsService.createBooking(bookingData);
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося створити бронювання.'
      );
    }
  }
);

//  Fetch all bookings (admin)
export const fetchBookings = createAsyncThunk(
  'bookings/fetchBookings',
  async ({ page = 0, size = 10, userId, status } = {}, { rejectWithValue }) => {
    try {
      const response = await bookingsService.fetchBookings(page, size, userId, status);

      const { getAccommodationById } = await import(
        '../../api/accommodations/accommodationService'
      );

      const enriched = await Promise.all(
        (response.content || []).map(async (b) => {
          let accommodation = null;
          let totalPrice = null;

          try {
            accommodation = await getAccommodationById(b.accommodationId);
            if (accommodation && b.checkInDate && b.checkOutDate) {
              const start = new Date(b.checkInDate);
              const end = new Date(b.checkOutDate);
              const nights = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
              totalPrice = nights * (accommodation.dailyRate || 0);
            }
          } catch {
            console.warn(`Не вдалося завантажити житло id=${b.accommodationId}`);
          }

          return { ...b, accommodation, totalPrice };
        })
      );

      return { ...response, content: enriched };
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося отримати список бронювань.'
      );
    }
  }
);

//  Fetch my bookings (current user)
export const fetchMyBookings = createAsyncThunk(
  'bookings/fetchMyBookings',
  async ({ page = 0, size = 5 } = {}, { rejectWithValue }) => {
    try {
      const response = await bookingsService.fetchMyBookings(page, size);

      const { getAccommodationById } = await import(
        '../../api/accommodations/accommodationService'
      );

      const enriched = await Promise.all(
        (response.content || []).map(async (b) => {
          let accommodation = null;
          let totalPrice = null;

          try {
            accommodation = await getAccommodationById(b.accommodationId);
            if (accommodation && b.checkInDate && b.checkOutDate) {
              const start = new Date(b.checkInDate);
              const end = new Date(b.checkOutDate);
              const nights = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
              totalPrice = nights * (accommodation.dailyRate || 0);
            }
          } catch {
            console.warn(`Не вдалося завантажити житло id=${b.accommodationId}`);
          }

          return { ...b, accommodation, totalPrice };
        })
      );

      return { ...response, content: enriched };
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося завантажити мої бронювання.'
      );
    }
  }
);

// Change booking status (admin)
export const changeBookingStatus = createAsyncThunk(
  'bookings/changeBookingStatus',
  async ({ booking, status }, { rejectWithValue }) => {
    try {
      const updatedBooking = {
        checkInDate: booking.checkInDate,
        checkOutDate: booking.checkOutDate,
        accommodationId: booking.accommodationId,
        status
      };

      const response = await bookingsService.updateBooking(booking.id, updatedBooking);
      return response;
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося змінити статус бронювання.'
      );
    }
  }
);

// Cancel booking (user)
export const cancelBooking = createAsyncThunk(
  'bookings/cancelBooking',
  async (id, { rejectWithValue }) => {
    try {
      await bookingsService.cancelBooking(id);
      return id;
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося скасувати бронювання.'
      );
    }
  }
);

//  Delete booking (admin)
export const deleteBooking = createAsyncThunk(
  'bookings/deleteBooking',
  async (id, { rejectWithValue }) => {
    try {
      await bookingsService.deleteBooking(id);
      return id;
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося видалити бронювання.'
      );
    }
  }
);

const bookingsSlice = createSlice({
  name: 'bookings',
  initialState,
  reducers: {
    clearCurrentBooking: (state) => {
      state.currentBooking = null;
    },
    resetPaymentStatus: (state) => {
      state.paymentStatus = 'idle';
    },
    setPage: (state, action) => {
      state.page = action.payload;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(createBooking.fulfilled, (state, action) => {
        state.bookings.push(action.payload);
        state.totalElements += 1;
        state.totalPages = Math.ceil(state.totalElements / 5);
      })
      .addCase(fetchBookings.fulfilled, (state, action) => {
        updatePageState(state, action.payload);
      })
      .addCase(fetchMyBookings.fulfilled, (state, action) => {
        updatePageState(state, action.payload);
      })
      .addCase(changeBookingStatus.fulfilled, (state, action) => {
        const idx = state.bookings.findIndex((b) => b.id === action.payload.id);
        if (idx !== -1) state.bookings[idx] = action.payload;
      })
      .addCase(cancelBooking.fulfilled, (state, action) => {
        removeBooking(state, action.payload);
      })
      .addCase(deleteBooking.fulfilled, (state, action) => {
        removeBooking(state, action.payload);
      });
  }
});

export { changeBookingStatus as updateBookingStatus };
export const { clearCurrentBooking, resetPaymentStatus, setPage } = bookingsSlice.actions;
export default bookingsSlice.reducer;
