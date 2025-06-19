/*
 * Created July 16, 2014.
 * @author David Espinosa
 * I am not affiliated with Texas Lottery in any way and
 * am not trying to make profit from this.
 */

/**
 * To-do:
 * -> add undo for editing letters (Ctrl + z) and redo (Ctrl + y)
 * -> add New / Open / Save menus
 * -> add menu bar
 * -> play sound when a word if found
 * -> fix duplicate word winner glitch
 */

import static java.lang.System.out;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

public class BonusCashword {
    JFrame frame;
	JButton button;
	JTextField textField;
	MyJPanel myJP;
	Letter[][] grid;
	boolean[][] winners;
	int boardRows = 11, boardColumns = 11;

	String input = ""; // the chosen 20 letters
	ArrayList<Character> inputSet;
	int width, height; // of each box
	int xShift, yShift; // of the entire grid
	ArrayList<String> winningWords;
	ArrayList<RowColCount> wordInfos; 
	int amtWinners;
	boolean flipped;
	boolean wonGrandPrize;
	
	boolean editMode;
	int editX, editY;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BonusCashword().go());
    }
    
	public BonusCashword() {
    	frame = new JFrame("Bonus Cashword!");
		button = new JButton("RESET");
		button.setFont(new Font("arial", Font.BOLD, 15));
		textField = new JTextField("Enter letters", 20);
		textField.setFont(new Font("arial", Font.PLAIN, 16));
		myJP = new MyJPanel();
    	myJP.addMouseListener(myJP);
    	myJP.addKeyListener(myJP);

    	width = height = 40;
    	xShift = yShift = 50;
    	flipped = false;
    	wonGrandPrize = false;
    	editMode = false;
		grid = Letter.getSampleGridB();
    	
    	setVariables();
    	textField.setText("Enter letters");
	}
	
	public void setVariables() {
		winners = new boolean[boardRows][boardColumns];
		input = "";
		inputSet = new ArrayList<Character>();
		winningWords = new ArrayList<String>();
		wordInfos = new ArrayList<RowColCount>();
		amtWinners = 0;
		myJP.repaint();
		textField.setText("");
	}

    public void go() {
    	frame.setIconImage(new ImageIcon("BC_logo.png").getImage());
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setSize(545,650);
    	frame.setVisible(true);
    	frame.setResizable(false);
    	
    	button.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent ev) { // reset everything
    			setVariables();
    			for(Letter[] row : grid ){
    				for(Letter l : row ){
    					l.matching = false;
    				}
    			}
    		}
    	});
    	
		textField.addActionListener(new ActionListener() { // set event to happen when user presses enter
			public void actionPerformed(ActionEvent ev) {
				input = textField.getText();
				input = input.toUpperCase();
				
				for(char ch : input.toCharArray()) {
					if(ch >= 'A' && ch <= 'Z' && !inputSet.contains(ch)) // make sure its a letter
						inputSet.add(ch);
				}
				
				determineWinners();
				myJP.repaint();
			}
		});

		frame.getContentPane().add(BorderLayout.SOUTH, textField);
		frame.getContentPane().add(BorderLayout.NORTH, button);
		frame.getContentPane().add(BorderLayout.CENTER, myJP);
    }

    public void determineWinners() {
		checkLetters();
		checkWinners(grid); //horizontal words
		flipped = true;
		checkWinners(Letter.flip(grid)); // vertical words
		flipped = false;
    }
    
    public void checkLetters() {
    	if(input.length() == 0) return;
    	for(char c : inputSet) {
			updateLetter(c);
    	}
    }

    public void updateLetter(char ch) {
    	for(int r = 0; r < grid.length; r++) {
    		for(int c = 0; c < grid[r].length; c++) {
    			if(grid[r][c].letter == ch) {
    				grid[r][c].matching = true;
    			}
    		}
    	}
    }

    public void checkWinners(Letter[][] grid) {
    	char ch;
    	boolean match;
    	int count = 0;
    	
    	for(int r = 0; r < grid.length; r++)
    	{
    		for(int c = 0; c < grid[r].length; c++)
    		{
    			ch = grid[r][c].letter; // get the letter of each new block (if its not a letter it gets 0)
    			match = grid[r][c].matching; // know if the letter matches one of the user's letters (or if it's a non-letter block)

    			if(!match) // found a non-letter block or a non-matching letter
    			{
    				if(count < 3 || ((c - count - 1) >= 0 && grid[r][c - count - 1].letter != 0)) // was not a winning word
    				{
    					count = 0;
    				}
    				else if(ch == 0)
    				{
    					if((c - count - 1) < 0 || grid[r][c - count - 1].letter == 0) // it found a winning word
    					{
	    					markWinner(r, c - 1, count);
							count = 0;
    					}
    					else
    					{
    						count = 0;
    					}
    				}
    				else // it found 3+ matching in a row, but wasn't a winning word
    				{
						//out.println("\nAlmost found a word - false alert. cnt:" + count + "  c:" + c + "  r:" + r);
						count = 0;
    				}
    			}
    			else // found a match, move on to next char
    			{
    				count++;
    			}
    		} // end inner for loop
    		
    		if(count >= 3 && (grid[r].length - count - 1 < 0 || grid[r][grid[r].length - count - 1].letter == 0)) // there was a winning word right before switching rows
    		{
    			markWinner(r, grid[r].length - 1, count);
    			count = 0;
    		}
    		else
    		{
    			//if(count >= 3)
    				//out.println("\nAlmost found a word - false alert. cnt:" + count + "  c:" + (grid[r].length - 1) + "  r:" + r);
				count = 0;
    		}
    		
    	} // end outer for loop

    } // end method
    
    public void markWinner(int r, int c, int count) {
    	String winningWord = "";
    	int t;
    	if (flipped) { //switch r and c's values
    		t = r;
    		r = c;
    		c = t;
    	}
    	
    	for(int i = count; i > 0; i--) {
    		winningWord = grid[r][c].letter + winningWord;
			winners[r][c] = true;
			if (flipped) r--;
			else c--;
		}
    	
    	if(flipped) r++;
    	else c++;
    	
    	/*if(!winningWords.contains(winningWord))*/ {
        	//out.println(flipped + " " + r + " " + c + " " + count);
        	wordInfos.add(new RowColCount(r, c, count, flipped));
        	out.println("'" + winningWord + "' IS A WINNER!");
    		winningWords.add(winningWord);
    		amtWinners++;
        	out.println(amtWinners + (amtWinners == 1? " winner!\n" : " winners!\n"));
    	}
    }
    
    
    static class RowColCount {
    	int row, col, count;
    	boolean vertical;
    	public RowColCount(int r, int c, int cnt, boolean v) {
    		row = r;
    		col = c;
    		count = cnt;
    		vertical = v;
    	}
    }
    
    @SuppressWarnings("serial")
	class MyJPanel extends JPanel implements KeyListener, MouseListener{

    	public void paintComponent(Graphics graphics) {
    		Graphics2D g = (Graphics2D) graphics;
    		
    		g.fillRect(0, 0, this.getWidth(), this.getHeight());
    		
    		// checkered background
    		/*int n = 0;
    		for(int r = 0; r < 29; r++) {
    			for( int c = 0; c < 27; c ++) {
    				g.setColor(n % 2 == 0? Color.blue : Color.cyan);
    				g.fillRect(c * 20,  r * 20,  20,  20);
    				n++;
    			}
    		}*/
    		
    		out.println(winningWords);
    		out.println(wordInfos);
			char letter;
			boolean matching, winner;
			int xLoc, yLoc;
    		for(int r = 0; r < grid.length; r++) {
    			for(int c = 0; c < grid[r].length; c++) {
    				xLoc = xShift + c * width;
    				yLoc = yShift + r * height;
    				letter = grid[r][c].letter;
					matching = grid[r][c].matching;
					winner = winners[r][c];

					if(letter == 0) {
						g.setColor(new Color(0, 90, 0));
					}
					/*else if(winner) {
						g.setColor(Color.red);
					}*/
					else if(matching) {
						g.setColor(new Color(255, 255, 235));
					}
					else {
						g.setColor(new Color(0, 235,0));
					}

    				g.fill3DRect(xLoc, yLoc, width - 2, height - 2, true);

    				if(letter != 0) {
    					g.setColor(new Color(0, 50, 0));
						g.setFont(new Font("TAHOMA",Font.BOLD,30));
    					g.drawString("" + grid[r][c].letter, xLoc + 8, yLoc + 30);
    				}

    			}
    		}

			g.setColor(Color.white);
			g.setFont(new Font("TAHOMA",Font.BOLD,17));
			try {
				input = inputSet.toString().replace(", ","").substring(1, inputSet.size() + 1);
				g.drawString("YOUR LETTERS: " + input + " (" + input.length() + ")", 50, 40);
			} catch (StringIndexOutOfBoundsException ex) {
				g.drawString("YOUR LETTERS: " + input + " (" + input.length() + ")", 50, 40);
				
			}
			
			// mark the winning words with a line or rectangle
			g.setColor( new Color(255, 0, 0) );
			for (RowColCount rcc : wordInfos) {
				// draw borders around the words
				/*g.drawRect(xShift + rcc.col * width - 2, yShift + rcc.row * height - 2,
						   rcc.vertical? width : width * rcc.count, rcc.vertical? height * rcc.count : height);
				g.drawRect(xShift + rcc.col * width - 1, yShift + rcc.row * height - 1,
						   rcc.vertical? width : width * rcc.count, rcc.vertical? height * rcc.count : height);*/
				
				// draw lines over them
				/*g.fillRect(xShift + rcc.col * width + width / 2 - 2, yShift + rcc.row * height + height / 2 - 2,
						   rcc.vertical? 3 : width * (rcc.count - 1) + 2, rcc.vertical? height * (rcc.count - 1) + 2: 3);*/
				
				// draw slightly different lines
				if(rcc.vertical){
					g.fillRect(xShift + (rcc.col*width) + (width/2) - 3, yShift + (rcc.row*height) + (height/3) - 2,
							3, (height) * (rcc.count-1) + (height/3) + 3);
				} else {
					g.fillRect(xShift + (rcc.col*width) + (width/3) - 2, yShift + (rcc.row*height) + (height/2) - 3,
							(width) * (rcc.count-1) + (width/3) + 3, 3);
				}
			}

			g.setColor(Color.white);
			g.drawString("Words found: " + amtWinners, 50, 520);
			
			String amtCash = "";
			
			if(amtWinners >= 3) {
				switch(amtWinners) {
					case 3: 
						amtCash = "$3!";
						break;
					case 4: 
						amtCash = "$5!";
						break;
					case 5:  
						amtCash = "$10!";
						break;
					case 6: 
						amtCash = "$20!";
						break;
					case 7:  
						amtCash = "$100!";
						break;
					case 8: 
						amtCash = "$500!";
						break;
					case 9:  
						amtCash = "$5,000!";
						break;
					case 10: 
						amtCash = "$50,000!";
						break;
					default:
						amtCash = "$50,000!";
				} // end switch
				g.setPaint(new GradientPaint(30, 515, Color.red, 420, 575, Color.yellow));
				g.setFont(new Font("TAHOMA", Font.BOLD, 25));
				g.drawString("Congratulations! You won " + amtCash, 50, 550);
			} // end if
			
			
    	}
    	
		public void keyPressed(KeyEvent event) {
			if(editMode) {
				if(event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					grid[editY][editX].letter = 0;
					repaint();
					out.println("backspace");
					if(editX > 0) editX--;
					else if(editY > 0) { editX = boardColumns - 1; editY--; }
					return;
				}
				
				if(event.getKeyChar() == 32)
					grid[editY][editX].letter = 0;
				else if(event.getKeyChar() >= 97 && event.getKeyChar() <= 122)
					grid[editY][editX].letter = (char) (event.getKeyChar() - 32);
				else if(event.getKeyChar() >= 65 && event.getKeyChar() <= 90)
					grid[editY][editX].letter = event.getKeyChar();
				
				out.println(event.getKeyChar() + " " + editX + " " + editY);
				editX++;
				if(editX >= boardColumns) {
					editX = 0;
					editY++;
					if(editY >= boardRows) {
						editY = 0;
						editMode = false;
					}
				}
			}
			repaint();
		}
		public void keyReleased(KeyEvent event) {}
		public void keyTyped(KeyEvent event) {}

		public void mouseClicked(MouseEvent event) {}
		public void mouseEntered(MouseEvent event) {}
		public void mouseExited(MouseEvent event) {}
		public void mousePressed(MouseEvent event) {
			int x = event.getX(), y = event.getY();
			if(isInBounds(xShift, yShift, xShift + boardColumns * width, yShift + boardRows * height, x, y)) {
				out.println(this.getWidth() + " " + this.getHeight() + " " + x + " " + y);
				this.requestFocus();
				editMode = true;
				editX = (event.getX() - xShift) / width;
				editY = (event.getY() - yShift) / height;
				out.println(editX + " " + editY);
			}
			else {
				this.requestFocus();
				editMode = false;
			}
		}
		public void mouseReleased(MouseEvent event) {}
		
		public boolean isInBounds(int x1, int y1, int x2, int y2, int x3, int y3) {
			return x3 <= x2 && x3 >= x1 && y3 <= y2 && y3 >= y1;
		}
    	
    
    }

    class NewButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JFileChooser fileChooser = new JFileChooser();
		}
    }
    
    class OpenButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JFileChooser fileChooser = new JFileChooser();
		}
    }

    class SaveButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			JFileChooser fileChooser = new JFileChooser();
		}
    }
}


