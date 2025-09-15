import React from 'react';
import { roles } from '../../utils/roles';
import '../../styles/components/admin/_admin-users.scss';

const RoleSelect = ({ value, onChange, disabled = false }) => {
  return (
    <select
      className="role-select"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      disabled={disabled}
    >
      {Object.values(roles).map(({ value, label }) => (
        <option key={value} value={value}>
          {label}
        </option>
      ))}
    </select>
  );
};

export default RoleSelect;
