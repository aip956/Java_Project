// Models.Game.java
package Models;

import Utils.ValidationUtils;
import View.GameUI;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List; 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.SQLException;
import DAO.GameDataDAO;


public class Game {
    // Fields
    // private List<Guesser> players; // List of players if multiplayer
    // private int currentPlayerIndex; // Track the player
    // comment out the Guesser guesser
    private Guesser guesser;
    private GameUI gameUI;
    private String secretCode;
    private List<String> guesses;
    public int attemptsLeft;
    private boolean solved;
    private GameDataDAO gameDataDAO;
    

    public static final int MAX_ATTEMPTS = 5;

    private int gameID; 
    private String playerName;
    private int roundsToSolve;
    private String formattedDate;


    // class constructor; use List<Guesser> players for guesser
    public Game (Guesser guesser, String secretCode, GameDataDAO gameDataDAO) {
        // this.players = players;
        // this.currentPlayerIndex = 0; // Start with the first player
        this.guesser = guesser;
        this.secretCode = secretCode;
        this.gameUI = new GameUI();
        this.guesses = new ArrayList<>();
        this.attemptsLeft = MAX_ATTEMPTS;
        this.gameDataDAO = gameDataDAO;      
        this.solved = false;
    }
    
    // Constructor for leaderboard 
    public Game(String playerName, String secretCode, int roundsToSolve, boolean solved, String formattedDate) {
        this.playerName = playerName;
        this.secretCode = secretCode;
        this.roundsToSolve = roundsToSolve;
        this.solved = solved;
        this.formattedDate = formattedDate;
    }

    // Getters, setters; moved
    public int getGameID() {
        return gameID;
    }
    public void setGameID (int gameID) {
        this.gameID = gameID;
        // logger.debug("48GameIDD: {}", gameID);
    }

    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
        // logger.debug("57playerName: {}", playerName);
    }


    public int getRoundsToSolve() {
        return roundsToSolve;
    }
    public void setRoundsToSolve(int roundsToSolve) {
        this.roundsToSolve = roundsToSolve;
        // logger.debug("66roundsToSolve: {}", roundsToSolve);
    }

    public boolean isSolved() {
        return solved;
    }
    public void setSolved(boolean solved) {
        this.solved = solved;
        // logger.debug("75solved: {}", solved);
    }

    public String getFormattedDate() {
        return formattedDate;
    }
    public void setFormattedDate (String formattedDate) {
        this.formattedDate = formattedDate;
    }

    // Do I need this getter?
    public String getSecretCode() {
        return secretCode;
    }


    public List<String> getGuesses() {
        return guesses;
    }
    public void setGuesses(List<String> guesses) {
        this.guesses = guesses;
        // logger.debug("102guesses: {}", guesses);
    }

    public static int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }
    public int getAttemptsLeft() {
        return attemptsLeft;
    }
    public boolean hasAttemptsLeft() {
        return attemptsLeft > 0;
    }

    // Method to check if guess is four nums 0 - 8
    public boolean isValidGuess(String guess) {
        // 4 digits 0 - 7
        return guess != null && guess.matches(ValidationUtils.VALID_GUESS_PATTERN);
    }

    public void evaluateGuess(String guess) {
        if (!isValidGuess(guess)) {
            throw new IllegalArgumentException("Invalid guess");
        }
        guesses.add(guess); // Add guess to list of guesses
        attemptsLeft--;
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

    public void startGame() {
        // Use gameUI for UI interactions
        gameUI.displayMessage("Will you find the secret code?\nGood luck!");

        while (hasAttemptsLeft()) {
            // Guesser currentPlayer = players.get(currentPlayerIndex);
            gameUI.displayMessage("---"); 
            gameUI.displayMessage("Round " + (MAX_ATTEMPTS - attemptsLeft + 1));  
            gameUI.displayMessage("Rounds left: " + attemptsLeft);     
            
            String guess = guesser.makeGuess();
            gameUI.displayMessage("YourGuess: " + guess);
            
            if (isValidGuess(guess)) {
                evaluateGuess(guess);
                String feedback = provideFeedback(guess);
                gameUI.displayMessage(feedback);

                if (guess.equals(secretCode)) {
                    gameUI.displayMessage("Congrats! You did it!");
                    solved = true;
                    break;
                }
            } else {
                gameUI.displayMessage("Wrong Input!");
            }
            // Switch to other player
            // currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } 

        // Womp womp womp . . . you lose
        if (!solved) {
            gameUI.displayMessage("Sorry, too many tries. The code was: " + secretCode);
        }
        finalizeGameData();
    }

    private void finalizeGameData() {
        this.playerName = guesser.getPlayerName();
        this.roundsToSolve = MAX_ATTEMPTS - attemptsLeft;
        this.formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        saveGameDataToDatabase();
        gameUI.close();        
    }
    // Save data to database
    public void saveGameDataToDatabase() {
        try {
            gameDataDAO.saveGameData(this);
            gameUI.displayMessage("Game data saved");
        } catch (SQLException e) {
            gameUI.displayMessage("Error occured saving game data: " + e.getMessage());
        }
    }
}


