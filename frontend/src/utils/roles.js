//  Словник ролей
export const roles = {
  CUSTOMER: { value: 'CUSTOMER', label: 'Клієнт' },
  MANAGER: { value: 'MANAGER', label: 'Менеджер' }
};

//  Хелпер для зручного отримання лейблу
export const getRoleLabel = (role) => {
  return roles[role]?.label || role;
};
