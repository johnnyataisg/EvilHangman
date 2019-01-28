package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.*;

public class EvilHangmanGame implements IEvilHangmanGame
{
    private String chosenWord;
    private Set<String> wordList = new HashSet<>();
    private int wordLength;
    private int remainingGuesses;
    private SortedSet<Character> guessedList = new TreeSet<>();

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

    public static class GuessAlreadyMadeException extends Exception
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
            Scanner dictionaryInput = new Scanner(dictionary);
            if (wordLength < 2)
            {
                throw new Exception();
            }
            this.wordLength = wordLength;
            while (dictionaryInput.hasNext())
            {
                String wordCandidate = dictionaryInput.next();
                if (wordCandidate.length() == wordLength)
                {
                    this.wordList.add(wordCandidate);
                }
            }
            dictionaryInput.close();
            Scanner input = new Scanner(System.in);
            while(this.remainingGuesses != 0)
            {
                System.out.println("You have " + this.remainingGuesses + " guesses left");
                System.out.print("Used letters: ");
                for (char character : this.guessedList)
                {
                    System.out.print(character + " ");
                }
                System.out.println();
                this.printWord();
                System.out.print("Enter a guess: ");
                String guess = input.next();
                if (guess.length() == 1 && Character.isLetter(guess.charAt(0)))
                {
                    Set<String> newSet = this.makeGuess(guess.charAt(0));
                }
                else
                {
                    System.out.println("Invalid input");
                }
            }
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

    public void printWord()
    {
        System.out.print("Word: ");
        for (int i = 0; i < this.chosenWord.length(); i++)
        {
            if (this.guessedList.contains(this.chosenWord.charAt(i)))
            {
                System.out.print(this.chosenWord.charAt(i));
            }
            else
            {
                System.out.print("-");
            }
        }
        System.out.println();
    }

    public Set<String> makeGuess(char guess) throws IEvilHangmanGame.GuessAlreadyMadeException
    {
        try
        {
            if (this.guessedList.contains(guess))
            {
                throw new GuessAlreadyMadeException();
            }
            if (this.chosenWord.indexOf(guess) < 0)
            {
                this.remainingGuesses--;
            }
            this.guessedList.add(guess);
            this.chooseWord(guess);
        }
        catch (GuessAlreadyMadeException e)
        {
            System.out.println("You already used that letter");
        }

        return new HashSet<>();
    }

    public void chooseWord(char guess)
    {
        Map<String, Set<String>> families = this.generateFamilies(guess);
        Set<String> chosenSet;
        for (Entry<String, Set<String>> pair : families.entrySet())
        {
            if (pair.getKey().indexOf(guess) < 0)
            {
                chosenSet = pair.getValue();
            }
        }
    }

    public Map<String, Set<String>> generateFamilies(char letter)
    {
        Map<String, Set<String>> familyMap = new HashMap<>();
        for (String str : this.wordList)
        {
            StringBuilder word = new StringBuilder(str);
            int length = word.length();
            if (word.toString().indexOf(letter) >= 0 && length == this.wordLength)
            {
                for (int i = 0; i < length; i++)
                {
                    if (word.charAt(i) != letter)
                    {
                        word.setCharAt(i, '-');
                    }
                }
                if (!familyMap.containsKey(word.toString()))
                {
                    familyMap.put(word.toString(), new TreeSet<>());
                }
                familyMap.get(word.toString()).add(str);
            }
        }
        return familyMap;
    }

    public void setChosenWord(String word)
    {
        this.chosenWord = word;
    }
}
