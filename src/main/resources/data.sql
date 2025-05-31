MERGE INTO genres (genre_ID, genre_name)
KEY (genre_ID)
VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'),
       (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');

MERGE INTO ratingOfFilmByMpa (rating_mpa_ID, rating_name)
KEY (rating_mpa_ID)
VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');