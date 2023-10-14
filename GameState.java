/**********************************************************
	 * Assignment: Twos
	 *
	 * Author: Arien Amin
	 *
	 * Description: This program is the state of the game 2048
	 *
	 * Academic Integrity: I pledge that this program represents my own work. I
	 * received help from no one in designing and debugging
	 * my program.
	 **********************************************************/
package twosCP2;

import java.util.ArrayList;
import java.util.Arrays;

/* Represents the game state, including status, score, and the 4x4 grid 
 * of values. Knows how to shift the values around. Notifies all 
 * registered IChangeListeners if anything happens. 
 */
public class GameState
{
	private int[][] values;
	private String status;
	private int score;

	public final static int LEFT = 0;
	public final static int UP = 1;
	public final static int RIGHT = 2;
	public final static int DOWN = 3;

	private ArrayList<IChangeListener> listeners;

	public GameState()
	{
		// initialize listeners, status, and score

		status = "";
		score = 0;
		values = new int[4][4];
		listeners = new ArrayList<IChangeListener>();
	}

	public GameState(int[][] values2)
	{
		// initialize listeners, status, and score

		status = "";
		score = 0;
		values = values2;
		listeners = new ArrayList<IChangeListener>();
	}

	public String getStatus()
	{
		return status;
	}

	public int getScore()
	{
		return score;
	}

	public void addListener(IChangeListener listener)
	{
		// add the listener to the arrayList of listeners
		listeners.add(listener);
	}

