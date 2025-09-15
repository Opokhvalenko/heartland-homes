import { fixDropboxUrl } from './fixDropboxUrl';

export const getSafeImageUrl = (url) => {
  if (!url) return '/no-image.png'; // fallback якщо url немає
  try {
    const fixed = fixDropboxUrl(url);
    return fixed || '/no-image.png';
  } catch (err) {
    console.error('❌ Помилка обробки URL:', err);
    return '/no-image.png';
  }
};