class Letter {
	char letter;
	boolean matching;

	public Letter(char c, boolean b) {
		letter = c;
		matching = b;
	}

	public char getLetter() { return letter; }
	public boolean getMatching() { return matching; }

	public void setLetter(char c) { letter = c;}
	public void setMatching(boolean b) { matching = b; }

	public String toString() {
		return "[" + letter + "," + (matching? "t" : "f") + "]";
	}

	public static Letter[][] getSampleGridA() {
		char[][] grid = new char[][]
		{{  0,  'U',  0,  'P',  0,  'R',  0,   0,  'Y', 'E', 'T'},
		 { 'I', 'N', 'S', 'E', 'C', 'U', 'R', 'E',  0,   0,  'W'},
		 {  0,  'L',  0,  'D',  0,  'L',  0,   0,  'T', 'O', 'O'},
		 {  0,  'O',  0,  'E',  0,  'E',  0,  'G',  0,  'C',  0 },
		 {  0,  'A', 'S', 'S', 'I', 'S', 'T', 'A', 'N', 'T',  0 },
		 {  0,  'D',  0,  'T',  0,   0,   0,  'M',  0,  'A',  0 },
		 {  0,   0,   0,  'A',  0,   0,  'B', 'E', 'I', 'G', 'E'},
		 { 'S', 'H', 'E', 'L', 'V', 'E',  0,   0,   0,  'O',  0 },
		 { 'L',  0,  'L',  0,  'O',  0,  'O', 'X', 'E', 'N',  0 },
		 { 'I', 'T', 'S', 'E', 'L', 'F',  0,   0,  'B',  0,   0 },
		 { 'P',  0,  'E',  0,  'T',  0,  'O', 'R', 'B', 'I', 'T'}};

		 Letter[][] letterGrid = new Letter[grid.length][grid[0].length];

		 for(int r = 0; r < letterGrid.length; r++){
		 	for(int c = 0; c < letterGrid[r].length; c++){
		 		letterGrid[r][c] = new Letter(grid[r][c], false);
		 	}
		 }

		 return letterGrid;
	}
	
