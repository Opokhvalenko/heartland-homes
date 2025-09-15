import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import {
  getCurrentUser,
  getAllUsers,
  updateProfile as apiUpdateProfile,
  updateUserRole as apiUpdateUserRole,
  deleteUser
} from '../../api/user/userService';

const savedProfile = localStorage.getItem('userProfile');

const initialState = {
  profile: savedProfile ? JSON.parse(savedProfile) : null,
  items: [],
  loading: false,
  error: null
};

//  Профіль
export const fetchProfile = createAsyncThunk(
  'user/fetchProfile',
  async (_, { rejectWithValue }) => {
    try {
      return await getCurrentUser();
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося завантажити профіль'
      );
    }
  }
);

// Оновлення профілю
export const updateProfile = createAsyncThunk(
  'user/updateProfile',
  async (userData, { rejectWithValue }) => {
    try {
      return await apiUpdateProfile(userData);
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Не вдалося оновити профіль');
    }
  }
);

// Список користувачів
export const fetchUsers = createAsyncThunk(
  'user/fetchUsers',
  async (_, { rejectWithValue }) => {
    try {
      const users = await getAllUsers();

      // нормалізація ролей
      return users.map((u) => {
        let role = u.role || (Array.isArray(u.roles) ? u.roles[0] : null);
        if (role?.startsWith('ROLE_')) role = role.replace('ROLE_', '');
        return { ...u, role };
      });
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося завантажити список'
      );
    }
  }
);

// Оновлення ролі
export const updateUserRole = createAsyncThunk(
  'user/updateUserRole',
  async ({ id, role }, { rejectWithValue }) => {
    try {
      const updated = await apiUpdateUserRole({ id, role });
      return { id, role: updated.role || role };
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || 'Не вдалося оновити роль');
    }
  }
);

//  Видалення користувача
export const removeUser = createAsyncThunk(
  'user/removeUser',
  async (id, { rejectWithValue }) => {
    try {
      await deleteUser(id);
      return id;
    } catch (err) {
      return rejectWithValue(
        err.response?.data?.message || 'Не вдалося видалити користувача'
      );
    }
  }
);

const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    clearProfile: (state) => {
      state.profile = null;
      state.error = null;
      localStorage.removeItem('userProfile');
    }
  },
  extraReducers: (builder) => {
    builder
      // PROFILE
      .addCase(fetchProfile.fulfilled, (s, { payload }) => {
        s.profile = payload;
        localStorage.setItem('userProfile', JSON.stringify(payload));
        s.loading = false;
      })
      .addCase(fetchProfile.pending, (s) => {
        s.loading = true;
      })
      .addCase(fetchProfile.rejected, (s, { payload }) => {
        s.loading = false;
        s.error = payload;
      })

      // USERS
      .addCase(fetchUsers.fulfilled, (s, { payload }) => {
        s.items = payload;
        s.loading = false;
      })

      // UPDATE ROLE
      .addCase(updateUserRole.fulfilled, (s, { payload }) => {
        const index = s.items.findIndex((u) => u.id === payload.id);
        if (index !== -1) {
          s.items[index].role = payload.role;
        }
      })

      // REMOVE USER
      .addCase(removeUser.fulfilled, (s, { payload }) => {
        s.items = s.items.filter((u) => u.id !== payload);
      });
  }
});

export const { clearProfile } = userSlice.actions;
export default userSlice.reducer;
