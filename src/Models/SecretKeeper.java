// SecretKeeper.java
package Models;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SecretKeeper extends Player {
    public String secretCode;
    private List<String> guesses;
    public int maxAttempts;
    public int attemptsLeft;
    private static final String VALID_GUESS_PATTERN = "[0-7]{4}";

    public SecretKeeper() {
        super("Secret Keeper");
    
        this.secretCode = generateRandomSecret();
        this.guesses = new ArrayList<>();
        this.attemptsLeft = Game.getMaxAttempts();
    }

    private String generateRandomSecret() {
        String apiSecret = fetchSecretFromAPI();
        if (apiSecret != null && apiSecret.matches(VALID_GUESS_PATTERN)) {
            System.out.println("Secret from API: " + apiSecret);
            return apiSecret;
        } else {
            String localSecret = generateLocalSecret();
            System.out.println("Local Secret: " + localSecret);
            return localSecret;
        }
    }

    private String fetchSecretFromAPI() {
        try {
            URL url = new URL("https://www.random.org/integers/?num=4&min=0&max=7&col=1&base=10&format=plain&rnd=new");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            return response.toString().trim().replace("\n", ""); // remove new lines
        } catch (IOException e) {
            System.out.println("Failed to get API secret: " + e.getMessage());
            return null; // Return null to indicate failure
        }
    }

    // Generate backup secret locally
    private String generateLocalSecret() {
        Random random = new Random();
        StringBuilder localSecret = new StringBuilder(4);
        for (int i = 0; i < 4; i++) {
            localSecret.append(random.nextInt(7)); // Generate digits 0-7
        }
        return localSecret.toString();
    }

    public String getSecretCode() {
        return secretCode;
    } 

    public boolean hasAttemptsLeft() {
        return attemptsLeft > 0;
    }

    // Method to check if guess is four nums 0 - 8
    public boolean isValidGuess(String guess) {
        // 4 digits 0 - 7
        return guess != null && guess.matches(VALID_GUESS_PATTERN);
    }

    public void evaluateGuess(String guess) {
        if (!isValidGuess(guess)) {
            throw new IllegalArgumentException("Invalid guess");
        }
        guesses.add(guess); // Add guess to list of guesses
        attemptsLeft--;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }
    
    public String provideFeedback(String guess) {

        if (!isValidGuess(guess)) {
            return "Invalid guess; enter 4 digits, 0 - 7.";
        }
        
        int wellPlaced = 0;
        int misPlaced = 0;
        Map<Character, Integer> secretCount = new HashMap<>();
        Map<Character, Integer> guessCount = new HashMap<>();

        // Count well placed; populate hash
        for (int i = 0; i < 4; i++) {
            char secretChar = secretCode.charAt(i);
            char guessChar = guess.charAt(i);

            if (secretChar == guessChar) {
                wellPlaced++;
            }
            else {
                secretCount.put(secretChar, secretCount.getOrDefault(secretChar, 0) + 1);
                guessCount.put(guessChar, guessCount.getOrDefault(guessChar, 0) + 1);
            }
        }

        // Count mis-placed
        for (char c : guessCount.keySet()) {
            if (secretCount.containsKey(c)) {
                misPlaced += Math.min(secretCount.get(c), guessCount.get(c));
            }
        }
        return String.format("Well placed pieces: %d\nMisplaced pieces: %d", wellPlaced, misPlaced);
    }
}

