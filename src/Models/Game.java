// Game.java
package Models;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.SQLException;
import DAO.GameDataDAO;
import View.GameUI;

public class Game {
    // Fields
    private Guesser guesser;
    private SecretKeeper secretKeeper;
    // private GameData gameData;
    private GameDataDAO gameDataDAO;
    private GameUI gameUI;
    private int attemptsLeft;
    public static final int MAX_ATTEMPTS = 5;

    private int gameID; // move
    private String playerName;// move
    private int roundsToSolve;// move
    private boolean solved;// move
    private String formattedDate;// move
    private String secretCode;// move
    private List<String> guesses;// move

    // class constructor
    public Game (Guesser guesser, SecretKeeper secretKeeper, GameData gameData, GameDataDAO gameDataDAO) {
        this.guesser = guesser;
        this.secretKeeper = secretKeeper;
        // this.gameData = gameData; 
        this.gameDataDAO = gameDataDAO;      
        this.gameUI = new GameUI();
        this.attemptsLeft = MAX_ATTEMPTS;
        this.guesses = new ArrayList<>();
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

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
        // logger.debug("93secretCode: {}", secretCode);
    }

    public List<String> getGuesses() {
        return guesses;
    }

    public void setGuesses(List<String> guesses) {
        this.guesses = guesses;
        // logger.debug("102guesses: {}", guesses);
    }

    // Methods
    public static int getMaxAttempts() {
        return MAX_ATTEMPTS;
    }

    public void startGame() {
        // Use gameUI for UI interactions
        gameUI.displayMessage("Will you find the secret code?\nGood luck!");

        while (secretKeeper.hasAttemptsLeft()) {
            gameUI.displayMessage("---"); 
            gameUI.displayMessage("Round " + (MAX_ATTEMPTS - secretKeeper.getAttemptsLeft()));  
            gameUI.displayMessage("Rounds left: " + secretKeeper.getAttemptsLeft());     
            
            // String guess = gameUI.getInput("Enter guess: ");
            String guess = guesser.makeGuess();
            gameUI.displayMessage("YourGuess: " + guess);

            
            if (secretKeeper.isValidGuess(guess)) {
                secretKeeper.evaluateGuess(guess);
                String feedback = secretKeeper.provideFeedback(guess);
                gameUI.displayMessage(feedback);

                if (guess.equals(secretKeeper.getSecretCode())) {
                    gameUI.displayMessage("Congrats! You did it!");
                    // gameData.setSolved(true);
                    solved = true;
                    break;
                }
            } else {
                gameUI.displayMessage("Wrong Input!");
            }
            guesses.add(guess); // Added
        } 

        // Womp womp womp . . . you lose
        if (!solved) {
            gameUI.displayMessage("Sorry, too many tries. The code was: " + secretKeeper.getSecretCode());
            // gameData.setSolved(false);
        }
        finalizeGameData();
    }

    private void finalizeGameData() {
        this.playerName = guesser.getPlayerName();
        this.roundsToSolve = MAX_ATTEMPTS - secretKeeper.getAttemptsLeft();
        this.formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.secretCode = secretKeeper.getSecretCode();
        // gameData.setPlayerName(guesser.getPlayerName());
        // gameData.setGuesses(guesser.getGuesses());
        // gameData.setRoundsToSolve(MAX_ATTEMPTS - secretKeeper.getAttemptsLeft());
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // String formattedDate = formatter.format(new Date());
        // gameData.setFormattedDate(formattedDate);
        // gameData.setSecretCode(secretKeeper.getSecretCode());
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
