import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { fetchProfile, updateProfile } from '../../store/slices/userSlice';
import Notification from '../../components/Notification';

const Profile = () => {
  const dispatch = useDispatch();
  const { profile, loading, error } = useSelector((state) => state.user);

  const [formData, setFormData] = useState({ email: '', firstName: '', lastName: '' });

  useEffect(() => {
    dispatch(fetchProfile());
  }, [dispatch]);

  useEffect(() => {
    if (profile) {
      setFormData({
        email: profile.email || '',
        firstName: profile.firstName || '',
        lastName: profile.lastName || ''
      });
    }
  }, [profile]);

  const handleSubmit = (e) => {
    e.preventDefault();
    dispatch(updateProfile(formData));
  };

  return (
    <div className="container page">
      <div className="profile-card">
        <h1 className="auth-title">Мій профіль</h1>

        {error && <Notification message={error} type="danger" />}
        {loading && <p>Завантаження...</p>}

        {!loading && profile && (
          <form onSubmit={handleSubmit} className="profile-form">
            <div className="form-group">
              <input
                type="email"
                className="form-control"
                value={formData.email}
                placeholder=" "
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              />
              <label>Електронна пошта</label>
            </div>

            <div className="form-group">
              <input
                type="text"
                className="form-control"
                value={formData.firstName}
                placeholder=" "
                onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
              />
              <label>Ім’я</label>
            </div>

            <div className="form-group">
              <input
                type="text"
                className="form-control"
                value={formData.lastName}
                placeholder=" "
                onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
              />
              <label>Прізвище</label>
            </div>

            <button className="btn btn-primary" type="submit" disabled={loading}>
              {loading ? 'Збереження...' : 'Редагувати профіль'}
            </button>
          </form>
        )}
      </div>
    </div>
  );
};

export default Profile;
