package com.toedter.msd.movieservice;

import com.toedter.msd.movieservice.movie.MovieLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableDiscoveryClient
public class MovieService extends WebMvcConfigurerAdapter {

    final public static String movieDir = "external-movies";

    @Bean(initMethod = "loadData")
    MovieLoader RepositoryTestData() {
        return new MovieLoader();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/" + movieDir + "/**")
                .addResourceLocations("file:" + movieDir + "/");
    }

    public static void main(String[] args) {
        SpringApplication.run(MovieService.class, args);
    }
}
