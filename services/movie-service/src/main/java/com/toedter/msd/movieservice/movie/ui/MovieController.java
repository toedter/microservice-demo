package com.toedter.msd.movieservice.movie.ui;

import com.toedter.msd.movieservice.movie.Movie;
import com.toedter.msd.movieservice.movie.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MovieController {

    private final MovieRepository movieRepository;

    @Autowired
    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @RequestMapping(value = "/movies.html", method = RequestMethod.GET)
    public ModelAndView movieList(@ModelAttribute("movie") Movie movie) {
        Sort sort = new Sort(Sort.Direction.ASC, "rank");
        return new ModelAndView("movies", "movies", movieRepository.findAll(sort));
    }

    @RequestMapping(value = "/movies.html", method = RequestMethod.POST)
    public String post(@ModelAttribute("movie") Movie movie) {
        movieRepository.save(movie);
        return "redirect:/movies.html";
    }

    @RequestMapping(value = "/movies/{id}.html", method = RequestMethod.DELETE)
    public String delete(@ModelAttribute("movie") Movie movie) {
        if (movie.getId() != null) {
            movieRepository.delete(movie.getId());
        }
        return "redirect:/movies.html";
    }
}
