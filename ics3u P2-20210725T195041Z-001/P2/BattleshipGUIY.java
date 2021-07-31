import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class BattleshipGUIY {

	private static char[][] playerBoard, cpuBoard; 
	private static JButton[][] displayPlayerBoard, displayCPUBoard;  
	private static JLabel[][] playerHitsMisses, cpuHitsMisses; 
	private static JPanel[][] basePlayerBoard, baseCPUBoard;
	private static JLabel[] msgUpdates = {new JLabel("Please open the PLAYER.txt file!"), new JLabel(), new JLabel("Please open the CPU.txt file!"), new JLabel()};
    private static JFileChooser chooser;
    private static int d = 100; //this variable is the upper-limit range for the random int generator stored by variable o (see lines 283-288)
		
	public static void main(String[] args) {		
		playerBoard = new char[10][10]; 
		cpuBoard = new char[10][10];
		basePlayerBoard = new JPanel[10][10];
		baseCPUBoard = new JPanel[10][10];
		displayPlayerBoard = new JButton[10][10];
		displayCPUBoard = new JButton[10][10];
		playerHitsMisses = new JLabel[10][10];
		cpuHitsMisses = new JLabel[10][10];
		
 		//THE FOLLOWING LINES(54 - 67) CREATE THE FILE MENU AND ITS OPTIONS 	
		chooser = new JFileChooser(); 
		chooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt")); //only files with the extension ".txt" will show up when this filter is applied			
 		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("FILE");
		menuBar.add(fileMenu);
		JMenuItem[] menuOptions = {new JMenuItem("OPEN"), new JMenuItem("RESTART GAME"), new JMenuItem("EXIT")};
		int[] keys = {KeyEvent.VK_O, KeyEvent.VK_R, KeyEvent.VK_E};			
		
		for (int i = 0; i < menuOptions.length; i++) {
			menuOptions[i].addActionListener(new MenuOptions());
			menuOptions[i].setActionCommand(Integer.toString(i));
			menuOptions[i].setAccelerator(KeyStroke.getKeyStroke(keys[i], ActionEvent.CTRL_MASK));
			fileMenu.add(menuOptions[i]);
		}		
			 		
 		JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 5, 5));			
  		bottomPanel.add(getMsgPanel(msgUpdates[0], msgUpdates[1], false));
		bottomPanel.add(getMsgPanel(msgUpdates[2], msgUpdates[3], true));		
		
 		JPanel mainPanel = new JPanel(new BorderLayout());  		
 		mainPanel.add(getBoardPanel(basePlayerBoard), BorderLayout.WEST);
 		mainPanel.add(getBoardPanel(baseCPUBoard), BorderLayout.EAST);
 		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
 				
 		JFrame frame = new JFrame("Battleship GUI"); 		
 		frame.setVisible(true);
 		frame.setResizable(false);
 		frame.setSize(1110, 670);
 		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 		frame.setJMenuBar(menuBar);		
		frame.setContentPane(mainPanel);
		frame.pack(); 
		
		showDifficultyChooser();	 	
	}
		
	public static JPanel getMsgPanel(JLabel msgUpdate, JLabel shipSunkUpdate, boolean cpu) {
		Color textColour = cpu ? Color.RED : Color.BLUE;
		Font textFont = new Font("Arial", Font.BOLD, 14);
		msgUpdate.setForeground(textColour);
		shipSunkUpdate.setForeground(textColour);
		msgUpdate.setFont(textFont);
		shipSunkUpdate.setFont(textFont);
		
		JPanel msgPanel = new JPanel(new GridLayout(2, 1));		
		msgPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), (cpu ? "CPU" : "PLAYER") + " MESSAGES:", TitledBorder.LEFT, TitledBorder.TOP));		
		msgPanel.add(msgUpdate);
		msgPanel.add(shipSunkUpdate);		
		return msgPanel;
	}	
		
	public static JPanel getBoardPanel(JPanel[][] baseGrid) {
		JPanel boardPanel = new JPanel(new GridLayout(10, 10));
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.add(new JLabel(new ImageIcon("cols.png")), BorderLayout.EAST);		
		
		for (int i = 0; i < baseGrid.length; i++) {
			for (int j = 0; j < baseGrid[i].length; j++) {
				JPanel empty = new JPanel(new BorderLayout());
				empty.setPreferredSize(new Dimension(50, 50));
				empty.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				baseGrid[i][j] = empty;
				boardPanel.add(baseGrid[i][j]);
			}
		}		
		
		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.add(new JLabel(new ImageIcon("rows.png")), BorderLayout.WEST);
		panel2.add(boardPanel, BorderLayout.EAST);
		
		JPanel basePanel = new JPanel(new BorderLayout());
		basePanel.setBorder(BorderFactory.createLoweredBevelBorder());
		basePanel.add(panel1, BorderLayout.NORTH);
		basePanel.add(panel2, BorderLayout.CENTER);
		return basePanel;
	}
	
	//this gives user options to change the likely hood the CPU will hit ships by choosing between 3 options (easy, medium, hard)
	public static void showDifficultyChooser() {
		ButtonGroup group = new ButtonGroup();
		JRadioButton[] difficultyLevels = {new JRadioButton("EASY"), new JRadioButton("MEDIUM"), new JRadioButton("HARD")};
		Color[] colours = {new Color(50, 205, 50), new Color(255, 140, 0), Color.RED};
		difficultyLevels[0].setSelected(true); //the difficulty is easy by default, so it will show as selected	
		JPanel panel = new JPanel(new GridLayout(4, 1));
		panel.setBorder(BorderFactory.createLoweredBevelBorder());	
		panel.add(new JLabel("Please select a difficulty level:"));
		
		for (int i = 0; i < difficultyLevels.length; i++) {
			difficultyLevels[i].setBackground(new Color(176, 224, 230));
			difficultyLevels[i].setForeground(colours[i]);
			difficultyLevels[i].setFont(new Font("Arial", Font.BOLD, 14));
			difficultyLevels[i].addActionListener(new SetDifficulty());
			difficultyLevels[i].setActionCommand(Integer.toString(i == 2 ? 5 : i));
			panel.add(difficultyLevels[i]);
			group.add(difficultyLevels[i]);
		}
	
		JOptionPane.showMessageDialog(null, panel, "Welcome to Battleship!", JOptionPane.PLAIN_MESSAGE); 
	}		
		
	//Counts the number of times a certain descriptor appears in the board and returns that value
	public static int counter(char[][] board, char desc) {
		int count = 0;

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				if (board[i][j] == desc) {
					count++;
				}
			}
		}
		
		return count;	
	}		
	
	private static class SetDifficulty implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			d = 40 * Integer.parseInt(e.getActionCommand()) + 100; //sets the upper limit range for the random int generator stored in variable o (located in class Turn)
		}
	}
	
	private static class MenuOptions implements ActionListener { 
		
		public void actionPerformed(ActionEvent e) {
			if ((e.getActionCommand()).equals("2")) {
				System.exit(0);
			} else if ((e.getActionCommand()).equals("1")) {				
				reset(playerBoard, displayPlayerBoard, playerHitsMisses, msgUpdates[0], msgUpdates[1], "Player");
				reset(cpuBoard, displayCPUBoard, cpuHitsMisses, msgUpdates[2], msgUpdates[3], "CPU");
				d = 100;
				showDifficultyChooser();				
			} else {
				if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                	File file = chooser.getSelectedFile();
                	
            		try {
						if (((file.getName()).toLowerCase()).equals("cpu.txt") && cpuBoard[0][0] == '\u0000') { //Loads the file if the board isn't already loaded. '\u0000' is the default character a newly created char array is loaded with
							loadFile(cpuBoard, baseCPUBoard, displayCPUBoard, cpuHitsMisses, msgUpdates[2], file, true);										
						} else if (((file.getName()).toLowerCase()).equals("player.txt") && playerBoard[0][0] == '\u0000') {
							loadFile(playerBoard, basePlayerBoard, displayPlayerBoard, playerHitsMisses, msgUpdates[0], file, false);
						}                			
            		} catch (IOException exception) { 
            		}
           		} 				
			}
		}
		
		// Reads a file and loads a two-dimensional array called board with the file's contents. 
		public void loadFile(char[][] board, JPanel[][] baseBoard, JButton[][] displayBoard, JLabel[][] hitsAndMisses, JLabel msgUpdate, File file, boolean cpu) throws IOException {
			BufferedReader inputStream = null;					
			try {
				inputStream = new BufferedReader(new FileReader(file));				
				// Reads the first line of data in the file
				String lineRead = inputStream.readLine();
				int row = 0;
        		while (lineRead != null && row < board.length && lineRead.length() >= 19) { 
					for (int col = 0; col < board[row].length; col++) {
						board[row][col] = lineRead.charAt(col * 2); //this was taken from Jarvis's P1 code because we are using a smaller array to store the descriptors					
					} 	 	
					
					// Reads the next line of data in the file
					lineRead = inputStream.readLine();
					row++;	
				}
			}	
			catch (FileNotFoundException exception) {
				System.out.println("Error opening file!");
			}
			finally {		
				if (inputStream != null)
					inputStream.close();
			}
			
			//The boards are set up only if the file loaded has the correct number of ship descriptors (C, B, S, D, P) and asterisks
			if (counter(board, '*') == 83 && counter(board, 'C') == 5 && counter(board, 'B') == 4 && counter(board, 'D') == 3 && counter(board, 'S') == 3 && counter(board, 'P') == 2) {
				msgUpdate.setText("File Loaded!");
				
				for (int i = 0; i < board.length; i++) {
					for (int j = 0; j < board[i].length; j++) {
						hitsAndMisses[i][j] = new JLabel(new ImageIcon((board[i][j] != '*' ? "H" : "M") + ".png")); //the icons in this array will be revealed when a move is made																		
						displayBoard[i][j] = new JButton(cpu ? "*" : String.valueOf(board[i][j])); //if the CPU board is being loaded, the button will display a asterisk so the player will not directly see CPU ships. 
						displayBoard[i][j].setFont(new Font("Arial", Font.BOLD, 16)); 
						displayBoard[i][j].setForeground(cpu ? Color.RED : Color.BLUE);
						displayBoard[i][j].setBackground(new Color(176, 224, 230)); 
						baseBoard[i][j].add(displayBoard[i][j], BorderLayout.CENTER);							
						
						if (playerBoard[9][9] != '\u0000' && cpuBoard[9][9] != '\u0000') { //If both boards have loaded properly, the game can begin		
							displayCPUBoard[i][j].addActionListener(new Turn()); //action listener is added to buttons on CPU board
							displayCPUBoard[i][j].setActionCommand(Integer.toString(i) + Integer.toString(j));																					
						}						
					}
				}								
			} else {
				for (int i = 0; i < board.length; i++) {
					for (int j = 0; j < board[i].length; j++) {
						board[i][j] = '\u0000'; //the char board is loaded with default char value so the user can load in a valid file	
					}
				}
				
				JOptionPane.showMessageDialog(null, "The file you loaded was invalid. Please load a valid file!", "Message", JOptionPane.ERROR_MESSAGE);									
			}
		}
		
		public void reset(char[][] board, JButton[][] displayBoard, JLabel[][] hitsAndMisses, JLabel updateMsg, JLabel shipSunkMsg, String player) {
			updateMsg.setText("Please open the " + player.toUpperCase() + ".txt file!");
			shipSunkMsg.setText(null);						
			
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if (displayBoard[0][0] != null) { 
						displayBoard[i][j].setVisible(false); //If you choose to restart the game while it is ongoing, all buttons that are still visible will be invisible						
						hitsAndMisses[i][j].setVisible(false); //the JLabels containing the hit/miss icons are also invisible					
					}
					
					board[i][j] = '\u0000'; //the char array is loaded with the default value, so the user can upload a different file
				}
			}
		}		
	}
	
	private static class Turn implements ActionListener {
		
		public void actionPerformed (ActionEvent event) {
			String command = event.getActionCommand();
			int row, col;
			
			row = (int) command.charAt(0) - 48;
			col = (int) command.charAt(1) - 48;
			placeMove(cpuBoard, baseCPUBoard, displayCPUBoard, cpuHitsMisses, row, col, false);				
			
			if (counter(cpuBoard, 'H') == 17) { //player wins if all 17 ship descriptors on the CPU board are hit
				victory(true);
			} else {
				int o = new Random().nextInt(d); //the value generated is used to determine whether the CPU hits or misses a ship. If value is less than 83, it misses, otherwise, it hits

				do { 
					row = new Random().nextInt(10);
					col = new Random().nextInt(10);				
				} while (o < 83 ? playerBoard[row][col] != '*' : (playerBoard[row][col] == '*' || playerBoard[row][col] == 'M' || playerBoard[row][col] == 'H')); 	
				
				placeMove(playerBoard, basePlayerBoard, displayPlayerBoard, playerHitsMisses, row, col, true);			
					
				if (counter(playerBoard, 'H') == 17) {
					victory(false);
				}				
			}		 
		}
		
		// Takes coordinate, makes the move in the repsective row and column, and updates the messages appropriately
		public void placeMove(char[][] board, JPanel[][] base, JButton[][] displayBoard, JLabel[][] hitsAndMisses, int row, int col, boolean cpu) {						
			boolean hit = board[row][col] != '*';
			String ship = ""; 
			
			if (hit) {
				switch (board[row][col]) {
					case 'C':
						ship = "Aircraft Carrier";
						break;
					case 'B': 
						ship = "Battleship";
						break;
					case 'S':
						ship = "Submarine";
						break;
					case 'D':
						ship = "Destroyer";
						break;
					case 'P':
						ship = "Patrol Boat";
						break;
				}					
			}				 

			if (cpu) {  
				msgUpdates[2].setText("The computer has attacked " + String.valueOf((char) (row + 65)) + Integer.toString(col) + " and " + (hit ? ("hit your " + ship + ".") : "missed."));
				msgUpdates[3].setText(ship != "" && counter(board, board[row][col]) == 1 ? ("The computer has sunk your " + ship + "!") : null); //If a ship was hit and the ship only appears in the location the move was made, that ship has been sunk.		
			} else {
				msgUpdates[0].setText(hit ? "Direct hit, nice shot sir!" : "You have missed sir!");
				msgUpdates[1].setText(ship != "" && counter(board, board[row][col]) == 1 ? ("You have sunk the " + ship + " captain, excellent work!") : null); 				
			}
			
			board[row][col] = hit ? 'H' : 'M';
			displayBoard[row][col].setVisible(false); //the button at that location will be invisible so you cant click on it again
			base[row][col].add(hitsAndMisses[row][col], BorderLayout.CENTER); //the button will be replaced by an image either representing a hit or a miss							
		}
		
		//When this method is called, it lets you know whether you won or lost and it prevents anyone from making further moves. 
		public void victory(boolean win) { 
			for (int i = 0; i < 10; i++) {
				for (int j = 0; j < 10; j++) {
					playerHitsMisses[i][j].setIcon(new ImageIcon(GrayFilter.createDisabledImage((new ImageIcon((playerHitsMisses[i][j].getIcon()).toString())).getImage()))); //the hit/miss icons will be greyed out 
					cpuHitsMisses[i][j].setIcon(new ImageIcon(GrayFilter.createDisabledImage((new ImageIcon((cpuHitsMisses[i][j].getIcon()).toString())).getImage())));
					displayPlayerBoard[i][j].setVisible(false); //the buttons will no longer be visible, preventing anyone from placing moves on current boards 
					displayCPUBoard[i][j].setVisible(false); 
				}				
			}
			
			JOptionPane.showMessageDialog(null, win ? "CONGRATULATIONS! YOU WIN!" : "GAME OVER! YOU LOSE!", "Message", JOptionPane.INFORMATION_MESSAGE);									
		}			 
	}
}	 	