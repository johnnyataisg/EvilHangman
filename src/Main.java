import hangman.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        EvilHangmanGame game = new EvilHangmanGame(Integer.parseInt(args[2]));
        game.startGame(new File(args[0]), Integer.parseInt(args[1]));

        Scanner input = new Scanner(System.in);
        while(1 > 0)
        {
            if (game.getPattern().indexOf("-") < 0)
            {
                System.out.println("You Win!");
                System.out.println(game.getPattern());
                break;
            }
            if (game.getRemainingGuesses() == 0)
            {
                List<String> newWordList = new ArrayList<String>(game.getWordList());
                Random rand = new Random();
                int randomInt = rand.nextInt(game.getWordList().size());
                System.out.println("You lose!");
                System.out.println("The word was: " + newWordList.get(randomInt));
                System.out.println();
                break;
            }
            game.printDialog();
            String userInput = input.nextLine();
            if (userInput.length() == 1 && Character.isLetter(userInput.charAt(0)))
            {
                userInput = userInput.toLowerCase();
                char guess = userInput.charAt(0);
                try
                {
                    game.setWordList(game.makeGuess(guess));
                }
                catch (IEvilHangmanGame.GuessAlreadyMadeException repeatGuess)
                {
                    System.out.println("You already used that letter");
                    System.out.println();
                    continue;
                }
                game.printResult(guess);
            }
            else
            {
                System.out.println("Invalid input");
                System.out.println();
            }
        }
    }
}
