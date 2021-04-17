import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Tetris extends JFrame {

	private final int T_WIDTH = 600;
	private final int T_HEIGHT = 550;
	
	private TetrisGame game;
	private int gameTime;
	private int bTime; // block time
	private int score;
	private int[] dropDur; // block duration
	private int blockSize;
	private boolean isGameOver;
	private boolean hardDropped;
	private int level;
	private final int deadLine = 4;
	private boolean restart = false;
	private boolean exit = false;
	private boolean canHold;
	private int holdBlock;

	private final int ROW = 22;
	private final int COL = 8;

	private int coord[] = { 4, 1 };
	private int bcoord[][] = new int[4][2];
	private Queue<Integer> blockQ;
	private int lastType;
	private int beforeType;
	private Thread gameThread;

	private int marginX;
	private int marginY;

	private int[][] iBoard = { { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };
	private Tetris.BTYPE[][] board = new Tetris.BTYPE[ROW+1][COL+2];

	private Tetris.BTYPE[][] tmpBoard = new Tetris.BTYPE[ROW+1][COL+2];

	int[][][] coordsTable = new int[][][] { { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, // S block
			{ { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }, // Z block
			{ { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, // I block
			{ { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, 1 } }, // T block
			{ { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 1 } }, // O block
			{ { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, // J block
			{ { 1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } } // L block
	};
	private Color[] colorTable = new Color[] { 
			new Color(102, 242, 139), // S green
			new Color(255, 112, 155), // Z red
			new Color(105, 237, 255), // I sky blue
			new Color(186, 102, 255), // T purple
			new Color(244, 250, 137), // O yellow
			new Color(105, 105, 255), // J navy
			new Color(255, 219, 102)  // L orange
			};

	int[][] rotateP = new int[][] { { 0, -1 }, { 1, 0 } };

	public enum BTYPE {
		VOID, BRICK, MOVE, SHADOW, WALL
	}

	public Tetris() {
		game = new TetrisGame();
		init();
		setSize(T_WIDTH, T_HEIGHT);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(game);
		setFocusable(true);
		setTitle("Tetris");
		addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					rotate();
					break;
				case KeyEvent.VK_DOWN:
					if(canMoveDown())
						drop();
					break;
				case KeyEvent.VK_LEFT:
					moveHorizontal(false);
					break;
				case KeyEvent.VK_RIGHT:
					moveHorizontal(true);
					break;
				case KeyEvent.VK_Z:
					hold();
					break;
				case KeyEvent.VK_SPACE:
					hardDrop();
					break;
				case KeyEvent.VK_ENTER:
					if(isGameOver) {
						restart = true;
						init();
					}
					break;
				case KeyEvent.VK_ESCAPE:
					exit = true;
					break;
				}
			}
		});

		// Thread that blocks down
		gameThread = new Thread() {
			@Override
			public void run() {
				System.out.println("Enter Thread");
				try {
					Thread.sleep(dropDur[level]);
					while(!exit){
						while (!isGameOver) {
							Thread.sleep(dropDur[level]);
							boolean d = drop();
							if (!d) {
								updateScore();
								// nextBlock();
							}
						}
						while(isGameOver) {};
					}
				} catch (Exception e) {

				}
			}
		};
		gameThread.run();
		
		
	}
	private void init() {

		this.gameTime = 0;
		this.bTime = 0;
		this.dropDur = new int[] { 1000, 800, 600, 400, 200 };
		this.blockSize = 20;
		this.blockQ = new LinkedList<>();
		this.isGameOver = false;
		this.level = 0;
		this.marginX = this.marginY = 20;
		this.hardDropped = false;
		this.holdBlock = -1;
		this.canHold = true;
		
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < 7; i++)
			list.add(i);
		Collections.shuffle(list);
		for (int i = 0; i < 7; i++)
			this.blockQ.offer(list.get(i));
		this.lastType = blockQ.poll();
		for (int i = 0; i < 4; i++) {
			this.bcoord[i][0] = this.coordsTable[lastType][i][0];
			this.bcoord[i][1] = this.coordsTable[lastType][i][1];
			System.out.println("()" + this.bcoord[i][0] + ", " + this.bcoord[i][1]);
		}
		game.init();
		game.repaint();
	}
	public void nextBlock() {
		coord = new int[] { 4, 1 };
		beforeType = lastType;
		lastType = blockQ.poll();
		bcoord = coordsTable[lastType].clone(); // blockQ.poll()
		blockQ.offer(getRandomBlock());
		checkCollision();
		delBlock();
		setBlock();
		System.out.println(blockQ.toString());
		canHold = true;
	}

	public boolean drop() {
		System.out.println("drop() called");
		if (canMoveDown()) {
			delBlock();
			coord[1]++;
			setBlock();
			System.out.println("coord is: (" + coord[0] + ", " + coord[1] + ")");
		} else {
			solidify();
			System.out.println("solidify() called");
			if (hardDropped) {
				hardDropped = false;
				return false;
			}
			nextBlock();
			return false;
		}
		return true;
	}

	public void hardDrop() {
		hardDropped = true;
		while (drop());
		nextBlock();
		updateScore();
	}
	
	public void hold() {
		if(!canHold || lastType == holdBlock)
			return;
		this.canHold = false;
		if(holdBlock < 0) {
			holdBlock = beforeType;
			delBlock();
			nextBlock();
			return;
		}
		delBlock();
		coord = new int[] {4,1};
		bcoord = coordsTable[holdBlock].clone();
		int t = holdBlock;
		holdBlock = lastType;
		lastType = t;
		checkCollision();
		delBlock();
		setBlock();
	}

	/**
	 * check can move to next state with block coordinate
	 * 
	 */
	public boolean canMoveDown() {
		for (int i = 0; i < 4; i++) {
			BTYPE t = board[coord[1] + 1 + bcoord[i][1]][coord[0] + bcoord[i][0]];
			if (t == BTYPE.BRICK || t == BTYPE.WALL)
				return false;
		}

		return true;
	}

	public void moveHorizontal(boolean rightside) {
		if (canMoveHorizontal(rightside)) {
			delBlock();
			coord[0] += rightside ? 1 : -1;
			setBlock();
		}
	}

	public boolean canMoveHorizontal(boolean rightside) {
		int off = rightside ? 1 : -1;
		for (int i = 0; i < 4; i++) {
			BTYPE t = board[coord[1] + bcoord[i][1]][coord[0] + off + bcoord[i][0]];
			if (t == BTYPE.BRICK || t == BTYPE.WALL)
				return false;
		}
		return true;
	}

	public boolean canRotate(boolean clockwise) {
		if (clockwise) {
			for (int i = 0; i < 4; i++) {
				int[] off = rotateCoord(new int[] { bcoord[i][0], bcoord[i][1] });
				try {
					BTYPE t = board[coord[1] + off[1]][coord[0] + off[0]];	
					if (t == BTYPE.BRICK || t == BTYPE.WALL)
						return false;				
				}catch(Exception e) {
					System.out.println(String.format("Error: coord{%d, %d}, off{%d, %d}", coord[0], coord[1], off[0], off[1]));
					return false;
				}
			}
		}
		return true;
	}

	public void solidify() {
		for (int i = 0; i < 4; i++)
			board[coord[1] + bcoord[i][1]][coord[0] + bcoord[i][0]] = BTYPE.BRICK;
	}

	public int getRandomBlock() {
		return (int) Math.floor(Math.random() * 7);
	}
	
	public boolean checkCollision() {
		for(int i = 0 ; i > 4 ; i++)
			if(board[coord[1] + bcoord[i][1]][coord[0] + bcoord[i][0]] == BTYPE.BRICK) {
				isGameOver = true;
				return true;
			}
		return false;
	}
	/**
	 * rotateCoord
	 * 
	 * @param c that is {x, y}
	 * @return calculated value
	 */
	public int[] rotateCoord(int[] c) {
		int[] result = new int[2];
		result[0] = c[1] * -1;
		result[1] = c[0] * 1;
		return result;
	}

	public void rotate() {
		delBlock();
		if (canRotate(true))
			for (int i = 0; i < 4; i++)
				bcoord[i] = rotateCoord(bcoord[i]).clone();
		setBlock();
	}

	public void setBlock() {
		for (int i = 0; i < 4; i++) 
			board[coord[1] + bcoord[i][1]][coord[0] + bcoord[i][0]] = BTYPE.MOVE;
	}

	public void delBlock() {
		for (int i = 0; i < 4; i++)
			board[coord[1] + bcoord[i][1]][coord[0] + bcoord[i][0]] = BTYPE.VOID;
	}

	public int checkLine() {
		int line = 0;
		for (int i = 0; i < ROW; i++) {
			boolean isLine = true;
			for (int j = 1; j < COL + 1; j++) {
				if (board[i][j] == BTYPE.VOID) {
					isLine = false;
					break;
				}
			}
			if (isLine) {
				for (int j = 1; j < COL + 1; j++) {
					board[i][j] = BTYPE.VOID;
				}
				for (int ii = i; ii >= 2; ii--) {
					for (int jj = 1; jj < COL + 1; jj++) {
						try {
							if (board[ii - 1][jj] == BTYPE.VOID || board[ii - 1][jj] == BTYPE.BRICK)
								board[ii][jj] = board[ii - 1][jj];
						} catch (ArrayIndexOutOfBoundsException e) {
							if (board[ii][jj] != BTYPE.MOVE)
								board[ii][jj] = BTYPE.VOID;
						}
					}
				}
				line++;
			}
		}
		for(int i = 1 ; i < COL + 1; i++)
			if(board[deadLine][i] == BTYPE.BRICK)
				isGameOver = true;
		if(!isGameOver)
			game.repaint();
		return line;
	}
	
	public void updateScore() {
		score += checkLine()*1000;
	}

	class TetrisGame extends JPanel {

		int[] offsetxy;
		int[][] offsetCoord = new int[4][2];

		public TetrisGame() {
			init();
		}
		
		public void init() {
			for (int i = 0; i < iBoard.length; i++)
				for (int j = 0; j < iBoard[0].length; j++) {
					board[i][j] = iBoard[i][j] == 1 ? BTYPE.WALL : BTYPE.VOID;
					tmpBoard[i][j] = BTYPE.VOID;
				}
		}

		@Override
		public void paintComponent(Graphics g) {
			// clear board when restart
			if(restart) {
				g.clearRect(0, 0, T_WIDTH, T_HEIGHT);
				restart = false;
				g.setColor(getBackground());
				g.fillRect(0, 0, T_WIDTH, T_HEIGHT);
			}
			
			g.setColor(getBackground());
			g.fillRect(marginX + (COL * 2 + 2) * blockSize, marginY + 0, 100, 100);
			g.setColor(Color.black);
			g.drawString("Score: " + score, marginX + (COL * 2 + 2) * blockSize, marginY + blockSize * 1);
			
			// draw dead line
			g.drawLine(marginX + blockSize, marginY + blockSize*(deadLine+1), marginX + blockSize*9, marginY + blockSize*(deadLine+1));
			
			for (int y = 0; y < ROW + 1; y++) {
				for (int x = 0; x < COL + 2; x++) {
					if (tmpBoard[y][x] != board[y][x] || y < 5) {
						tmpBoard[y][x] = board[y][x];
						BTYPE s = board[y][x];
						switch (s) {
						case VOID:
							g.setColor(getBackground());
							g.fillRect(marginX + x * blockSize, marginY + y * blockSize, blockSize, blockSize);
							break;
						case BRICK:
							g.setColor(Color.gray);
							g.fillRect(marginX + x * blockSize + 1, marginY + y * blockSize + 1, blockSize - 2,
									blockSize - 2);
							break;
						case MOVE:
							g.setColor(colorTable[lastType]);
							g.fillRect(marginX + x * blockSize + 1, marginY + y * blockSize + 1, blockSize - 2,
									blockSize - 2);
							break;
						case WALL:
							g.setColor(Color.black);
							g.fillRect(marginX + x * blockSize, marginY + y * blockSize, blockSize, blockSize);
							break;
						case SHADOW:
							g.setColor(Color.LIGHT_GRAY);
							g.drawRect(marginX + x * blockSize + 1, marginY + y * blockSize + 1, blockSize - 2,
									blockSize - 2);
						}
					}
				}
			}
			//draw next block and hold block
			g.setColor(getBackground());
			g.fillRect(marginX + (COL + 4) * blockSize,
					marginY + blockSize * 2,
					blockSize * 4 - 2,
					(blockSize * 4 - 2)*3);
			g.setColor(Color.black);
			g.drawString("next block", marginX + (COL + 4) * blockSize,
					marginY + blockSize * 2 - 10);
			g.drawString("hold block (Key: Z)", marginX + (COL + 4) * blockSize,
					marginY + blockSize * 7 - 10);
			for (int i = 0; i < 4; i++) {
				int t = blockQ.peek();
				g.setColor(colorTable[t]);
				g.fillRect(marginX + (COL + 2) * blockSize + blockSize * 3 + 1 + coordsTable[t][i][0] * blockSize,
						marginY + blockSize * 3 + 1 + coordsTable[t][i][1] * blockSize, blockSize - 2, blockSize - 2);
				

				t = holdBlock;
				if(t >= 0) {
					g.setColor(colorTable[t]);
					g.fillRect(marginX + (COL + 2) * blockSize + blockSize * 3 + 1 + coordsTable[t][i][0] * blockSize,
							marginY + blockSize * 8 + 1 + coordsTable[t][i][1] * blockSize,
							blockSize - 2,
							blockSize - 2);					
				}
			}
			if(!isGameOver)
				repaint();
			else {
				g.setColor(new Color(.0f, .0f, .0f, .5f));
				g.fillRect(0, 0, T_WIDTH, T_HEIGHT);
				g.setColor(Color.white);
				int fsize = 30;
				g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fsize));
				g.drawString("GAME OVER", (int)(T_WIDTH/2 - 3.5*fsize), T_HEIGHT/2 - fsize);
				String tmpS = "socre: "+score;
				g.drawString(tmpS, (int)(T_WIDTH/2 - tmpS.length()/2*fsize), T_HEIGHT/2);
				g.drawString("Press Enter to restart", (int)(T_WIDTH/2 - 5.5*fsize), T_HEIGHT/2 + fsize);
			}
		}
	}

	static public void main(String[] args) {
		new Tetris();
		
		return;
	}
}
