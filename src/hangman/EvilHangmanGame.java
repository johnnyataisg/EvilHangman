package hangman;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class EvilHangmanGame implements IEvilHangmanGame
{
    private Set<String> wordList = new HashSet<String>();
    private int wordLength;
    private int remainingGuesses;

    public int getLength()
    {
        return this.wordLength;
    }

    public int getGuesses()
    {
        return this.remainingGuesses;
    }

    public Set<String> getList()
    {
        return this.wordList;
    }

    public EvilHangmanGame(int guesses)
    {
        try
        {
            if (guesses < 1)
            {
                throw new Exception();
            }
            this.remainingGuesses = guesses;
        }
        catch (Exception e)
        {
            System.out.println("Invalid guess amount");
        }
    }

    public static class GuessAlreadyMadeException
    {
        public GuessAlreadyMadeException()
        {
            super();
        }
    }

    public void startGame(File dictionary, int wordLength)
    {
        try
        {
            Scanner input = new Scanner(dictionary);
            if (wordLength < 2)
            {
                throw new Exception();
            }
            this.wordLength = wordLength;
            while (input.hasNext())
            {
                this.wordList.add(input.next());
            }
            input.close();
        }
        catch (IOException e)
        {
            System.out.println("File not found");
        }
        catch (Exception f)
        {
            System.out.println("Invalid word length");
        }
    }

    public Set<String> makeGuess(char guess) throws IEvilHangmanGame.GuessAlreadyMadeException
    {
        Set<String> temp = new HashSet<String>();
        return temp;
    }
}
