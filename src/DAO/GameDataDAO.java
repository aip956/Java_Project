// GameDataDAO.java
package DAO;
import Models.Game;
import java.sql.SQLException;
import java.util.List;


public interface GameDataDAO {
    // Data Access Object (DAO) interface; allows games to be pulled by playerName and solved
    void saveGameData(Game game) throws SQLException; // Save a game's data to db
    /*  Placehold for getGamesByPlayer
        Placehold for getGamesBySolved
        or any other retrieval of games from the database
    */
    // Retrieve the top games (leaderboard)
    List<Game> getTopGames(int limit) throws SQLException;

    // Retrieve games from a specific player
    List<Game> getGamesByPlayer(String playerName) throws SQLException;

    // Optional: Delete all game data (for cleanup)
    void deleteAllGames() throws SQLException;
}

