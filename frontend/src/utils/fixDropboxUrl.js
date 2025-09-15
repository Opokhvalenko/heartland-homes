export const fixDropboxUrl = (url) => {
  if (!url) return '';

  // Якщо є ?dl=0 → замінюємо на raw=1
  if (url.includes('dl=0')) {
    return url.replace('dl=0', 'raw=1');
  }

  // Якщо є ?dl=1 → замінюємо на raw=1 (стабільніший варіант)
  if (url.includes('dl=1')) {
    return url.replace('dl=1', 'raw=1');
  }

  // Якщо є вже raw=1 → залишаємо
  if (url.includes('raw=1')) {
    return url;
  }

  // Якщо немає жодного параметра → додаємо raw=1
  if (!url.includes('?')) {
    return `${url}?raw=1`;
  }

  return url;
};
