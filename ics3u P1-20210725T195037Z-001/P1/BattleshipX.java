import java.util.*;
import java.io.*;

public class BattleshipX {
	
	// Reads a file called fileName and loads a two-dimensional array called board with the file's contents
	public static void loadFile(char[][] board, String fileName) throws IOException {
		BufferedReader inputStream = null; 
		
		try {
			inputStream = new BufferedReader(new FileReader(fileName));
			// Reads the first line of data in the file
			String lineRead = inputStream.readLine();
			int row = 0;
        	while (lineRead != null) {
				// Assigns each character in lineRead to its respective column in the board
				for (int col = 0; col < board[row].length; col++) {
					board[row][col] = lineRead.charAt(col);
				} 	 	
				
				// Reads the next line of data in the file
				lineRead = inputStream.readLine();
				row++;
			}
		}
		catch (FileNotFoundException exception) {
			System.out.println("Error opening file");
		}
		finally {		
			if (inputStream != null)
				inputStream.close();
		}
	}
	
	public static void displayBoard(char[][] board1, char[][] board2) {
		System.out.println("       CPU BOARD     \t      PLAYER BOARD   ");
		System.out.println(" |0 1 2 3 4 5 6 7 8 9\t |0 1 2 3 4 5 6 7 8 9");
		System.out.println("- -------------------\t- -------------------");
		
		for (int i = 0; i < board1.length; i++) {
			System.out.print((char)(i + 65) + "|"); 
			
			for (int j = 0; j < board1[i].length; j++) { 
				/*
				 * This variable below is assigned with a ternary operator that "reads" the board and checks to see if the location has a ship. 
				 * If it does, the variable is assigned with an asterisk which will hide the ship from the view of the player when printed
				 */				
				char readBoard1 = (board1[i][j] != 'H' && board1[i][j] != 'M' && board1[i][j] != '*' && board1[i][j] != ' ') ? '*' : board1[i][j];
				System.out.print(readBoard1);
			}
			
			System.out.print("\t" + (char)(i + 65) + "|"); 		
			
			for (int j = 0; j < board2[i].length; j++) {
				System.out.print(board2[i][j]);
			}		
			
			System.out.println();
		}
		System.out.println();
	}
	
	//Counts the number of times a certain descriptor appears in the board and returns that value
	public static int counter(char[][] board, char desc) {
		int count = 0;

		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[i].length; j++) {
				count += (board[i][j] == desc) ? 1 : 0;
			}
		}
		
		return count;	
	}
	
	// Takes coordinate and makes the move in the repsective row and column, and gives player appropriate message after making move 
	public static void placeMove(char[][]board, String move, String turn) {		
		int row = (int)move.charAt(0) - 65;
		int col = 2 * ((int)move.charAt(1) - 48);
		
		if (board[row][col] != '*') { 			
			String msg1 = (turn.equals("PLAYER")) ? "\nDirect hit, nice shot sir!" : ("The computer made a move at " + move + " and hit");
			System.out.println(msg1);
			/* 
			 * The if statement below checks to see if the ship descriptor (C,B,S,D,P) only appears in the location the player made their move
			 * If this if statement is true, then the player has successfully sunk the ship associated with that particular descriptor 
			 */
			if (counter(board, board[row][col]) == 1) {
				String ship = "";
			
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

				String msg2 = (turn.equals("PLAYER")) ? ("You have sunk the " + ship + " captain, excellent work!") : ("The computer has sunk your " + ship + ".");
				System.out.println(msg2);
			}
		} else {
			String msg3 = (turn.equals("PLAYER")) ? "\nYou have missed sir!" : ("The computer made a move at " + move + " and missed.");
			System.out.println(msg3);
		}
		
		board[row][col] = (board[row][col] != '*') ? 'H' : 'M';				
	}	
	
	public static boolean moveValid(String move, char[][] board) {
		int row = (int)move.charAt(0) - 65;
		int col = 2 * ((int)move.charAt(1) - 48);
		return (row >= 0 && row < 10) && (col >= 0 && col < 19) &&
				(board[row][col] != 'H') && (board[row][col] != 'M');
	}
		
	public static void displayGameStatistics(char[][]board, String player) {
		int hitCount = counter(board,'H');
		int missCount = counter(board,'M');
		int attackCount = hitCount + missCount;
		int accuracy = (int)(hitCount * 100 / attackCount);
		
		System.out.println("\n" + player + ":");
		System.out.println("Attacks: " + attackCount);
		System.out.println("Hits: " + hitCount);
		System.out.println("Misses: " + missCount);
		System.out.println("Accuracy: " + accuracy + "%"); 
	}
	
	public static void main(String[] args) throws IOException {
		Scanner keyboard = new Scanner(System.in);
		Random ran = new Random();
		
		char[][] playerBoard = new char[10][19];
		char[][] cpuBoard = new char[10][19]; 		
		String turn = "PLAYER";
		String move;
		
		loadFile(playerBoard,"PLAYER.txt");
		loadFile(cpuBoard,"CPU.txt");
		displayBoard(cpuBoard,playerBoard);					
		do { 		
			if (turn.equals("PLAYER")) {
				System.out.println("Please enter an attack coordinate Captain:");
				move = keyboard.next();
				
				// This ternary operator the user enter lowercase letters as input and converts it to its upper case value
				move = ((int)move.charAt(0) >= 97 && (int)move.charAt(0) <= 106) ? (char)((int)move.charAt(0) - 32) + "" + move.charAt(1) : move;  
				
				
				// Prevents player from placing a move in a location where they have already placed a move	
				if (moveValid(move, cpuBoard)) {			
					placeMove(cpuBoard,move,turn); 
					turn = "CPU"; // If the player has made a valid move, the CPU can proceed with its turn
				}			
			} else if (turn.equals("CPU")) {
				move = (char)(ran.nextInt(10) + 65) + "" + (char)(ran.nextInt(10) + 48);
					
				if (moveValid(move, playerBoard)) {
					placeMove(playerBoard, move, turn);
					turn = "PLAYER";
					System.out.println(); 
					displayBoard(cpuBoard,playerBoard);												 
				}
			}
		} while (counter(cpuBoard, 'H') < 17 && counter(playerBoard, 'H') < 17); // This loop will keep looping until all 17 ship descriptors on a board are replaced by an 'H', meaning that they are all sunk
		
		String winMsg = (counter(cpuBoard, 'H') == 17) ? "CONGRATULATIONS CAPTAIN! YOU HAVE SUCCESSFULLY SUNK ALL OF THE COMPUTER'S SHIPS!" : "GAME OVER! THE COMPUTER HAS SUCCESSFULLY SUNK ALL YOUR SHIPS!";
		System.out.println("\n" + winMsg);
		
		displayGameStatistics(cpuBoard,"PLAYER");
		displayGameStatistics(playerBoard,"CPU");
	}
} 	