package frames;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class GameFrame extends JFrame{
	private MineField mineField;
	private GameTimer gameTimer;
	private BombTimer bombTimer;
	private LeaderBoard leaderBoardList;

	private int rows;
	private int cols;

	
	private JPanel mineSweeperPanel;
	private GameBoardPanel gamePanel;
	private JPanel leaderBoardPanel;
	
	private JLabel minesLabel;
	private static JLabel timerLabel = new JLabel("Time: " + 0); //static bc of readObject issue
	private static JLabel bombTimerLabel = new JLabel("Timed bomb: 5s"); //static bc of readObject issue
	private JButton flagButton;
	

	private transient Image[] numImages;
	private transient Image flagImage;
	private transient Image blankImage;


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

		
		


		this.setLayout(new BorderLayout());
		//creating the minefield and the timers (the backend)
		mineField = new MineField(rows,cols);
		bombTimer = new BombTimer();
		gameTimer = new GameTimer();

		
		
		/////////////////////////////////////////////////////////////////////////////////
		//Menu:
		JMenuBar menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(400,30));
	
		JMenu fileMenu = new JMenu("File");
		JMenu gameMenu = new JMenu("Game");
		JMenu leaderBoard = new JMenu("LeaderBoard");
		
		JMenuItem save= new JMenuItem("Save");
		save.addActionListener(new SaveListener());

		JMenuItem load= new JMenuItem("Load");
		load.addActionListener(new LoadListener());
		
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
		
		JPanel statusBar = new JPanel();
		statusBar.setLayout(new FlowLayout());
		
		minesLabel = new JLabel("Mines left: "); //only creation 
		
		

		//Flag button:
		flagButton = new JButton();
		flagButton.addActionListener(new FlagListener()); //action listener

		//initialization:
		flagButton.setPreferredSize(new Dimension(20, 20));
        flagButton.setIcon(new ImageIcon(flagImage));
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


		JTable leaderBoardTable = new JTable(leaderBoardList); //will use game data as constructor
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


		leaderBoardList = new LeaderBoard();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./save_files/leaderboard"))) {
			leaderBoardList = (LeaderBoard) ois.readObject();
		} catch (IOException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}
        
		addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./save_files/leaderboard"))) {
					oos.writeObject(leaderBoardList);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
            }
        });
		
		
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

		
		flagButton.setIcon(new ImageIcon(flagImage));
		
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
					
					else if(tile.isFlagged()){
						button.setIcon(new ImageIcon(flagImage));
					}
					else if(!tile.isFlagged()){
						button.setIcon(new ImageIcon(blankImage));
					}
					

				}
			}
	}
	
	private void victoryScreen(){
		gameTimer.stop();
		bombTimer.stop();
		gamePanel.makeUneditable();

		String name = JOptionPane.showInputDialog("You Won!!! Please enter your name:");

		if(name != null){
		int score = gameTimer.getValue();
		leaderBoardList.add(score, name);
		}
		
	} 

	private void loseScreen(){
		gameTimer.stop();
		bombTimer.stop();
		gamePanel.makeUneditable();

		JOptionPane.showMessageDialog(rootPane, "YOU LOST!!", "DEFEAT", JOptionPane.PLAIN_MESSAGE);
		

	} 
///////////////////////////////////////////////////////////////////////////////////
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
					flagButton.setIcon(new ImageIcon(flagImage));
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
						//victoryScreen(); //for testing
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
		private static class GameTimer implements ActionListener, Serializable {

			private transient Timer timer;
			private int seconds;

			public GameTimer() {
				seconds = 0;

				// Create a Timer with a one-second delay
				timer = new Timer(1000, this);
			}

			// Start the timer
			public void start() {
				if (timer != null) {
					timer.start();
				}
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
					if (timerLabel != null) {
						timerLabel.setText("Time: " + seconds + "s");
					}
				}
			
			private void writeObject(ObjectOutputStream out) throws IOException {
				// Save the elapsed time before serialization
				out.writeInt(seconds);
			}

			 private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
				// Restore the elapsed time after deserialization
				seconds = in.readInt();
				// Recreate the timer and schedule the task
				timer = new Timer(1000, this);		

				timer.start();
			}
		
		}
	////////////////////////////////////////////////////////////////////
	//BombTimer class:
	private class BombTimer implements ActionListener, Serializable {

			private transient Timer timer;
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

			private void writeObject(ObjectOutputStream out) throws IOException {
				// Save the elapsed time before serialization
				out.writeInt(seconds);
			}

			 private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
				// Restore the elapsed time after deserialization
				seconds = in.readInt();
				// Recreate the timer and schedule the task
				timer = new Timer(1000, this);
				timer.start();
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

	private class SaveListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			JFileChooser chooser = new JFileChooser();
			// szülő ablakot és a kiválasztógomb szövegét definiálni kell
			int returnVal = chooser.showDialog(null, "Select");
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				String file = chooser.getSelectedFile().getAbsolutePath();
	
				try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
				oos.writeObject(mineField);
				oos.writeObject(gameTimer);
				oos.writeObject(bombTimer);
				}	catch (IOException  | NullPointerException e) {e.printStackTrace();}
			}
		}
	}


	private class LoadListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ae){
			JFileChooser chooser = new JFileChooser();
			// szülő ablakot és a kiválasztógomb szövegét definiálni kell
			int returnVal = chooser.showDialog(null, "Select");
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				String file = chooser.getSelectedFile().getAbsolutePath();
	
				try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
					mineField = (MineField) ois.readObject();
					gameTimer.stop();
					gameTimer.reset();
					gameTimer = (GameTimer) ois.readObject();
					bombTimer = (BombTimer) ois.readObject();

					redrawField();
					repaint();
				
				}	catch (IOException | ClassNotFoundException | NullPointerException e) {e.printStackTrace();}
			}
		}
	}
}