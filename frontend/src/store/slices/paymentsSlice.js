import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {
  createPayment as createPaymentService,
  fetchPaymentsByUser as fetchPaymentsByUserService,
  cancelPayment as cancelPaymentService,
  getAllPaymentsService
} from '../../api/payments/paymentService';

const initialState = {
  payment: null,
  payments: [],
  totalPages: 1,
  createStatus: 'idle',
  fetchStatus: 'idle',
  cancelStatus: 'idle',
  error: null
};

//  Create payment
export const createPayment = createAsyncThunk(
  'payments/createPayment',
  async ({ bookingId, paymentType = 'PAYMENT' }, { rejectWithValue }) => {
    try {
      const response = await createPaymentService(bookingId, paymentType);
      return response;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Не вдалося створити платіж');
    }
  }
);

//  Fetch payments by user
export const fetchPaymentsByUser = createAsyncThunk(
  'payments/fetchByUser',
  async ({ userId, pageable }, { rejectWithValue }) => {
    try {
      const response = await fetchPaymentsByUserService(userId, pageable);
      return response;
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося отримати список платежів'
      );
    }
  }
);

//  Cancel payment
export const cancelPayment = createAsyncThunk(
  'payments/cancelPayment',
  async (paymentId, { rejectWithValue }) => {
    try {
      await cancelPaymentService(paymentId);
      return paymentId;
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося скасувати платіж'
      );
    }
  }
);

//  Fetch all payments (admin)
export const fetchAllPayments = createAsyncThunk(
  'payments/fetchAll',
  async (params, { rejectWithValue }) => {
    try {
      const response = await getAllPaymentsService(params);
      return response;
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося отримати всі платежі'
      );
    }
  }
);

const paymentsSlice = createSlice({
  name: 'payments',
  initialState,
  reducers: {
    resetPayment: (state) => {
      state.payment = null;
      state.createStatus = 'idle';
      state.error = null;
    },
    resetPaymentsList: (state) => {
      state.payments = [];
      state.totalPages = 1;
      state.fetchStatus = 'idle';
      state.error = null;
    }
  },
  extraReducers: (builder) => {
    builder
      // createPayment
      .addCase(createPayment.pending, (state) => {
        state.createStatus = 'loading';
      })
      .addCase(createPayment.fulfilled, (state, action) => {
        state.createStatus = 'succeeded';
        state.payment = action.payload;
      })
      .addCase(createPayment.rejected, (state, action) => {
        state.createStatus = 'failed';
        state.error = action.payload;
      })

      // fetchPaymentsByUser
      .addCase(fetchPaymentsByUser.pending, (state) => {
        state.fetchStatus = 'loading';
      })
      .addCase(fetchPaymentsByUser.fulfilled, (state, action) => {
        state.fetchStatus = 'succeeded';
        state.payments = action.payload.content || [];
        state.totalPages = action.payload.totalPages || 1;
      })
      .addCase(fetchPaymentsByUser.rejected, (state, action) => {
        state.fetchStatus = 'failed';
        state.error = action.payload;
      })

      //  cancelPayment
      .addCase(cancelPayment.pending, (state) => {
        state.cancelStatus = 'loading';
      })
      .addCase(cancelPayment.fulfilled, (state, action) => {
        state.cancelStatus = 'succeeded';
        state.payments = state.payments.map((p) =>
          p.id === action.payload ? { ...p, status: 'CANCELED' } : p
        );
      })
      .addCase(cancelPayment.rejected, (state, action) => {
        state.cancelStatus = 'failed';
        state.error = action.payload;
      })

      // fetchAllPayments (admin)
      .addCase(fetchAllPayments.pending, (state) => {
        state.fetchStatus = 'loading';
      })
      .addCase(fetchAllPayments.fulfilled, (state, action) => {
        state.fetchStatus = 'succeeded';
        state.payments = action.payload.content || action.payload || [];
        state.totalPages = action.payload.totalPages || 1;
      })
      .addCase(fetchAllPayments.rejected, (state, action) => {
        state.fetchStatus = 'failed';
        state.error = action.payload;
      });
  }
});

export const { resetPayment, resetPaymentsList } = paymentsSlice.actions;
export default paymentsSlice.reducer;
