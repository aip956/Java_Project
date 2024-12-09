// Models.Guesser.java
package Models;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Guesser extends Player {
    private Scanner scanner;

    public Guesser(String name) {
        super("");// Call the Player constructor
        this.scanner = new Scanner(System.in);
        // this.guesses = new ArrayList<>();
        System.out.print("Please enter your name: ");
        this.playerName = scanner.nextLine();
    }

    @Override
    public String makeGuess() {
        System.out.print("Enter guess: ");
        return scanner.nextLine();
    }
}

