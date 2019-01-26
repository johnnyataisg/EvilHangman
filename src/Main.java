import hangman.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
        EvilHangmanGame test = new EvilHangmanGame(Integer.parseInt(args[2]));

        test.startGame(new File(args[0]), Integer.parseInt(args[1]));
        System.out.println(test.getGuesses());
    }
}
