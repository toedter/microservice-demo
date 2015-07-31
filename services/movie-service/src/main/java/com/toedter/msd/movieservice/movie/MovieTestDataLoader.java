package com.toedter.msd.movieservice.movie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class MovieTestDataLoader {
    private final Logger logger = LoggerFactory.getLogger(MovieTestDataLoader.class);

    @Autowired
    private MovieRepository movieRepository;

    @Transactional
    public void loadData() {
        logger.info("init test movies");
        ImdbReader imdbReader = new ImdbReader();
        try {
            imdbReader.readMoviesOnline(movieRepository);
        } catch (Exception e) {
            logger.error("Cannot read IMDB online, will use single test movie");
            Movie movie = new Movie("tt0111161", " The Shawshank Redemption", 1994, 9.3f, 1, "srthumb.jpg");
            movieRepository.save(movie);
        }
    }
}