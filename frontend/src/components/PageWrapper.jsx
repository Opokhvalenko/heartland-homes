import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import Notification from './Notification';

const PageWrapper = ({ title, children, extraErrors = [] }) => {
  const authError = useSelector((s) => s.auth.error);
  const userError = useSelector((s) => s.user.error);
  const bookingsError = useSelector((s) => s.bookings.error);
  const accommodationsError = useSelector((s) => s.accommodations.error);
  const paymentsError = useSelector((s) => s.payments.error);

  const errors = [
    authError,
    userError,
    bookingsError,
    accommodationsError,
    paymentsError,
    ...extraErrors
  ].filter(Boolean);

  const [blink, setBlink] = useState(false);

  // toggle blink, тільки коли є errors
  useEffect(() => {
    if (errors.length === 0) return;

    const interval = setInterval(() => setBlink((prev) => !prev), 1500);
    return () => clearInterval(interval);
  }, [errors.length]);

  // update title + favicon
  useEffect(() => {
    const favicon = document.querySelector("link[rel='icon']");
    const errorMsg = errors[0];

    if (errors.length > 0) {
      document.title = blink
        ? `⚠️ Помилка: ${errorMsg}`
        : title
          ? `${title} | Heartland Homes`
          : 'Heartland Homes';

      if (favicon) {
        favicon.href = blink
          ? `/favicon-error.ico?v=${Date.now()}`
          : `/favicon.ico?v=${Date.now()}`;
      }
    } else {
      document.title = title ? `${title} | Heartland Homes` : 'Heartland Homes';
      if (favicon) favicon.href = '/favicon.ico';
    }
  }, [blink, errors, title]);

  return (
    <>
      {errors.map((err, idx) => (
        <Notification key={idx} message={err} type="danger" />
      ))}
      {children}
    </>
  );
};

export default PageWrapper;