	public static Letter[][] getSampleGridB() {
		char[][] grid = new char[][]
		{{D,E,C,O,Y,0,A,N,T,I,C},
		 {U,0,L,0,0,0,P,0,0,0,O},
		 {C,0,E,0,P,0,T,0,0,0,P},
		 {T,R,A,D,I,T,I,O,N,0,P},
		 {0,0,R,0,T,0,T,0,U,S,E},
		 {S,K,I,0,0,0,U,0,M,0,R},
		 {T,0,N,0,0,0,D,Y,E,0,0},
		 {R,0,G,R,A,P,E,0,R,0,O},
		 {O,0,0,A,0,A,0,0,A,0,N},
		 {B,O,A,T,0,R,E,P,L,A,Y},
		 {E,0,0,E,0,T,0,0,0,0,X}};

		 Letter[][] letterGrid = new Letter[grid.length][grid[0].length];

		 for(int r = 0; r < letterGrid.length; r++){
		 	for(int c = 0; c < letterGrid[r].length; c++){
		 		letterGrid[r][c] = new Letter(grid[r][c], false);
		 	}
		 }

		 return letterGrid;
	}
	
	public static Letter[][] getSampleGridC() {
		 Letter[][] letterGrid = new Letter[11][11];
		 for(int r = 0; r < letterGrid.length; r++){
		 	for(int c = 0; c < letterGrid[r].length; c++){
		 		letterGrid[r][c] = new Letter(Math.random() < .6? 0 : (char) (Math.random() * 26 + 65), false);
		 	}
		 }
		 return letterGrid;
	}
	
	public static Letter[][] flip(Letter[][] grid) {
		Letter[][] temp = new Letter[grid.length][grid[0].length];
		for(int r = 0; r < grid.length; r++) {
			for(int c = 0; c < grid[r].length; c++) {
				temp[c][r] = grid[r][c];
			}
		}
		return temp;
	}
	
	/*
	 * Prints a matrix of Letters
	 */
	public static void quickPrint(Letter[][] grid) {
    	for(Letter[] array : grid) {
    		for(Letter let : array)
    			out.print(let + " ");
    		out.println();
    	}
		out.println();
	}
	
	public static final char
	A = 'A', B = 'B', C = 'C', D = 'D', E = 'E', F = 'F', G = 'G', H = 'H', I = 'I', 
    J = 'J', K = 'K', L = 'L', M = 'M', N = 'N', O = 'O', P = 'P', Q = 'Q', R = 'R', 
    S = 'S', T = 'T', U = 'U', V = 'V', W = 'W', X = 'X', Y = 'Y', Z = 'Z';
}