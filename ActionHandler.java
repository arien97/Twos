package twosCP2;
import java.awt.event.*;

/* will implement key listener, action listener, etc. and route
 * user actions to grid
 */
public class ActionHandler implements ActionListener
{
	private GameState grid;
	
	public ActionHandler(GameState g)
	{
		this.grid = g;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand().equalsIgnoreCase("new game"))
			grid.newGame();
	}

	public void handleArrowPress(int direction)
	{
		grid.shift(direction);
	}
}