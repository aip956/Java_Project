// MyMastermind.java

import Models.Guesser;
import Models.SecretKeeper;
import Models.Game;
import View.GameUI;
import DAO.GameDataDAO;
import DAO.SQLiteGameDataDAO;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyMastermind {
    // private static final Logger logger = LoggerFactory.getLogger(MyMastermind.class);
    public static void main(String[] args) {
        // db path to save
        String dbPath = System.getenv("DB_FILE");
        dbPath = (dbPath == null || dbPath.isEmpty()) ? "src/data/MM_Reach.db" : dbPath;
        System.out.println("Attempting to connect to the database...");
        GameDataDAO gameDataDAO; // Declaring outside try block

        try {
            gameDataDAO = new SQLiteGameDataDAO(dbPath);
            System.out.println("DB connect success");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return;
        }
        // Scanner scanner = new Scanner(System.in);
        // System.out.print("Enter number of players: ");
        // int numPlayers = Integer.parseInt(scanner.nextLine());

        // List<Guesser> players = new ArrayList<>();
        // for (int i = 0; i < numPlayers; i++);
        // System.out.print("Enter name for Player " + i ": ");
        // String playerName = scanner.nextLine();
        // players.add(new Guesser(playerName));


        // Flag check
        Boolean debugMode = false;
        for (String arg : args) {
            if ("-d".equals(arg)) {
                debugMode = true;
            }
        }
        
        System.out.println("debugger: " + debugMode);
        Guesser guesser = new Guesser("Player1");
        SecretKeeper secretKeeper = new SecretKeeper();
        // GameData gameData = new GameData();
         String secretCode = secretKeeper.getSecretCode();
        Game game = new Game(guesser, secretCode, gameDataDAO);
        // Start game
        game.startGame();
    }
}

