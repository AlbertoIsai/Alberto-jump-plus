import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MovieRatingApp {
    private final Scanner scanner = new Scanner(System.in);
    private final Map<Integer, Movie> movies;
    private final Map<String, User> users;

    public MovieRatingApp(Map<String, User> users, Map<Integer, Movie> movies) {
        this.users = users;
        this.movies = movies;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Map<String, User> users = readUsersFromFile("users.txt");
        Map<Integer, Movie> movies = readMoviesFromFile("movies.txt");

        MovieRatingApp app = new MovieRatingApp(users, movies);

        User currentUser = null;
        boolean quit = false;
        while (currentUser == null) {
            System.out.println("+===================+");
            System.out.printf("|%-15s\t|\n|%-15s\t|\n|%-15s\t|\n|%-15s\t|\n", "1. REGISTER", "2. LOGIN", "3. VIEW MOVIES", "4. EXIT");
            System.out.println("+===================+");

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                currentUser = app.registerUser();
            } else if (choice == 2) {
                currentUser = app.loginUser();
            } else if (choice == 3) {
                app.viewMovies();
            } else if (choice == 4) {
                System.exit(0);
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }

        while (!quit) {
            System.out.println("+===================+");
            System.out.printf("|%-15s\t|\n|%-15s\t|\n|%-15s\t|\n", "1. VIEW MOVIES", "2. RATE A MOVIE", "3. LOGOUT");
            System.out.println("+===================+");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> app.viewMovies();
                case 2 -> app.rateMovie();
                case 3 -> {
                    System.out.println("Logging out...");
                    quit = true;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }

        writeUsersToFile("users.txt", users);
        writeMoviesToFile("movies.txt", movies);
    }

    public static void writeUsersToFile(String fileName, Map<String, User> users) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            for (Map.Entry<String, User> entry : users.entrySet()) {
                String email = entry.getKey();
                User user = entry.getValue();
                writer.println(email + "," + user.password());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public static Map<String, User> readUsersFromFile(String filename) {
        Map<String, User> users = new HashMap<>();

        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String[] userData = scanner.nextLine().split(",");
                String email = userData[0];
                String password = userData[1];

                users.put(email, new User(email, password));
            }
        } catch (FileNotFoundException e) {
            System.out.println("User file not found");
        }

        return users;
    }

    public static void writeMoviesToFile(String fileName, Map<Integer, Movie> movies) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (Map.Entry<Integer, Movie> entry : movies.entrySet()) {
                Movie movie = entry.getValue();
                String movieString = movie.getTitle() + ";" + movie.getRatingCount() + ";" + String.format("%.1f", movie.getRatingSum());
                writer.println(movieString);
            }
        } catch (IOException e) {
            System.out.println("Error writing movies to file: " + e.getMessage());
        }
    }

    public static Map<Integer, Movie> readMoviesFromFile(String filename) {
        Map<Integer, Movie> movies = new HashMap<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            int counter = 1;
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(";");
                String title = parts[0];
                int ratingCount = Integer.parseInt(parts[1]);
                double ratingSum = Double.parseDouble(parts[2]);
                movies.put(counter++, new Movie(title, ratingCount, (int) ratingSum));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filename);
        }
        return movies;
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void writeMoviesToFile() {
        try (PrintWriter writer = new PrintWriter("movies.txt")) {
            for (Movie movie : movies.values()) {
                writer.println(movie.getTitle() + ";" + movie.getRatingCount() + ";" + movie.getRatingSum());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Could not write movies file: " + "movies.txt");
        }
    }

    public void viewMovies() {
        System.out.println("+=========================================================================+");
        System.out.printf("|%-5s %-30s %-20s %-15s|\n", "Id", "Movie", "Avg. Rating", "# of Ratings");
        for (Map.Entry<Integer, Movie> entry : movies.entrySet()) {
            Movie movie = entry.getValue();
            int id = entry.getKey();
            double averageRating = movie.getAverageRating();
            int ratingCount = movie.getRatingCount();
            System.out.printf("|%-5s %-30s ", id, movie.getTitle());
            if (averageRating == 0.0) {
                System.out.printf("%-20s", "N/A");
            } else {
                System.out.printf("%-20.1f", averageRating);
            }
            System.out.printf(" %-15d|\n", ratingCount);
        }
        System.out.println("+=========================================================================+");
    }

    private void rateMovie() {
        System.out.println("+===========+\n|\tRATE\t|\n+===========+");

        viewMovies();

        System.out.print("Enter the movie id you want to rate: ");
        int movieId = scanner.nextInt();
        scanner.nextLine();

        Movie movie = movies.get(movieId);
        if (movie == null) {
            System.out.println("Movie not found");
            return;
        }

        System.out.printf("+=========================================================================+\n| Movie:\t%-62s|\n", movie.getTitle());
        System.out.printf("|%-73s|", "");
        System.out.printf("\n| Rating:\t%-62.2f|", movie.getAverageRating());
        printInDesiredFormat("0.", "Really Bad:");
        printInDesiredFormat("1.", "Bad");
        printInDesiredFormat("2.", "Not Good");
        printInDesiredFormat("3.", "Okay");
        printInDesiredFormat("4.", "Good");
        printInDesiredFormat("5.", "Great");
        System.out.printf("\n|%-73s|", " ");
        printInDesiredFormat("6.", "Exit");
        System.out.println("\n+=========================================================================+\n");
        int ratingScore = scanner.nextInt();
        scanner.nextLine();

        if (ratingScore < 0 || ratingScore > 6) {
            System.out.println("Invalid rating score. Please try again.");

            return;
        }

        if (ratingScore == 6) {
            return;
        }

        movie.addRating(ratingScore);

        writeMoviesToFile();

        System.out.println("Thank you for rating \"" + movie.getTitle() + "\"!");
    }

    private void printInDesiredFormat(String desc, String value) {
        System.out.printf("\n|%-3s%-70s|", desc, value);
    }

    public User registerUser() {
        String email = getEmailInput();
        String password = getPasswordInput();
        User user = new User(email, password);
        users.put(email, user);
        return user;
    }

    private String getEmailInput() {
        String email;
        while (true) {
            System.out.print("Enter email: ");
            email = scanner.nextLine();
            if (isValidEmail(email)) {
                if (users.containsKey(email)) {
                    System.out.println("Email already in use. Please choose a different email.");
                } else {
                    break;
                }
            } else {
                System.out.println("Invalid email. Please enter a valid email.");
            }
        }
        return email;
    }

    private String getPasswordInput() {
        System.out.print("Enter password: ");
        return scanner.nextLine();
    }

    private User loginUser() {
        System.out.println("+===========+\n|\tLOGIN\t|\n+===========+");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Email: ");
            String email = scanner.nextLine();

            if (!isValidEmail(email)) {
                System.out.println("Invalid email format. Try Again");
                continue;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = users.get(email);
            if (user == null || !user.password().equals(password)) {
                System.out.println("Invalid email or password. Please try again.");
                continue;
            }

            System.out.println("Welcome to the Movie Rating App, " + user.email() + "!");
            return user;
        }
    }

    private static class Movie {
        private final String title;
        private int ratingCount;
        private double ratingSum;

        public Movie(String title, int ratingCount, double ratingSum) {
            this.title = title;
            this.ratingCount = ratingCount;
            this.ratingSum = ratingSum;
        }

        public String getTitle() {
            return title;
        }

        public int getRatingCount() {
            return ratingCount;
        }

        public double getRatingSum() {
            return ratingSum;
        }

        public void addRating(int rating) {
            ratingCount++;
            ratingSum += rating;
        }

        public double getAverageRating() {
            if (ratingCount == 0) {
                return 0.0;
            }
            return ratingSum / ratingCount;
        }

    }

    private record User(String email, String password) {
    }

}