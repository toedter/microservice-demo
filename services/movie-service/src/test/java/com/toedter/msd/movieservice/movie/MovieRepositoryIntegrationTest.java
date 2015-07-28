package com.toedter.msd.movieservice.movie;

import com.toedter.msd.movieservice.MovieService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MovieService.class)
@Transactional
public class MovieRepositoryIntegrationTest {

    @Autowired
    MovieRepository movieRepository;

    @Test
    public void findsAllMovies() {
        Iterable<Movie> users = movieRepository.findAll();
        assertThat(users, is(not(emptyIterable())));
    }

    @Test
    public void createsNewMovie() {
        Long before = movieRepository.count();

        Movie user = movieRepository.save(createMovie());

        Iterable<Movie> result = movieRepository.findAll();
        assertThat(result, is(Matchers.<Movie>iterableWithSize(before.intValue() + 1)));
        assertThat(result, Matchers.hasItem(user));
    }

    public static Movie createMovie() {
        Movie testMovie = new Movie("sw", "Star Wars", 1978, 8.3f, 17, "icon");
        return testMovie;
    }
}
