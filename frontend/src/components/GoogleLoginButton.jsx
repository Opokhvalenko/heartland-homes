import React from 'react';

const GoogleLoginButton = () => {
  const handleGoogleLogin = () => {
    const api = import.meta.env.VITE_API_URL;
    window.location.href = `${api}/oauth2/authorization/google`;
  };

  return (
    <button type="button" className="btn-google w-100" onClick={handleGoogleLogin}>
      <img
        src="https://developers.google.com/identity/images/g-logo.png"
        alt="Google logo"
        width="20"
        height="20"
        style={{ marginRight: 8 }}
      />
      Увійти через Google
    </button>
  );
};

export default GoogleLoginButton;