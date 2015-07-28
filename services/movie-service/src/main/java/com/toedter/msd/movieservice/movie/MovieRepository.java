package com.toedter.msd.movieservice.movie;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(
        collectionResourceRel = "movies",
        path = "movies")
public interface MovieRepository extends PagingAndSortingRepository<Movie, String> {
}
