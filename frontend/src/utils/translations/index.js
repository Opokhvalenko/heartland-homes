import { colors } from '../colors';

//  Типи житла
export const typeTranslations = {
  HOUSE: { label: 'Будинок', icon: '🏠', color: colors.badge.house },
  APARTMENT: { label: 'Квартира', icon: '🏢', color: colors.badge.apartment },
  HOTEL: { label: 'Готель', icon: '🏨', color: colors.badge.hotel },
  VACATION_HOME: {
    label: 'Дім для відпочинку',
    icon: '🌴',
    color: colors.badge.vacationHome
  },
  HOSTEL: { label: 'Хостел', icon: '🛏️', color: colors.badge.hostel },
  COTTAGE: { label: 'Котедж', icon: '🏡', color: colors.badge.cottage }
};

//  Мапінг типів житла
export const mapType = (type) => {
  if (!type) return { label: 'Невідомо', icon: '❓', color: '#ccc' };
  const key = type.toUpperCase();
  return typeTranslations[key] || { label: type, icon: '❓', color: '#ccc' };
};

//  Зручності
export const amenityTranslations = {
  wifi: {
    label: 'Wi-Fi',
    icon: '📶',
    slug: 'wifi',
    color: colors.badge.wifi,
    aliases: ['wi-fi', 'wifi', 'вайфай']
  },
  kitchen: {
    label: 'Кухня',
    icon: '🍳',
    slug: 'kitchen',
    color: colors.badge.kitchen,
    aliases: ['кухня', 'kitchen']
  },
  ac: {
    label: 'Кондиціонер',
    icon: '❄️',
    slug: 'ac',
    color: colors.badge.ac,
    aliases: ['кондиціонер', 'air conditioner', 'ac']
  },
  parking: {
    label: 'Парковка',
    icon: '🅿️',
    slug: 'parking',
    color: colors.badge.parking,
    aliases: ['парковка', 'parking']
  },
  washer: {
    label: 'Пральна машина',
    icon: '🧺',
    slug: 'washer',
    color: colors.badge.washer,
    aliases: ['пральна машина', 'washer', 'laundry']
  }
};

//  Мапінг зручностей
export const mapAmenity = (slug = '') => {
  const lower = slug.toLowerCase();
  for (const key in amenityTranslations) {
    const { aliases, ...rest } = amenityTranslations[key];
    if (aliases.some((alias) => lower.includes(alias))) {
      return rest;
    }
  }
  return { label: slug || 'Інше', icon: '❔', slug: 'other', color: colors.badge.other };
};

//  Статуси
export const statusTranslations = {
  PENDING: { label: 'Очікує', color: colors.warning, slug: 'pending' },
  PAID: { label: 'Оплачено', color: colors.success, slug: 'paid' },
  CONFIRMED: { label: 'Підтверджено', color: colors.success, slug: 'confirmed' },
  CANCELED: { label: 'Скасовано', color: colors.danger, slug: 'canceled' },
  EXPIRED: { label: 'Прострочено', color: colors.gray400, slug: 'expired' },
  REQUIRES_VERIFICATION: {
    label: 'Очікує перевірки',
    color: colors.warning,
    slug: 'requires-verification'
  },
  PERMITTED: { label: 'Дозволено', color: colors.success, slug: 'permitted' },
  REJECTED: { label: 'Відхилено', color: colors.danger, slug: 'rejected' }
};

//  Мапінг статусів
export const mapStatus = (status = '') => {
  const key = status.toUpperCase();
  return (
    statusTranslations[key] || {
      label: status || 'Невідомо',
      color: colors.gray400,
      slug: 'unknown'
    }
  );
};

// Типи платежів
export const paymentTypeTranslations = {
  PAYMENT: { label: 'Оплата', slug: 'payment', icon: '💳' },
  REFUND: { label: 'Повернення', slug: 'refund', icon: '↩️' }
};

// Мапінг типів платежів
export const mapPaymentType = (type = '') => {
  const key = type.toUpperCase();
  return (
    paymentTypeTranslations[key] || {
      label: type || 'Невідомо',
      slug: 'unknown',
      icon: '❔'
    }
  );
};
