import React, { Suspense, lazy, useEffect } from 'react';
import { Routes, Route } from 'react-router-dom';
import { useDispatch } from 'react-redux';

// Спільні компоненти
import Header from './components/Header.jsx';
import Footer from './components/Footer.jsx';
import PageWrapper from './components/PageWrapper.jsx';
import ScrollToTop from './components/ScrollToTop.jsx';
import ErrorBoundary from './components/ErrorBoundary.jsx';

// Auth (легкі — можна лишити не-lazy, як у тебе)
import ProtectedRoute from './pages/Auth/ProtectedRoute.jsx';
import Login from './pages/Auth/Login.jsx';
import Register from './pages/Auth/Register.jsx';
import LoginSuccess from './pages/Auth/LoginSuccess.jsx';

// 👇 Ледачимо важкі/вторинні сторінки
const Home = lazy(() => import('./pages/Home.jsx'));
const Accommodations = lazy(() => import('./pages/Accommodations/Accommodations.jsx'));
const AccommodationDetails = lazy(
  () => import('./pages/Accommodations/AccommodationDetails.jsx')
);

// User
const Profile = lazy(() => import('./pages/User/Profile.jsx'));
const MyBookings = lazy(() => import('./pages/User/MyBookings.jsx'));
const BookingDetails = lazy(() => import('./pages/BookingDetails.jsx'));
const Payment = lazy(() => import('./pages/User/Payment.jsx'));
const PaymentSuccess = lazy(() => import('./pages/User/PaymentSuccess.jsx'));
const PaymentCancel = lazy(() => import('./pages/User/PaymentCancel.jsx'));
const PaymentsList = lazy(() => import('./pages/User/PaymentsList.jsx'));

// Admin
const AdminLayout = lazy(() => import('./components/AdminLayout.jsx'));
const AdminDashboard = lazy(() => import('./pages/Admin/AdminDashboard.jsx'));
const AdminAccommodations = lazy(() => import('./pages/Admin/AdminAccommodations.jsx'));
const AdminEditAccommodation = lazy(
  () => import('./pages/Admin/AdminEditAccommodation.jsx')
);
const AdminBookings = lazy(() => import('./pages/Admin/AdminBookings.jsx'));
const AdminBookingDetails = lazy(() => import('./pages/Admin/AdminBookingDetails.jsx'));
const AdminUsers = lazy(() => import('./pages/Admin/AdminUsers.jsx'));
const AdminPayments = lazy(() => import('./pages/Admin/AdminPayments.jsx'));

// Shared
const CreateAccommodation = lazy(
  () => import('./pages/Accommodations/CreateAccommodation.jsx')
);
const MyAccommodations = lazy(
  () => import('./pages/Accommodations/MyAccommodations.jsx')
);
const EditMyAccommodation = lazy(
  () => import('./pages/Accommodations/EditMyAccommodation.jsx')
);

// Info pages (раніше були eager)
const FAQ = lazy(() => import('./pages/Info/FAQ.jsx'));
const Support = lazy(() => import('./pages/Info/Support.jsx'));
const About = lazy(() => import('./pages/Info/About.jsx'));
const OffersPage = lazy(() => import('./pages/Info/OffersPage.jsx'));
const Terms = lazy(() => import('./pages/Info/Terms.jsx'));
const Privacy = lazy(() => import('./pages/Info/Privacy.jsx'));
const Popular = lazy(() => import('./pages/Info/Popular.jsx'));
const Partners = lazy(() => import('./pages/Info/Partners.jsx'));
const Cookies = lazy(() => import('./pages/Info/Cookies.jsx'));

const NotFound = lazy(() => import('./pages/NotFound.jsx'));

import './styles/main.scss';

// Redux
import { setUser } from './store/slices/authSlice';
import authService from './api/auth/authService';

