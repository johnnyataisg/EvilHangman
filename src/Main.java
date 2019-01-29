import hangman.*;

import java.io.File;

public class Main
{
    public static void main(String[] args)
    {
        EvilHangmanGame game = new EvilHangmanGame(Integer.parseInt(args[2]));
        game.startGame(new File(args[0]), Integer.parseInt(args[1]));
    }
}
