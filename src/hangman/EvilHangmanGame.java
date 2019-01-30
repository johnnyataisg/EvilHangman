package hangman;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.*;

public class EvilHangmanGame implements IEvilHangmanGame
{
    private Set<String> wordList = new HashSet<>();
    private StringBuilder pattern = new StringBuilder();
    private int wordLength;
    private int initialGuesses;
    private int remainingGuesses;
    private SortedSet<Character> guessedList = new TreeSet<>();

    public EvilHangmanGame()
    {
        this.wordLength = 0;
        this.remainingGuesses = 0;
    }

    public EvilHangmanGame(int guesses)
    {
        try
        {
            if (guesses < 1)
            {
                throw new Exception();
            }
            this.initialGuesses = guesses;
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
        this.wordList.clear();
        this.guessedList.clear();
        this.pattern = new StringBuilder();
        this.wordLength = 0;
        try
        {
            Scanner dictionaryInput = new Scanner(dictionary);
            this.setGameData(dictionaryInput, wordLength);
        }
        catch (IOException invalidFile)
        {
            System.out.println("File not found");
        }
        catch (IllegalArgumentException invalidArgument)
        {
            System.out.println("Invalid word length");
        }
    }

    public void setGameData(Scanner scanner, int length)
    {
        if (length < 2)
        {
            throw new IllegalArgumentException();
        }
        this.remainingGuesses = this.initialGuesses;
        this.wordLength = length;
        while (scanner.hasNext())
        {
            String wordCandidate = scanner.next().toLowerCase();
            if (wordCandidate.length() == this.wordLength)
            {
                this.wordList.add(wordCandidate);
            }
        }
        scanner.close();
        for (int i = 0; i < this.wordLength; i++)
        {
            this.pattern.append("-");
        }
    }

    public void printDialog()
    {
        System.out.println("You have " + this.remainingGuesses + " guesses left");
        System.out.print("Used letters: ");
        for (char character : this.guessedList)
        {
            System.out.print(character + " ");
        }
        System.out.println();
        System.out.println("Word: " + this.pattern);
        System.out.print("Enter a guess: ");
    }

    public void printResult(char guess)
    {
        if (this.pattern.indexOf(guess + "") >= 0)
        {
            int count = this.pattern.length() - this.pattern.toString().replace(guess + "", "").length();
            System.out.println("Yes, there is " + count + " " + guess);
        }
        else
        {
            this.remainingGuesses--;
            System.out.println("Sorry, there are no " + guess + "\'s");
        }
        System.out.println();
    }

    public Set<String> makeGuess(char guess) throws IEvilHangmanGame.GuessAlreadyMadeException
    {
        Set<String> output = new HashSet<>();
        if (this.guessedList.contains(guess))
        {
            throw new IEvilHangmanGame.GuessAlreadyMadeException();
        }
        this.guessedList.add(guess);
        this.wordList = this.chooseWord(guess);
        return this.wordList;
    }

    public Set<String> chooseWord(char guess)
    {
        Set<String> output = new HashSet<>();
        Map<String, Set<String>> familyCandidates = this.generateFamilies(guess);
        familyCandidates = this.findLargestFamilies(familyCandidates);
        if (familyCandidates.size() != 1)
        {
            familyCandidates = this.findEmptyKey(familyCandidates, guess);
        }
        if (familyCandidates.size() != 1)
        {
            familyCandidates = this.findLowestFrequency(familyCandidates);
        }
        if (familyCandidates.size() != 1)
        {
            familyCandidates = this.findRightMost(familyCandidates);
        }
        for (Entry<String, Set<String>> pair : familyCandidates.entrySet())
        {
            for (int i = 0; i < pair.getKey().length(); i++)
            {
                if (pair.getKey().charAt(i) == guess)
                {
                    this.pattern.setCharAt(i, guess);
                }
            }
            output = pair.getValue();
        }
        return output;
    }

    public Map<String, Set<String>> generateFamilies(char letter)
    {
        Map<String, Set<String>> familyMap = new HashMap<>();
        for (String str : this.wordList)
        {
            StringBuilder word = new StringBuilder(str);
            int length = word.length();
            if (length == this.wordLength)
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

    public Map<String, Set<String>> findLargestFamilies(Map<String, Set<String>> familyMap)
    {
        Map<String, Set<String>> familyList = new HashMap<>();
        int largest = 0;
        for (Entry<String, Set<String>> pair : familyMap.entrySet())
        {
            int size = pair.getValue().size();
            if (size > largest)
            {
                familyList.clear();
                familyList.put(pair.getKey(), pair.getValue());
                largest = size;
            }
            else if (size == largest)
            {
                familyList.put(pair.getKey(), pair.getValue());
            }
        }
        return familyList;
    }

    public Map<String, Set<String>> findEmptyKey(Map<String, Set<String>> familyMap, char letter)
    {
        Map<String, Set<String>> familyList = new HashMap<>();
        for (Entry<String, Set<String>> pair : familyMap.entrySet())
        {
            if (pair.getKey().indexOf(letter) < 0)
            {
                familyList.put(pair.getKey(), pair.getValue());
            }
        }
        if (familyList.size() == 0)
        {
            return familyMap;
        }
        return familyList;
    }

    public Map<String, Set<String>> findLowestFrequency(Map<String, Set<String>> familyMap)
    {
        Map<String, Set<String>> familyList = new HashMap<>();
        int minimum = 1000000;
        for (Entry<String, Set<String>> pair : familyMap.entrySet())
        {
            int frequency = this.count(pair.getKey());
            if (frequency < minimum)
            {
                familyList.clear();
                familyList.put(pair.getKey(), pair.getValue());
                minimum = frequency;
            }
            else if (frequency == minimum)
            {
                familyList.put(pair.getKey(), pair.getValue());
            }
        }
        if (familyList.size() == 0)
        {
            return familyMap;
        }
        return familyList;
    }

    public Map<String, Set<String>> findRightMost(Map<String, Set<String>> familyMap)
    {
        Map<String, Set<String>> temp = new HashMap<>(familyMap);
        Map<String, Set<String>> familyList = new HashMap<>();
        for (int i = this.wordLength - 1; i >= 0; i--)
        {
            for (Entry<String, Set<String>> pair : temp.entrySet())
            {
                if (pair.getKey().charAt(i) != '-')
                {
                    familyList.put(pair.getKey(), pair.getValue());
                }
            }
            if (familyList.size() == 1)
            {
                break;
            }
            else
            {
                if (familyList.size() == 0)
                {
                    temp = familyMap;
                }
                else
                {
                    temp = new HashMap<>(familyList);
                }
                familyList.clear();
            }
        }
        if (familyList.size() == 0)
        {
            return familyMap;
        }
        return familyList;
    }

    public int count(String key)
    {
        int result = 0;
        for (int i = 0; i < key.length(); i++)
        {
            if (key.charAt(i) != '-')
            {
                result++;
            }
        }
        return result;
    }

    public int getInitialGuesses()
    {
	return this.initialGuesses;
    }

    public int getRemainingGuesses()
    {
        return this.remainingGuesses;
    }

    public StringBuilder getPattern()
    {
        return this.pattern;
    }

    public Set<String> getWordList()
    {
        return this.wordList;
    }

    public void setWordList(Set<String> set)
    {
        this.wordList = set;
    }
}
