package com.toedter.msd.movieservice.movie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImdbReader {
    public static void main(String[] args) {
    }

    public void readTop250(MovieRepository movieRepository) throws Exception {
        URL oracle = new URL("http://www.imdb.com/chart/top");
        BufferedReader in = new BufferedReader(
                new InputStreamReader(oracle.openStream()));

        Pattern p = Pattern.compile("<a href=\"/title/(\\w+)");

        String inputLine;
        String lastFoundId = "";
        List<String> ids = new ArrayList<>();

        while ((inputLine = in.readLine()) != null) {
            Matcher matcher = p.matcher(inputLine);
            while (matcher.find()) {
                String group = matcher.group();
                String imdbId = group.substring(group.lastIndexOf('/') + 1);
                if (!lastFoundId.equals(imdbId)) {
                    ids.add(imdbId);
                }
                lastFoundId = imdbId;
            }
        }
        in.close();

        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://www.omdbapi.com/?i=" + id + "&r=json",
                    String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readValue(response.getBody(), JsonNode.class);
            System.out.println(rootNode);
            String title = rootNode.get("Title").asText();
            long year = rootNode.get("Year").asLong();
            double imdbRating = rootNode.get("imdbRating").asDouble();
            String posterURL = rootNode.get("Poster").asText();

            Movie movie = new Movie(id, title, year, imdbRating, i + 1, "x");
            movieRepository.save(movie);
            System.out.println(i + 1 + ": " + title + " (" + year + ") " + imdbRating);
        }
    }
}