	public void newGame()
	{
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				values[i][j] = 0;
			}
		}

		// the variables are to remember each random number
		int col1 = (int) (Math.random() * 4);
		int col2 = (int) (Math.random() * 4);
		int row1 = (int) (Math.random() * 4);
		int row2 = (int) (Math.random() * 4);

		values[row1][col1] = drawRandomNum();

		if (row1 == row2 && col1 == col2)
		{
			col2 = (int) (Math.random() * 4);
		}

		values[row2][col2] = drawRandomNum();

		status = "New game started";
		updateListeners();
	}

	// this method returns 2 when drawing a random number
	// but at least 10% of the time should return a 4
	public int drawRandomNum()
	{
		if ((int) (Math.random() * 10) == 5)
		{
			return 4;
		}
		return 2;
	}

	public void shift(int direction)
	{	
		if (direction == LEFT)
		{
			status = "Shifted tiles left";
			moveTiles(LEFT);
		}
		if (direction == UP)
		{
			status = "Shifted tiles up";
			moveTiles(UP);
		}
		if (direction == RIGHT)
		{
			status = "Shifted tiles right";
			moveTiles(RIGHT);
		}
		if (direction == DOWN)
		{
			status = "Shifted tiles down";
			moveTiles(DOWN);
		}

		updateListeners();
	}

	// this is a boolean method to find out if the 4x4 is full
	public boolean isBoardFull()
	{
		// this traverses the 2D array
		int counter = 0;
		for (int i = 0; i < values.length; i++)
		{
			for (int j = 0; j < values[0].length; j++)
			{
				if (values[i][j] != 0)
				{
					counter++;
				}
			}
		}
		if (counter == 16)
		{
			return true;
		}

		return false;
	}

	// this method will spawn a new tile in an empty spot
	// until it finds a spot that is empty to fill
	public void spawn()
	{
		int col1 = (int) (Math.random() * 4);
		int row1 = (int) (Math.random() * 4);

		while (values[row1][col1] != 0)
		{
			col1 = (int) (Math.random() * 4);
			row1 = (int) (Math.random() * 4);
		}

		values[row1][col1] = drawRandomNum();

	}

	// this method shifts the tiles and merges tiles in values when possible.
	// It also checks for the highest score, and if new tiles need to be spawned
	public void moveTiles(int direction)
	{
		ArrayList<Integer> nums = new ArrayList<Integer>();

		int[][] copy = copyValues();
		
		//checks to see if the game is already over and no moves can be made
		if(isTheGameOver(copy))
		{
			status = "Game over!";
		}

		// the for loop goes through each row or column to merge and shift
		// them separately before putting it back into the array
		for (int i = 0; i < 4; i++)
		{
			nums = toList(i, direction);
			removeZeros(nums);
			merge(nums, direction);
			padTo4(nums, direction);
			putIntoCopy(nums, i, copy, direction);
		}

		// this is to check if the game board is changing or if it's staying
		// the same
		if (!equalToValues(copy))
		{
			for (int i = 0; i < values.length; i++)
			{
				for (int j = 0; j < values[0].length; j++)
				{					
					values[i][j] = copy[i][j];
					
					if (values[i][j] == 2048)
					{
						status = "You win";
					}
				}
			}

			// this is to see if the board still has empty places to add
			// a new tile or if anything can be moved, otherwise it won't 
			// spawn anything new
			if(!isBoardFull())
			{
				spawn();
			}
			if (isTheGameOver(copy))
			{
				status = "Game over!";
			}
		}
		updateListeners();
	}

	// this method checks to see if any moves are still able to be made
	// before determining if the game is over
	private boolean isTheGameOver(int[][] copy)
	{
		int counter = 0;
		if (isBoardFull())
		{
			counter++;
		}
		if (equalToValues(copy))
		{
			counter++;
		}

		for (int i = 0; i < copy.length - 1; i++)
		{
			for (int j = 0; j < copy.length - 1; j++)
			{
				// if the numbers are equal, then the player can still make a move
				// so this is to check whether or not there are two values next to
				// each other that are equal, meaning they can be moved, or if there
				// is a zero, meaning a shift can happen still
				if (values[i][j] == (values[i][j + 1]) && values[i][j] == (values[i + 1][j]) && values[i][j] != 0)
				{
					counter++;
				}

			}
		}

		// will only return 2 if the first two if-statements apply
		// and there are no same numbers next to each other or zeros
		// left in the 2D array
		return counter == 2;
	}

	// this method merges numbers together depending on the direction
	private void merge(ArrayList<Integer> nums, int direction)
	{
		int total = 0;
		if (direction == LEFT || direction == UP)
		{
			for (int i = 0; i < nums.size() - 1; i++)
			{
				if (nums.get(i).equals(nums.get(i + 1)))
				{
					total += (int)(nums.get(i) * 2);
					nums.set(i, nums.get(i) * 2);
					nums.remove(i + 1);
				}
			}
		}

		if (direction == RIGHT || direction == DOWN)
		{
			int start = nums.size() - 1;
			for (int i = start; i > 0; i--)
			{
				if (nums.get(i).equals(nums.get(i - 1)))
				{
					total += (int)(nums.get(i) * 2);
					nums.set(i, nums.get(i) * 2);
					nums.remove(i - 1);
					
				}
			}
		}
		
		score += total;
	}

	private ArrayList<Integer> toList(int number, int direction)
	{
		// depending on the direction, int number can be an index for a row
		// or an index for a column

		ArrayList<Integer> nums = new ArrayList<Integer>();

		// directions LEFT and RIGHT affect the rows
		if (direction == LEFT || direction == RIGHT)
		{
			for (int col = 0; col < 4; col++)
			{
				nums.add(values[number][col]);
			}
		}

		// direction DOWN and UP affect the columns
		if (direction == DOWN || direction == UP)
		{
			for (int row = 0; row < 4; row++)
			{
				nums.add(values[row][number]);
			}
		}
		return nums;
	}

	// this method gets rid of the zeros in a single row
	private void removeZeros(ArrayList<Integer> nums)
	{
		int[] newNums = new int[nums.size()];

		for (int i = 0; i < nums.size(); i++)
		{
			for (int j = 0; j < nums.size(); j++)
			{
				newNums[j] = nums.get(j);
				if (newNums[j] == 0)
				{
					nums.remove(j);
					newNums = new int[nums.size()];
				}
			}
		}
	}

	// this method makes a row a length of 4 by adding 0s
	private void padTo4(ArrayList<Integer> nums, int direction)
	{

		if (direction == LEFT || direction == UP)
		{
			while(nums.size() != 4)
			{
				//adds 0 to the end of a list
				nums.add(0);
			}
		}

		if (direction == RIGHT || direction == DOWN)
		{
			while (nums.size() != 4)
			{
				// adds 0 to the beginning of the list
				nums.add(0, 0);
			}
		}

	}

	// this method initializes the copy array so it can be compared to values
	private void putIntoCopy(ArrayList<Integer> nums, int num, int[][] copy, int direction)
	{
		if (direction == LEFT || direction == RIGHT)
		{
			for (int col = 0; col < 4; col++)
			{
				copy[num][col] = nums.get(col);
			}
		}
		if (direction == UP || direction == DOWN)
		{
			for (int row = 0; row < 4; row++)
			{
				copy[row][num] = nums.get(row);
			}
		}
	}

	// this method makes a copy of the Values array and returns a copy of it
	private int[][] copyValues()
	{
		int[][] copy = new int[values.length][values[0].length];
		for (int i = 0; i < values.length; i++)
		{
			for (int j = 0; j < values[0].length; j++)
			{
				copy[i][j] = values[i][j];
			}
		}
		return copy;
	}

	// this method compares if the copy array is the same as the values array
	// by comparing each value
	private boolean equalToValues(int[][] other)
	{
		int size = values.length * values[0].length;
		int counter = 0;
		for (int i = 0; i < values.length; i++)
		{
			for (int j = 0; j < values[0].length; j++)
			{
				if (other[i][j] == values[i][j])
				{
					counter++;
				}
			}
		}

		// will return true if all the contents match up
		return counter == size;
	}

	public int getValue(int r, int c)
	{
		// return the appropriate value
		return values[r][c];
	}

	private void updateListeners()
	{
		// for each item in the listeners list calls its redraw method

		for (IChangeListener i : listeners)
		{
			i.redraw();
		}
	}
}