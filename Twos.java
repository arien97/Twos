package twosCP2;
public class Twos 
{
	public static void main(String[] args)
	{
		GameState grid = new GameState();
		GameView game = new GameView(grid);
		
		grid.newGame();
		game.display();
	}
}