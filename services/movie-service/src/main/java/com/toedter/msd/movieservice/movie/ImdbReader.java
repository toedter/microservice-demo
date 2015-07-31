package com.toedter.msd.movieservice.movie;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toedter.msd.movieservice.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.*;
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

    private final Logger logger = LoggerFactory.getLogger(ImdbReader.class);

    public void initializeMovies(MovieRepository movieRepository) throws Exception {
        File movieDirs = new File(MovieService.movieDir + "/thumbs");
        movieDirs.mkdirs();

        String moviesJson;
        long date;
        JsonNode rootNode = null;
        boolean releadMoviesOnline = false;
        boolean reloadMovieFile = true;
        ObjectMapper mapper = new ObjectMapper();

        try {
            moviesJson = readFile(MovieService.movieDir + "/movies.json", StandardCharsets.UTF_8);
            rootNode = mapper.readValue(moviesJson, JsonNode.class);
            date = rootNode.get("date").asLong();

            long oneDayInMillis = 1000 * 60 * 60 * 24;
            Date lastLoadedMoviesDate = new Date(date);

            long timePassed = new Date().getTime() - lastLoadedMoviesDate.getTime();
            if (timePassed > oneDayInMillis) {
                releadMoviesOnline = true;
            }
            reloadMovieFile = false;
        } catch (Exception e) {
            releadMoviesOnline = true;
        }

        if (releadMoviesOnline) {
            readMoviesOnline(movieRepository);
        }

        if (reloadMovieFile) {
            moviesJson = readFile(MovieService.movieDir + "/movies.json", StandardCharsets.UTF_8);
            rootNode = mapper.readValue(moviesJson, JsonNode.class);
        }

        JsonNode movies = rootNode.get("movies");
        int rating = 1;
        Iterator<JsonNode> iterator = movies.iterator();
        while (iterator.hasNext()) {
            JsonNode movie = iterator.next();
            handleMovie(movieRepository, rating++, movie.get("imdbID").asText(), movie);
        }
    }

    private void readMoviesOnline(MovieRepository movieRepository) throws Exception {
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

        PrintWriter movieWriter = new PrintWriter(MovieService.movieDir + "/movies.json", "UTF-8");
        movieWriter.println("{");
        movieWriter.println("  \"date\": \"" + new Date().getTime() + "\",");
        movieWriter.println("  \"movies\": [");

        int movieCount = ids.size();
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

            String imageURL = rootNode.get("Poster").asText();

            String movieImage = MovieService.movieDir + "/thumbs/" + id + ".jpg";
            try {
                saveImage(imageURL, movieImage);
            } catch (IOException e) {
                logger.error("cannot save movie image");
            }
        }
        movieWriter.println("  ]");
        movieWriter.println("}");
        movieWriter.close();
    }

    private void handleMovie(MovieRepository movieRepository, int rank, String id, JsonNode rootNode) {
        String title = rootNode.get("Title").asText();
        long year = rootNode.get("Year").asLong();
        double imdbRating = rootNode.get("imdbRating").asDouble();

        String movieImage = "/" + MovieService.movieDir + "/thumbs/" + id + ".jpg";
        Movie movie = new Movie(id, title, year, imdbRating, rank, movieImage);
        if (movieRepository != null) {
            movieRepository.save(movie);
        }
        logger.info("found movie: " + rank + ": " + title + " (" + year + ") " + imdbRating);
    }

    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
        URL url = new URL(imageUrl);
        InputStream inputStream = url.openStream();
        OutputStream outputStream = new FileOutputStream(destinationFile);

        byte[] b = new byte[2048];
        int length;

        while ((length = inputStream.read(b)) != -1) {
            outputStream.write(b, 0, length);
        }

        inputStream.close();
        outputStream.close();
    }

    private String readFile(String path, Charset encoding)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String[] args) throws Exception {
        ImdbReader imdbReader = new ImdbReader();
        imdbReader.initializeMovies(null);
    }
}