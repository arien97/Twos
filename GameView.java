/**********************************************************
	 * Assignment: Twos
	 *
	 * Author: Arien Amin
	 *
	 * Description: This program is the game view for the game 2048
	 *
	 * Academic Integrity: I pledge that this program represents my own work. I
	 * received help from no one in designing and debugging
	 * my program.
	 **********************************************************/
package twosCP2;
import java.awt.*;
import java.awt.event.*;
import java.sql.Blob;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;


public class GameView extends JFrame implements IChangeListener
{
	private JLabel status;
	private JLabel score;
	private GameState grid;
	private JLabel[] tiles;
	
	public GameView(GameState g)
	{
		// set the value for the instance variable grid
		// this next line registers the GameView with the GameState
		grid = g;
		grid.addListener(this);
		
		score = new JLabel("");
		score.setFont(new Font("Serif", Font.PLAIN, 14));
		score.setForeground(Color.WHITE);
		
		status = new JLabel("");
		status.setFont(new Font("Serif", Font.PLAIN, 14));
		status.setForeground(Color.WHITE);
		
		tiles = new JLabel[16];
		
		int col = 0;
		int row = 0;
		for(int index=0; index<16; index++)
		{
			tiles[index] = new JLabel(g.getValue(row, col) + "", SwingConstants.CENTER);
			tiles[index].setOpaque(true);
			tiles[index].setFont(new Font("Serif", Font.PLAIN, 20));
			col++;
			if(col > 3)
			{
				col = 0;
				row++;
			}
								
		}		
		
		// create a new ActionHandler object 
		ActionHandler a = new ActionHandler(grid);
		
		// set the title and size
		setTitle("Arien's 2048");
		setSize(500, 650);
		
		// set the layout to a BorderLayout
		setLayout(new BorderLayout());
		
		// build a top panel and add it to the NORTH of the BoardLayout
		add(buildTopPanel(a), BorderLayout.NORTH);
		
		// build a center panel and add it to the CENTER of the BoardLayout
		add(buildCenterPanel(a), BorderLayout.CENTER);
		
		// build a bottom panel and add it to the BOTTOM of the BoardLayout
		add(buildBottomPanel(), BorderLayout.SOUTH);
		
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public void display()
	{
		setVisible(true);
	}
	
	@Override
	public void redraw()
	{
		// set the text of these labels: status, score, all the tiles
		
		score.setText("Score: " + grid.getScore() + " ");
		status.setText(grid.getStatus());
	
		//a nested for loop to initialize all the tiles as the values array
		int index=0;
		for(int i=0; i<4; i++)
		{
			for(int j=0; j<4; j++)
			{
				tiles[index].setText(grid.getValue(i, j) + "");
				tiles[index].setBackground(new Color(255, 255, 255));
				tiles[index].setForeground(Color.WHITE);
				
				//if the value is zero, it's replaced by an empty string
				if(grid.getValue(i, j) == 0)
				{
					tiles[index].setText(" ");
				}
				
				//the following if statements are for the tile color
				if(grid.getValue(i, j) == 2)
				{
					tiles[index].setBackground(new Color(0, 0, 250));
				}
				if(grid.getValue(i, j) == 4)
				{
					tiles[index].setBackground(new Color(10, 200, 230));
				}
				if(grid.getValue(i, j) == 8)
				{
					tiles[index].setBackground(new Color(20, 150, 210));
				}
				if(grid.getValue(i, j) == 16)
				{
					tiles[index].setBackground(new Color(60, 40, 190));
				}
				if(grid.getValue(i, j) == 32)
				{
					tiles[index].setBackground(new Color(40, 90, 150));
				}
				if(grid.getValue(i, j) == 64)
				{
					tiles[index].setBackground(new Color(50, 50, 100));
				}
				if(grid.getValue(i, j) == 128)
				{
					tiles[index].setBackground(new Color(90, 10, 110));
				}
				if(grid.getValue(i, j) == 256)
				{
					tiles[index].setBackground(new Color(150, 0, 90));
				}
				if(grid.getValue(i, j) == 512)
				{
					tiles[index].setBackground(new Color(100, 0, 70));
				}
				if(grid.getValue(i, j) == 1024)
				{
					tiles[index].setBackground(new Color(90, 255, 90));
				}
				if(grid.getValue(i, j) == 2048)
				{
					tiles[index].setBackground(new Color(100, 200, 30));
				}
				
				index++;
			}
		}
		
		
	}

	private JPanel buildBottomPanel() 
	{
		// create a JPanel and use the instance variable status to add a label
		// to the bottom panel
		
		JPanel bottom = new JPanel();
		
		//adds to bottom panel
		bottom.add(status);
		
		//sets background color
		bottom.setBackground(new Color(150, 150, 250));
		status.setBackground(Color.WHITE);
		
		return bottom;
	}

	private JPanel buildTopPanel(ActionHandler handler) 
	{
		// create JPanel and add a JButton to it for "New game"
		// also use the instance variable score to add a label
		// the next line registers the button if it were called "button"  
		
		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		
		//initializes buttons and color
		JButton button = new JButton("New game");
		button.addActionListener(handler);
		button.setBackground(new Color(240, 200, 210));
		button.setFont(new Font("Serif", Font.PLAIN, 14));
		button.setForeground(Color.WHITE);
		button.setMargin(new Insets(5, 5, 5, 5));
		
		//adds button and score to top panel
		top.add(button, BorderLayout.WEST);
		top.add(score, BorderLayout.EAST);
		
		//sets background color
		top.setBackground(new Color(150, 150, 250));
		score.setBackground(Color.WHITE);
		
		return top;
	}
	
	private JPanel buildCenterPanel(ActionHandler handler)
	{
		// create a JPanel with a GridLayout
		// use the instance variable tiles to fill the grid with labels
		// the next line sets up the arrow keys using the method we gave you
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(4, 4));
		
		//creates a border around the game
		centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		centerPanel.setBackground(new Color(255, 250, 250));
		
		//adds tiles to the center panel
			for(int index=0; index<16; index++)
			{
				centerPanel.add(tiles[index]);
			}
		
		bindArrows(handler, centerPanel);
		
		return centerPanel;
	}

	// might need to give the students this method instead of making them 
	// learn about key bindings and anonymous inner classes
	private void bindArrows(ActionHandler handler, JPanel panel) 
	{
		String[] commands = {"left arrow", "up arrow", "right arrow", "down arrow"};
		for (int i = 0; i < 4; i++)
		{
			int copy = i;
			KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT + i, 0);
			Action action = new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					handler.handleArrowPress(GameState.LEFT + copy);
				}
			};
			panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(key, commands[i]);
			panel.getActionMap().put(commands[i], action);
		}
	}
}