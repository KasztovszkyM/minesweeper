package frames;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;


public class GameFrame extends JFrame{
	private MineField mineField;
	private GameTimer gameTimer;
	private BombTimer bombTimer;
	private LeaderBoard leaderBoardList;

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
	private JLabel bombTimerLabel;
	private JButton flagButton;
	
	private JTable leaderBoardTable;

	private Image[] numImages;
	private Image flagImage;
	private Image blankImage;


///////////////////////////////////////////////////////////////////////////////
	//intialization of the components
	private void initComponents(){
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

		bombTimerLabel = new JLabel("Timed bomb: 5s");
		leaderBoardList = new LeaderBoard();


		this.setLayout(new BorderLayout());
		//creating the minefield and the timers (the backend)
		mineField = new MineField(rows,cols);
		bombTimer = new BombTimer();
		gameTimer = new GameTimer();

		
		
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
		reset.addActionListener(new ResetListener());

		JMenuItem solve= new JMenuItem("Solve");
		solve.addActionListener(new SolveListener());

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
		flagButton.addActionListener(new FlagListener()); //action listener

		//initialization:
		flagButton.setPreferredSize(new Dimension(20, 20));
		ImageIcon icon3 = new ImageIcon("./image/flag.png");
        Image img = icon3.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
        flagButton.setIcon(new ImageIcon(img));
	////////////////////////////////////////////////////////////////////////////////////////////66
		
		//game panel:
        gamePanel = new GameBoardPanel();
		
		mineSweeperPanel = new JPanel();
		mineSweeperPanel.setLayout(new BorderLayout());
	
		statusBar.add(bombTimerLabel);
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


		leaderBoardTable = new JTable(leaderBoardList); //will use game data as constructor
		JScrollPane scrollPane = new JScrollPane(leaderBoardTable); //put into a scrollpane
		leaderBoardPanel.add(scrollPane,BorderLayout.CENTER);
		
		
	//////////////////////////////////////////////////////////////
		this.add(mineSweeperPanel); //always begins with the game panel

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

		
		
		rows = r;
		cols = c;
		
		initComponents();
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


	private void resetField(){
		gameTimer.reset();
		bombTimer.reset();
		gameTimer.start();
		
		mineField = new MineField(rows, cols);

		//the replacing of the grid:
		mineSweeperPanel.remove(gamePanel);
		gamePanel = new GameBoardPanel();
		mineSweeperPanel.add(gamePanel,BorderLayout.CENTER); 
		showPanel(mineSweeperPanel); 
		
		minesLabel.setText("Mines left: 40");
	}


	private void redrawField(){
		Image image;
		//this is mainly needed bc of recursive revealing
		//if only a single cell was revealed then it would be enough to set the image for the clicked button
		//however with not clicked buttons being changed we need to iterate through the whole grid
		Component[] components = gamePanel.getComponents(); //we store the buttons to set the image
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
	}
	
	private void victoryScreen(){
		gameTimer.stop();
		bombTimer.stop();
		gamePanel.makeUneditable();

		String name = JOptionPane.showInputDialog("You Won!!! Please enter your name:");
		int score = gameTimer.getValue();
		leaderBoardList.add(score, name);
	
		
	} 

	private void loseScreen(){
		gameTimer.stop();
		bombTimer.stop();
		gamePanel.makeUneditable();

		//Does all the nesseccary ui and backend changes in order to reset the board
		JOptionPane.showMessageDialog(rootPane, "YOU LOST!!", "DEFEAT", JOptionPane.PLAIN_MESSAGE);
		

	} 

	//gameBoardPanel - the game panel itself - enclosed class
			private class GameBoardPanel extends JPanel{
				public GameBoardPanel() {
					

					gameTimer.start();

					setLayout(new GridLayout(rows,cols));
					for (int i = 0; i < rows; i++) {
						for (int j = 0; j < cols; j++) {
			            JButton cell = new JButton();
			            cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
			            cell.setBackground(Color.LIGHT_GRAY);
						
						cell.setIcon(new ImageIcon(blankImage));	
						
						cell.addActionListener(new CellListener(i, j));
			        	this.add(cell);
			        }
				}
			}

			//removes acion listeners so you can click on any of the buttons
			public void makeUneditable(){
				Component[] components = getComponents(); 
				
				for (int i = 0; i < components.length; i++){
					JButton button = (JButton) components[i];
					//basically a removeAllActionListeners:
					for( ActionListener al : button.getActionListeners() ) {
						button.removeActionListener( al );
					}
				}
			}
		}
		///////////////////////////////////////////////////////////////////////
		//ActionListeners:

			//Flaglistener:
		private class FlagListener implements ActionListener{
			
			@Override
			public void actionPerformed(ActionEvent ae){
				if(!mineField.isFlagMode()){
					//set in the frontend
					ImageIcon icon = new ImageIcon("./image/flagClicked.png"); 
					Image img = icon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
					flagButton.setIcon(new ImageIcon(img));
					repaint();

					mineField.setFlagMode(true); //set in the backend
				}

				else if(mineField.isFlagMode()){
					//set int the frontend
					ImageIcon icon = new ImageIcon("./image/flag.png"); 
					Image img = icon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
					flagButton.setIcon(new ImageIcon(img));
					repaint();

					mineField.setFlagMode(false); //set in the backend
				}
			}
		}
		/////////////////////////////////////////////////////////////////////////////////////
		//CellListener: (Handles each button)
		private class CellListener implements ActionListener{
			int row; 
			int col;
			

			
			CellListener( int r, int c){
				this.row = r;
				this.col = c;
			}

			@Override
			public void actionPerformed(ActionEvent ae){
				boolean isTimedBefore = mineField.hasTimer(); //this is so we can check if the timer changed

				mineField.reveal(row, col);

				redrawField();
				
				//starting and stopping the timers based on the following logic:
				//if: hasTimer changed from false to true we begin the bombTimer
				//if: hasTimer is false (no matter what it was before) we stop and reset the bombTimer
				if(isTimedBefore != mineField.hasTimer() && mineField.hasTimer()){
					
					
					bombTimer.start();
				}
				if(!mineField.hasTimer()){
					bombTimer.stop();
					bombTimer.reset();
				}

				//updates minesLeft:
				minesLabel.setText("Mines left: " +Integer.toString(mineField.getMinesLeft())); 

				repaint();

				//Checks if the game has ended and does the nesseccary actions
				int outcome = mineField.checkEndOutcome();
				switch (outcome) {
					case -1:
						//victoryScreen();
						loseScreen();
						break;
					case 0:
							break;
					case 1:
						victoryScreen();
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

			public void reset(){
				seconds = 0;
				timerLabel.setText("Time: " + seconds + "s");
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				seconds++;
				timerLabel.setText("Time: " + seconds + "s");
			}
		
			
		}
	////////////////////////////////////////////////////////////////////
	//BombTimer class:
	private class BombTimer implements ActionListener {

			private Timer timer;
			private int seconds;

			public BombTimer() {
				seconds = 5;
				bombTimerLabel.setVisible(false);



				// Create a Timer with a one-second delay
				timer = new Timer(1000, this);
			}

			// Start the timer
			public void start() {
				bombTimerLabel.setVisible(true);
				timer.start();
			}

			// Stop the timer
			public void stop() {
				bombTimerLabel.setVisible(false);
				timer.stop();
			}

			// Get the current value of the timer
			public void reset() {
				seconds = 5;
				bombTimerLabel.setText("Timed bomb: " + seconds +"s");
			}
			
			@Override
			public void actionPerformed(ActionEvent e) {
				seconds--;
				bombTimerLabel.setText("Timed bomb: " + seconds +"s");
				if(seconds == 0){
					loseScreen(); 
				}
			}
		
			
		}

	private class ResetListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			resetField();
		}
	}

	private class SolveListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			mineField.revealAll();
			gameTimer.stop();
			redrawField();
			repaint();
		}
	}
}
