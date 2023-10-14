package twosCP2;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.Test;

import testHelp.*;

public class GameStateTest
{
    private ArrayList<Integer> convertToList(int[][] array)
    {
        ArrayList<Integer> myList = new ArrayList<Integer>();
        for (int r = 0; r < array.length; r++)
            for (int c = 0; c < array[0].length; c++)
                myList.add(array[r][c]);
        return myList;
    }

    private ArrayList<Integer> valuesToList(GameState g)
    {
        ArrayList<Integer> myList = new ArrayList<Integer>();
        for (int r = 0; r < 4; r++)
            for (int c = 0; c < 4; c++)
                myList.add(g.getValue(r, c));
        return myList;
    }

    // accounts for the possibility of spawning 4s vs just 2s
    private void twoify(ArrayList<Integer> theList)
    {
        for (int i = 0; i < theList.size(); i++)
        {
            if (theList.get(i) == 4)
                theList.set(i, 2);
        }
    }

    private int countNonZeroTiles(GameState g)
    {
        ArrayList<Integer> board = valuesToList(g);
        int count = 0;
        for (Integer e : board)
        {
            if (e != 0)
                count++;
        }
        return count;
    }

    @Test
    public void getValueShouldReturnCorrectValue()
    {
        int[][] values = { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 10, 11, 12 },
                { 13, 14, 15, 16 } };
        GameState g = new GameState(values);

