package frames;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class GameFrame extends JFrame{
	private MineField mineField;
	private GameTimer gameTimer;

	private int rows;
	private int cols;

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu gameMenu;
	private JMenu leaderBoard;
	
	private JPanel mineSweeperPanel;
	private GameBoardPanel gamePanel;
	private JPanel leaderBoardPanel;
	
	private JPanel statusBar;
	private JLabel minesLabel;
	private JLabel timerLabel;
	private JButton flagButton;
	
	private JTable leaderBoardTable;

	private Image[] numImages;
	private Image flagImage;
	private Image blankImage;
//////////////////////////////////////////////////////
//initializator block for improved performance
	{
		numImages = new Image[10];
		int idx;
		for(int i =-1; i<9; i++){
			ImageIcon icon = new ImageIcon("./image/"+i+".png"); // replace with the actual path to your image
			Image img = icon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
			idx = i+1;
			numImages[idx] =img;
		}

		ImageIcon icon = new ImageIcon("./image/flag.png"); 
		flagImage = icon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
	
		ImageIcon icon2 = new ImageIcon("./image/blank.png");
		blankImage = icon2.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
	
		
	}
//////////////////////////////////////////////////////66	
	//STRART OF CTR
	public GameFrame(int r, int c) {
		//frame basic setup
		super("Minesweeper");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(400 ,430);
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		this.setLayout(new BorderLayout());
		
		rows = r;
		cols = c;
		
		//creating the minefield (the backend)
		mineField = new MineField(rows,cols);
		
		//END OF CTR
		/////////////////////////////////////////////////////////////////////////////////
		//Menu:
		menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(400,30));
	
		fileMenu = new JMenu("File");
		gameMenu = new JMenu("Game");
		leaderBoard = new JMenu("LeaderBoard");
		
		JMenuItem save= new JMenuItem("Save");
		JMenuItem load= new JMenuItem("Load");
		
		
		JMenuItem reset= new JMenuItem("Reset");
		JMenuItem solve= new JMenuItem("Solve");
		JMenuItem leaderBoardItem = new JMenuItem("LeaderBoard");
		JMenuItem game = new JMenuItem("MineSweeper");
		
		leaderBoardItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel(leaderBoardPanel);
            }
        });
		
		game.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel(mineSweeperPanel);
            }
        });

		//add:
		fileMenu.add(save);
		fileMenu.add(load);
		
		gameMenu.add(reset);
		gameMenu.add(solve);
		
		
		leaderBoard.add(leaderBoardItem);
		leaderBoard.add(game);
		
		menuBar.add(fileMenu);
		menuBar.add(gameMenu);
		menuBar.add(leaderBoard);
		this.setJMenuBar(menuBar);
		
		statusBar = new JPanel();
		statusBar.setLayout(new FlowLayout());
		
		minesLabel = new JLabel("Mines left:" + 40); //only creation 
		timerLabel = new JLabel("Time: " + 0); //only creation for timer
		
		//Flag button:
		flagButton = new JButton();
		flagButton.addActionListener(new FlagListener(this)); //action listener

		//initialization:
		flagButton.setPreferredSize(new Dimension(20, 20));
		ImageIcon icon = new ImageIcon("./image/flag.png");
        Image img = icon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
        flagButton.setIcon(new ImageIcon(img));
	////////////////////////////////////////////////////////////////////////////////////////////66
		
		//game panel:
        gamePanel = new GameBoardPanel(this);
		
		mineSweeperPanel = new JPanel();
		mineSweeperPanel.setLayout(new BorderLayout());
	
		statusBar.add(timerLabel);
		statusBar.add(minesLabel);
		statusBar.add(flagButton);
		mineSweeperPanel.add(statusBar, BorderLayout.NORTH);
		mineSweeperPanel.add(gamePanel,BorderLayout.CENTER); 
	////////////////////////////////////////////////////////////////
		
		//leaderBoardPanel:
		leaderBoardPanel = new JPanel();//this will contain a JTable for the leaderboard
		leaderBoardPanel.setLayout(new BorderLayout());
		JLabel titleLabel = new JLabel("LEADERBOARD");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		leaderBoardPanel.add(titleLabel,BorderLayout.NORTH);

		String[] columnNames = {"Name", "Time"};
		leaderBoardTable = new JTable(); //will use game data as constructor
		leaderBoardPanel.add(leaderBoardTable,BorderLayout.CENTER);
		
		
	//////////////////////////////////////////////////////////////
		this.add(mineSweeperPanel); //always begins with the game panel
		
		
	}
	///////////////////////////////////////////////////////////////////
	//END OF CTR
	
	//Helper functions:
	private void showPanel(JPanel panel) {
	        getContentPane().removeAll();
	        getContentPane().add(panel);
	        revalidate();
	        repaint();
	        }
	
	private void victoryScreen(JFrame jf){
		gameTimer.stop();
		String Name = JOptionPane.showInputDialog("You Won!!! Please enter your name:");
		//leaderBoardList.add(Name, gameTimer.getValue); or summin
	} 

	private void loseScreen(JFrame jf){
		gameTimer.stop();
		//Does all the nesseccary ui and backend changes in order to reset the board
		JOptionPane.showMessageDialog(rootPane, "YOU LOST!!", "DEFEAT", JOptionPane.PLAIN_MESSAGE);
		
		mineField = new MineField(16, 16); //creates the backend minefield
		timerLabel.setText("Time: 0s"); //resetting the timer GAMEBOARDPANEL WILL DO THIS ANYWAYS, BUT STARTS FROM 1
		//the replacing of the grid:
		mineSweeperPanel.remove(gamePanel);
		gamePanel = new GameBoardPanel(jf);
		mineSweeperPanel.add(gamePanel,BorderLayout.CENTER); 
		showPanel(mineSweeperPanel);
		
		minesLabel.setText("Mines left: 40");
		jf.repaint();
	} 

	//gameBoardPanel - the game panel itself - enclosed class
			private class GameBoardPanel extends JPanel{

				JFrame jf; //for repainting porpuses 
				public GameBoardPanel(JFrame jf) {
					this.jf = jf;

					gameTimer = new GameTimer();
					gameTimer.start();

					setLayout(new GridLayout(rows,cols));
					for (int i = 0; i < rows; i++) {
						for (int j = 0; j < cols; j++) {
			            JButton cell = new JButton();
			            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			            cell.setBackground(Color.LIGHT_GRAY);
						
						cell.setIcon(new ImageIcon(blankImage));	
						
						cell.addActionListener(new CellListener(jf, this, i, j));
			        	this.add(cell);
			        }
				}
			}
		}
		///////////////////////////////////////////////////////////////////////
		//ActionListeners:

			//Flaglistener:
		private class FlagListener implements ActionListener{
			private JFrame jf;
			FlagListener(JFrame jf){
				this.jf = jf;
			}
			@Override
			public void actionPerformed(ActionEvent ae){
				if(!mineField.isFlagMode()){
					//set in the frontend
					ImageIcon icon = new ImageIcon("./image/flagClicked.png"); 
					Image img = icon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
					flagButton.setIcon(new ImageIcon(img));
					jf.repaint();

					mineField.setFlagMode(true); //set in the backend
				}

				else if(mineField.isFlagMode()){
					//set int the frontend
					ImageIcon icon = new ImageIcon("./image/flag.png"); 
					Image img = icon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
					flagButton.setIcon(new ImageIcon(img));
					jf.repaint();

					mineField.setFlagMode(false); //set in the backend
				}
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////
		//CellListener: (Handles each button)
		private class CellListener implements ActionListener{
			private JFrame jf;
			int row; 
			int col;
			GameBoardPanel gbp;

			CellListener(JFrame jf, GameBoardPanel gbp, int r, int c){
				this.jf = jf;
				this.gbp = gbp;
				this.row = r;
				this.col = c;
			}

			@Override
			public void actionPerformed(ActionEvent ae){
				//boolean isTimedBefore = mineField.hasTimer(); //this is so we can check if the timer changed

				
				mineField.reveal(row, col);

				Image image;
				

				//this is mainly needed bc of recursive revealing
				//if only a single cell was revealed then it would be enough to set the image for the clicked button
				//however with not clicked buttons being changed we need to iterate through the whole grid
				Component[] components = gbp.getComponents(); //we store the buttons to set the image
				for (int i = 0; i < components.length; i++) {
					if (components[i] instanceof JButton) {
						JButton button = (JButton) components[i];
						int currRow = i / rows;
                    	int currCol = i % cols;
							Tile tile = mineField.getTile(currRow, currCol);
							if(tile.isRevealed()){
								image = numImages[(mineField.getTile(currRow, currCol).getMinesAround())+1]; // replace with the actual path to your image
								button.setIcon(new ImageIcon(image));
							}
							
							else if(mineField.isFlagMode()){
								if(tile.isFlagged()){
									ImageIcon icon = new ImageIcon("./image/flag.png"); 
									image = icon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
									button.setIcon(new ImageIcon(image));
								}
								else if(!tile.isFlagged()){
									button.setIcon(new ImageIcon(blankImage));
								}
							}

						}
					}

				//updates minesLeft:
				minesLabel.setText("Mines left: " +Integer.toString(mineField.getMinesLeft())); 

				jf.repaint();

				//Checks if the game has ended and does the nesseccary actions
				int outcome = mineField.checkEndOutcome(false);
				switch (outcome) {
					case -1:
						minesLabel.setText("YOU LOST!!!");
						loseScreen(jf);
						break;
					case 0:
							
							break;
					case 1:
						minesLabel.setText("YOU WON!!!");
						victoryScreen(jf);
						break;
					default:
						break;
				}
			}
		}
		////////////////////////////////////////////////////////////////////
		//Timer class:
		private class GameTimer implements ActionListener {

			private Timer timer;
			private int seconds;
			

			public GameTimer() {
				seconds = 0;

				// Create a Timer with a one-second delay
				timer = new Timer(1000, this);
			}

			// Start the timer
			public void start() {
				timer.start();
			}

			// Stop the timer
			public void stop() {
				timer.stop();
			}

			// Get the current value of the timer
			public int getValue() {
				return seconds;
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				seconds++;
				timerLabel.setText("Time: " + seconds + " s");
			}
		
			
		}
}
