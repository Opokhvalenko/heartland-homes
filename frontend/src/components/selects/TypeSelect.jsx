import React from 'react';
import { typeTranslations } from '../../utils/translations';
import '../../styles/components/admin/_admin-form.scss';

const TypeSelect = ({ value, onChange, disabled = false }) => {
  return (
    <select
      className="type-select"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      disabled={disabled}
    >
      <option value="">— Оберіть тип —</option>
      {Object.entries(typeTranslations).map(([key, { label }]) => (
        <option key={key} value={key}>
          {label}
        </option>
      ))}
    </select>
  );
};

export default TypeSelect;
