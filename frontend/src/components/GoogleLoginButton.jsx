import React from 'react';

const GoogleLoginButton = () => {
  const handleGoogleLogin = () => {
    // ⚡ Редірект усього вікна на бекенд-ендпоінт
    window.location.href = 'http://localhost:8080/oauth2/authorization/google';
  };

  return (
    <button type="button" className="btn-google w-100" onClick={handleGoogleLogin}>
      <img
        src="https://developers.google.com/identity/images/g-logo.png"
        alt="Google logo"
        width="20"
        height="20"
        style={{ marginRight: '8px' }}
      />
      Увійти через Google
    </button>
  );
};

export default GoogleLoginButton;
