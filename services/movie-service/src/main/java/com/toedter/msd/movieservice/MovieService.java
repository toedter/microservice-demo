package com.toedter.msd.movieservice;

import com.toedter.msd.movieservice.movie.MovieTestDataLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class MovieService {

    @Bean(initMethod = "loadData")
    MovieTestDataLoader RepositoryTestData() {
        return new MovieTestDataLoader();
    }

    public static void main(String[] args) {
        SpringApplication.run(MovieService.class, args);
    }
}
