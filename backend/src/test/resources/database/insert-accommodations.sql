INSERT INTO accommodations (id, name, type, accommodation_status, location, city, latitude, longitude, size, daily_rate, image, is_deleted, owner_id) VALUES
    (1, 'Затишний будинок у центрі Києва', 'HOUSE', 'PERMITTED', 'Київська область, м. Київ, вул. Хрещатик, 12', 'Київ', '50.4501', '30.5234', '3 Спальні', 2500.00, 'https://www.dropbox.com/scl/fi/rbh8v6jjkhd3txxyhrfhg/photo1.jpg?rlkey=a4lrfbg4pz7zef6fv8jhcq12e&st=w0pq32wx&dl=0', 0, 1),
    (2, 'Апартаменти біля Оперного театру', 'APARTMENT', 'PERMITTED', 'Львівська область, м. Львів, просп. Свободи, 45, кв. 12, поверх 3', 'Львів', '49.8397', '24.0297', '1 Спальня', 1800.00, 'https://www.dropbox.com/scl/fi/72shzge7jjptym0m4lqpb/photo2.jpg?rlkey=6fq88kcxwwysv0epfj25oxs92&st=3iloeccy&dl=0', 0, 1),
    (3, 'Вілла біля моря', 'VACATION_HOME', 'PERMITTED', 'Одеська область, м. Одеса, вул. Шевченка, 8', 'Одеса', '46.4825', '30.7233', '4 Спальні', 3200.00, 'http://img/3', 0, 1),
    (4, 'Готель у центрі Харкова', 'HOTEL', 'PERMITTED', 'Харківська область, м. Харків, вул. Сумська, 101, кв. 45, поверх 7', 'Харків', '49.9935', '36.2304', '2 Спальні', 2100.00, 'http://img/4', 0, 1);

INSERT INTO accommodation_amenities (accommodation_id, amenity) VALUES
    (1, 'Wi-Fi'), (1, 'Кондиціонер'), (1, 'Пральна машина'),
    (2, 'Wi-Fi'), (2, 'Кухня'), (2, 'Балкон'),
    (3, 'Wi-Fi'), (3, 'Кухня'), (3, 'Парковка'),
    (4, 'Wi-Fi'), (4, 'Кондиціонер'), (4, 'Пральна машина');
