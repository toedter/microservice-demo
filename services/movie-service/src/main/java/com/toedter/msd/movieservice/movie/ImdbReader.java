package com.toedter.msd.movieservice.movie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImdbReader {
    static String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public void initializeMovies(MovieRepository movieRepository) throws Exception {
        String moviesJson = readFile("movies.json", StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readValue(moviesJson, JsonNode.class);
        String date = rootNode.get("date").asText();
        System.out.println(date);


        JsonNode movies = rootNode.get("movies");
        int rating = 1;
        Iterator<JsonNode> iterator = movies.iterator();
        while(iterator.hasNext()) {
            JsonNode movie = iterator.next();
            handleMovie(movieRepository, rating++, movie.get("imdbID").asText(),movie);
        }
    }

    public void readMoviesOnline(MovieRepository movieRepository) throws Exception {
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

        PrintWriter movieWriter = new PrintWriter("movies.json", "UTF-8");
        movieWriter.println("{");
        movieWriter.println("  \"date\": \"" + new Date() + "\",");
        movieWriter.println("  \"movies\": [");

        int movieCount = 2;
        for (int i = 0; i < movieCount; i++) {
            String id = ids.get(i);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(
                    "http://www.omdbapi.com/?i=" + id + "&r=json",
                    String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readValue(response.getBody(), JsonNode.class);

            String jsonComma = i == movieCount - 1 ? "" : ",";
            movieWriter.println("    " + rootNode + jsonComma);
            System.out.println(rootNode);
            handleMovie(movieRepository, i+1, id, rootNode);
        }
        movieWriter.println("  ]");
        movieWriter.println("}");
        movieWriter.close();
    }

    private void handleMovie(MovieRepository movieRepository, int rating, String id, JsonNode rootNode) {
        String title = rootNode.get("Title").asText();
        long year = rootNode.get("Year").asLong();
        double imdbRating = rootNode.get("imdbRating").asDouble();
        String posterURL = rootNode.get("Poster").asText();

        Movie movie = new Movie(id, title, year, imdbRating, rating, "x");
        if (movieRepository != null) {
            movieRepository.save(movie);
        }
        System.out.println(rating + ": " + title + " (" + year + ") " + imdbRating);
    }

    public static void main(String[] args) throws Exception {
        ImdbReader imdbReader = new ImdbReader();
        imdbReader.initializeMovies(null);
    }
}