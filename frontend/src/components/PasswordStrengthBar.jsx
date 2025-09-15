import React from 'react';

const PasswordStrengthBar = ({ password }) => {
  if (!password) return null;

  const hasLetters = /[a-zA-Z]/.test(password);
  const hasNumbers = /\d/.test(password);
  const hasSpecial = /[^a-zA-Z0-9]/.test(password);

  let label = 'Слабкий';
  let color = 'red';
  let level = 25;

  if (password.length < 6) {
    label = 'Слабкий';
    color = 'red';
    level = 25;
  } else if (password.length >= 6 && hasLetters && hasNumbers) {
    if (password.length >= 8 && hasSpecial) {
      label = 'Сильний';
      color = 'green';
      level = 100;
    } else {
      label = 'Середній';
      color = 'orange';
      level = 50;
    }
  }

  return (
    <div style={{ marginTop: '5px' }}>
      <p style={{ color, marginBottom: '5px' }}>Надійність пароля: {label}</p>
      <div
        style={{
          height: '6px',
          borderRadius: '4px',
          background: '#ddd',
          overflow: 'hidden'
        }}
      >
        <div
          style={{
            width: `${level}%`,
            height: '100%',
            background: color,
            transition: 'width 0.3s ease'
          }}
        />
      </div>
    </div>
  );
};

export default PasswordStrengthBar;