function App() {
  const dispatch = useDispatch();

  useEffect(() => {
    const stored = JSON.parse(localStorage.getItem('auth'));
    if (stored?.token) {
      authService
        .getProfile()
        .then((profile) => {
          const cleanRole = profile.role?.startsWith('ROLE_')
            ? profile.role.replace('ROLE_', '')
            : profile.role;
          dispatch(setUser({ token: stored.token, ...profile, cleanRole }));
        })
        .catch(() => {
          localStorage.removeItem('auth');
          localStorage.removeItem('userProfile');
        });
    }
  }, [dispatch]);

  return (
    <div className="main-layout">
      <Header />
      <main className="main-content">
        <ScrollToTop />
        <ErrorBoundary>
          <Suspense fallback={<p className="text-center mt-5">Завантаження...</p>}>
            <Routes>
              {/* Public routes */}
              <Route
                path="/"
                element={
                  <PageWrapper title="Головна">
                    <Home />
                  </PageWrapper>
                }
              />
              <Route
                path="/accommodations"
                element={
                  <PageWrapper title="Усі помешкання">
                    <Accommodations />
                  </PageWrapper>
                }
              />
              <Route
                path="/accommodations/:id"
                element={
                  <PageWrapper title="Деталі помешкання">
                    <AccommodationDetails />
                  </PageWrapper>
                }
              />

              {/* Create accommodation (CUSTOMER + MANAGER) */}
              <Route
                path="/accommodations/new"
                element={
                  <ProtectedRoute requiredRole={['CUSTOMER', 'MANAGER']}>
                    <PageWrapper title="Створити помешкання">
                      <CreateAccommodation />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />

              {/* My accommodations (CUSTOMER + MANAGER) */}
              <Route
                path="/my-accommodations"
                element={
                  <ProtectedRoute requiredRole={['CUSTOMER', 'MANAGER']}>
                    <PageWrapper title="Мої помешкання">
                      <MyAccommodations />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/my-accommodations/edit/:id"
                element={
                  <ProtectedRoute requiredRole={['CUSTOMER', 'MANAGER']}>
                    <PageWrapper title="Редагувати моє помешкання">
                      <EditMyAccommodation />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />

              {/* Admin accommodations (MANAGER only) */}
              <Route
                path="/admin/accommodations"
                element={
                  <ProtectedRoute requiredRole="MANAGER">
                    <PageWrapper title="Усі помешкання">
                      <AdminAccommodations />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />

              {/* Auth */}
              <Route
                path="/login"
                element={
                  <PageWrapper title="Вхід">
                    <Login />
                  </PageWrapper>
                }
              />
              <Route path="/auth/success" element={<LoginSuccess />} />
              <Route path="/login/success" element={<LoginSuccess />} />
              <Route
                path="/register"
                element={
                  <PageWrapper title="Реєстрація">
                    <Register />
                  </PageWrapper>
                }
              />

              {/* User routes */}
              <Route
                path="/my-bookings"
                element={
                  <ProtectedRoute>
                    <PageWrapper title="Мої бронювання">
                      <MyBookings />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/my-bookings/:id"
                element={
                  <ProtectedRoute>
                    <PageWrapper title="Деталі бронювання">
                      <BookingDetails />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/profile"
                element={
                  <ProtectedRoute>
                    <PageWrapper title="Мій профіль">
                      <Profile />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/payment/:bookingId"
                element={
                  <ProtectedRoute>
                    <PageWrapper title="Оплата">
                      <Payment />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/payments/success"
                element={
                  <ProtectedRoute>
                    <PageWrapper title="Оплата успішна">
                      <PaymentSuccess />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/payments/cancel"
                element={
                  <ProtectedRoute>
                    <PageWrapper title="Оплату скасовано">
                      <PaymentCancel />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/my-payments"
                element={
                  <ProtectedRoute>
                    <PageWrapper title="Мої платежі">
                      <PaymentsList />
                    </PageWrapper>
                  </ProtectedRoute>
                }
              />

              {/* Admin routes (MANAGER only) */}
              <Route
                path="/admin/*"
                element={
                  <ProtectedRoute requiredRole="MANAGER">
                    <AdminLayout />
                  </ProtectedRoute>
                }
              >
                <Route index element={<AdminDashboard />} />
                <Route
                  path="accommodations/edit/:id"
                  element={<AdminEditAccommodation />}
                />
                <Route path="bookings" element={<AdminBookings />} />
                <Route path="bookings/:id" element={<AdminBookingDetails />} />
                <Route path="users" element={<AdminUsers />} />
                <Route path="payments" element={<AdminPayments />} />
              </Route>

              {/* Info routes */}
              <Route
                path="/faq"
                element={
                  <PageWrapper title="FAQ">
                    <FAQ />
                  </PageWrapper>
                }
              />
              <Route
                path="/support"
                element={
                  <PageWrapper title="Зв'язатися з нами">
                    <Support />
                  </PageWrapper>
                }
              />
              <Route
                path="/about"
                element={
                  <PageWrapper title="Про нас">
                    <About />
                  </PageWrapper>
                }
              />
              <Route
                path="/offers"
                element={
                  <PageWrapper title="Акції та знижки">
                    <OffersPage />
                  </PageWrapper>
                }
              />
              <Route
                path="/popular"
                element={
                  <PageWrapper title="Популярні напрямки">
                    <Popular />
                  </PageWrapper>
                }
              />
              <Route
                path="/partners"
                element={
                  <PageWrapper title="Партнери">
                    <Partners />
                  </PageWrapper>
                }
              />
              <Route
                path="/terms"
                element={
                  <PageWrapper title="Умови використання">
                    <Terms />
                  </PageWrapper>
                }
              />
              <Route
                path="/privacy"
                element={
                  <PageWrapper title="Політика конфіденційності">
                    <Privacy />
                  </PageWrapper>
                }
              />
              <Route
                path="/cookies"
                element={
                  <PageWrapper title="Файли Cookie">
                    <Cookies />
                  </PageWrapper>
                }
              />

              {/* Catch-all */}
              <Route
                path="*"
                element={
                  <PageWrapper title="Сторінку не знайдено">
                    <NotFound />
                  </PageWrapper>
                }
              />
            </Routes>
          </Suspense>
        </ErrorBoundary>
      </main>
      <Footer />
    </div>
  );
}

export default App;