        verify.that(g.getValue(1, 2)).isEqualTo(7);
    }

    @Test
    public void startingStatusShouldBeNewGame()
    {
        GameState g = new GameState();
        g.newGame();

        verify.that(g.getStatus()).isEqualTo("New game started");
    }

    @Test
    public void startingScoreShouldBeZero()
    {
        GameState g = new GameState();
        g.newGame();

        verify.that(g.getScore()).isEqualTo(0);
    }

    @Test
    public void gameShouldStartWithTwoTwos()
    {
        int[][] expectedVals = { { 2, 2, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
                { 0, 0, 0, 0 } };
        ArrayList<Integer> expected = convertToList(expectedVals);
        Collections.sort(expected);
        GameState g = new GameState();
        g.newGame();
        ArrayList<Integer> retrieved = valuesToList(g);
        twoify(retrieved);
        Collections.sort(retrieved);

        verify.that(retrieved).isEqualTo(expected);
    }

    @Test
    public void firstShiftShouldResultInThreeTwos()
    {
        int[][] expectedVals = { { 2, 2, 2, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 0 },
                { 0, 0, 0, 0 } };
        ArrayList<Integer> expected = convertToList(expectedVals);
        Collections.sort(expected);

        int[][] values = { { 2, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 2, 0 },
                { 0, 0, 0, 0 } };
        GameState g = new GameState(values);
        g.shift(g.LEFT);
        ArrayList<Integer> retrieved = valuesToList(g);
        twoify(retrieved);
        Collections.sort(retrieved);

        verify.that(retrieved).isEqualTo(expected);
    }

    @Test
    public void smallTilesShouldMerge()
    {
        int[][] values = { { 4, 2, 8, 2 }, { 4, 4, 16, 2 }, { 8, 64, 16, 32 },
                { 8, 64, 0, 32 } };
        GameState g = new GameState(values);
        g.shift(g.UP);

        verify.that(g.getValue(0, 3)).isEqualTo(4);
        verify.that(g.getValue(0, 0)).isEqualTo(8);
        verify.that(g.getValue(1, 0)).isEqualTo(16);
        verify.that(g.getValue(1, 2)).isEqualTo(32);
        verify.that(g.getValue(1, 3)).isEqualTo(64);
        verify.that(g.getValue(2, 1)).isEqualTo(128);
    }

    @Test
    public void bigTilesShouldMerge()
    {
        int[][] values = { { 128, 128, 2, 4 }, { 16, 256, 256, 8 },
                { 4, 32, 512, 512 }, { 1024, 1024, 2, 2 } };
        GameState g = new GameState(values);
        g.shift(g.RIGHT);

        verify.that(g.getValue(0, 1)).isEqualTo(256);
        verify.that(g.getValue(1, 2)).isEqualTo(512);
        verify.that(g.getValue(2, 3)).isEqualTo(1024);
        verify.that(g.getValue(3, 2)).isEqualTo(2048);
    }

    @Test
    public void mergingShouldIncreaseScore()
    {
        int[][] values = { { 4, 2, 8, 2 }, { 0, 4, 16, 2 }, { 0, 0, 0, 8 },
                { 2, 0, 0, 0 } };
        GameState g = new GameState(values);
        g.shift(g.UP);

        verify.that(g.getScore()).isEqualTo(4);
    }

    @Test
    public void shiftingThatDoesntMergeOrMoveShouldNotAddTile()
    {
        int[][] values = { { 0, 0, 0, 0 }, { 0, 2, 8, 0 }, { 2, 16, 4, 0 },
                { 16, 8, 2, 4 } };
        ArrayList<Integer> expected = convertToList(values);
        GameState g = new GameState(values);
        g.shift(g.DOWN);
        ArrayList<Integer> retrieved = valuesToList(g);

        verify.that(retrieved).isEqualTo(expected);
    }

    @Test
    public void mergingWithMultipleSameValueTilesShouldBeCorrect()
    {
        int[][] values = { { 4, 4, 4, 0 }, { 0, 8, 8, 8 }, { 16, 0, 16, 16 },
                { 2, 2, 2, 2 } };
        GameState g = new GameState(values);
        g.shift(g.LEFT);

        verify.that(g.getValue(0, 0)).isEqualTo(8);
        verify.that(g.getValue(0, 1)).isEqualTo(4);
        verify.that(g.getValue(1, 0)).isEqualTo(16);
        verify.that(g.getValue(1, 1)).isEqualTo(8);
        verify.that(g.getValue(2, 0)).isEqualTo(32);
        verify.that(g.getValue(2, 1)).isEqualTo(16);
        verify.that(g.getValue(3, 0)).isEqualTo(4);
        verify.that(g.getValue(3, 1)).isEqualTo(4);
    }

    @Test
    public void generalTestForShiftingUp()
    {
        int[][] values = { { 0, 0, 0, 0 }, { 0, 0, 2, 2 }, { 0, 4, 4, 0 },
                { 8, 8, 2, 2 } };
        GameState g = new GameState(values);
        g.shift(g.UP);

        verify.that(g.getValue(0, 0)).isEqualTo(8);
        verify.that(g.getValue(0, 1)).isEqualTo(4);
        verify.that(g.getValue(0, 2)).isEqualTo(2);
        verify.that(g.getValue(0, 3)).isEqualTo(4);
        verify.that(g.getValue(1, 1)).isEqualTo(8);
        verify.that(g.getValue(1, 2)).isEqualTo(4);
        verify.that(g.getValue(2, 2)).isEqualTo(2);
        verify.that(countNonZeroTiles(g)).isEqualTo(8);
        verify.that(g.getScore()).isEqualTo(4);
    }

    @Test
    public void generalTestForShiftingDown()
    {
        int[][] values = { { 0, 0, 0, 0 }, { 0, 0, 2, 2 }, { 0, 4, 4, 0 },
                { 8, 8, 2, 2 } };
        GameState g = new GameState(values);
        g.shift(g.DOWN);

        verify.that(g.getValue(1, 2)).isEqualTo(2);
        verify.that(g.getValue(2, 1)).isEqualTo(4);
        verify.that(g.getValue(2, 2)).isEqualTo(4);
        verify.that(g.getValue(3, 0)).isEqualTo(8);
        verify.that(g.getValue(3, 1)).isEqualTo(8);
        verify.that(g.getValue(3, 2)).isEqualTo(2);
        verify.that(g.getValue(3, 3)).isEqualTo(4);
        verify.that(countNonZeroTiles(g)).isEqualTo(8);
        verify.that(g.getScore()).isEqualTo(4);
    }

    @Test
    public void generalTestForShiftingLeft()
    {
        int[][] values = { { 0, 0, 0, 0 }, { 0, 0, 2, 2 }, { 0, 4, 4, 0 },
                { 8, 8, 2, 2 } };
        GameState g = new GameState(values);
        g.shift(g.LEFT);

        verify.that(g.getValue(1, 0)).isEqualTo(4);
        verify.that(g.getValue(2, 0)).isEqualTo(8);
        verify.that(g.getValue(3, 0)).isEqualTo(16);
        verify.that(g.getValue(3, 1)).isEqualTo(4);
        verify.that(countNonZeroTiles(g)).isEqualTo(5);
        verify.that(g.getScore()).isEqualTo(32);
    }

    @Test
    public void generalTestForShiftingRight()
    {
        int[][] values = { { 0, 0, 0, 0 }, { 0, 0, 2, 2 }, { 0, 4, 4, 0 },
                { 8, 8, 2, 2 } };
        GameState g = new GameState(values);
        g.shift(g.RIGHT);

        verify.that(g.getValue(1, 3)).isEqualTo(4);
        verify.that(g.getValue(2, 3)).isEqualTo(8);
        verify.that(g.getValue(3, 2)).isEqualTo(16);
        verify.that(g.getValue(3, 3)).isEqualTo(4);
        verify.that(countNonZeroTiles(g)).isEqualTo(5);
        verify.that(g.getScore()).isEqualTo(32);
    }

    @Test
    public void endOfGameShouldBeDetected()
    {
        int[][] values = { { 2, 4, 8, 16 }, { 16, 8, 4, 2 }, { 32, 4, 8, 16 },
                { 16, 64, 2, 2 } };
        GameState g = new GameState(values);
        g.shift(g.RIGHT);

        verify.that(g.getStatus()).isEqualTo("Game over!");
    }

}