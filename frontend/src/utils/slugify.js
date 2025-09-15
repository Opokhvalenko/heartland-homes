/**
 * Перетворює рядок у slug
 * Напр. "Vacation Home" -> "vacation_home"
 *      "Café del Mar"   -> "cafe_del_mar"
 *      "Тестова Стаття" -> "тестова_стаття"
 */
const slugify = (text = '') =>
  text
    .toString()
    .normalize('NFD') // розбиває діакритичні символи (é -> e +  ́)
    .replace(/[\u0300-\u036f]/g, '') // прибирає діакритичні знаки
    .toLowerCase()
    .trim()
    .replace(/\s+/g, '_') // пробіли -> _
    .replace(/[^\w-]+/g, '') // видаляє все, крім букв, цифр, "_", "-"
    .replace(/_+/g, '_') // кілька підряд "_" -> один "_"
    .replace(/^_+|_+$/g, ''); // прибирає "_" на початку і в кінці

export default slugify;
