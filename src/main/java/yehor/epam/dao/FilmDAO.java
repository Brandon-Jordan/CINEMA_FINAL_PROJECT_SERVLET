package yehor.epam.dao;

import yehor.epam.entities.Film;

public interface FilmDAO extends DAO<Film> {
    boolean delete(final int filmId);
}
