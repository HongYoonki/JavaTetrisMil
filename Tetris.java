import java.awt.Color;
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

public class Tetris extends JFrame{
	
	private TetrisGame game;
	private int gameTime;
	private int bTime; // block time
	private int score;
	private int[] dropDur; // block duration
	private int blockSize;
	private boolean isGameOver;
	private boolean callNextBlock;
	private boolean hardDropped;
	private int level;
	
	private final int ROW = 20;
	private final int COL = 8;
	
	private int coord[] = {4, 1};
	private int bcoord[][] = new int[4][2];
	private Queue<Integer> blockQ;
	private int lastType;
	private int beforeType;
	
	private int marginX;
	private int marginY;
	
	private int[][] iBoard = {
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
			};
	private Tetris.BTYPE[][] board = new Tetris.BTYPE[22][10];
		
	
	private Tetris.BTYPE[][] tmpBoard = new Tetris.BTYPE[22][10];
	
	int[][][] coordsTable = new int[][][]{
	        {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}}, // S block
	        {{0, -1}, {0, 0}, {1, 0}, {1, 1}}, // Z block
	        {{0, -1}, {0, 0}, {0, 1}, {0, 2}}, // I block
	        {{-1, 0}, {0, 0}, {1, 0}, {0, 1}}, // T block
	        {{0, 0}, {0, 1}, {1, 0}, {1, 1}}, // O block
	        {{-1, -1}, {0, -1}, {0, 0}, {0, 1}}, // J block
	        {{1, -1}, {0, -1}, {0, 0}, {0, 1}} // L block
	};
	private Color[] colorTable = new Color[] {
			new Color(204, 153, 255),
			new Color(169, 209, 247),
			new Color(204, 153, 255),
			new Color(180, 240, 167),
			new Color(255, 255, 191),
			new Color(255, 223, 190),
			new Color(255, 177, 176)};
	
	int[][] rotateP = new int[][] {
		{0, -1},
		{1, 0}
	};
	
	public enum BTYPE{
		VOID,
		BRICK,
		MOVE,
		SHADOW,
		WALL
	}
	
	public Tetris(){
		
		this.gameTime = 0;
		this.bTime = 0;
		this.dropDur = new int[] {1000, 800, 600, 400, 200};
		this.blockSize = 20;
		this.blockQ = new LinkedList<>();
		this.isGameOver = false;
		this.level = 0;
		this.callNextBlock = false;
		this.marginX = this.marginY = 20;
		this.hardDropped = false;
		
		List<Integer> list = new ArrayList<>();
		for(int i = 0 ; i < 7 ; i++)list.add(i);
		Collections.shuffle(list);
		for(int i = 0 ; i < 7 ; i++)
			this.blockQ.offer(list.get(i));
		this.lastType = blockQ.peek();
		int qt = blockQ.poll();
		for(int i = 0 ; i < 4 ; i++) {
			this.bcoord[i][0] = this.coordsTable[qt][i][0];
			this.bcoord[i][1] = this.coordsTable[qt][i][1];
			System.out.println("()"+this.bcoord[i][0]+", "+this.bcoord[i][1]);
		}
		
		
		setSize(600, 500);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game = new TetrisGame();
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
				switch(e.getKeyCode()) {
				case KeyEvent.VK_UP:
					rotate();
					break;
				case KeyEvent.VK_DOWN:
					if(canMoveDown() && !drop()) // modify bugs that bricking when push the down button
						callNextBlock = true;
					break;
				case KeyEvent.VK_LEFT:
					moveHorizontal(false);
					break;
				case KeyEvent.VK_RIGHT:
					moveHorizontal(true);
					break;
				case KeyEvent.VK_SPACE:
					hardDrop();
					break;
				} 
			}
		});
		
		
		// Thread that blocks down
		(new Thread() {
			@Override
			public void run() {
				System.out.println("Enter Thread");
				try {
					Thread.sleep(dropDur[level]);
					while(!isGameOver) {
						Thread.sleep(dropDur[level]);
						if(!drop()) {
							System.out.println("222222222222");
							int l = checkLine();
							score += l*1000;
							//nextBlock();
							callNextBlock = false;
						}
					}
				}catch(Exception e) {
					
				}
			}
		}).run();
	}
	
	public void nextBlock() {
		coord = new int[] {4, 1};
		beforeType = lastType;
		lastType = blockQ.peek();
		bcoord = coordsTable[blockQ.poll()].clone();
		blockQ.offer(getRandomBlock());
		System.out.println(blockQ.toString());
	}
	
	public boolean drop() {
		System.out.println("drop() called");
		if(canMoveDown()) {
			delBlock();
			coord[1]++;
			setBlock();
			System.out.println("coord is: ("+coord[0]+", "+coord[1]+")");
		}else {
			solidify();
			System.out.println("solidify() called");
			if(hardDropped) {
				hardDropped = false;
				return false;
			}
			nextBlock();
			return false;
		}
		return true;
	}
	
	public void hardDrop() {
		callNextBlock = true;
		hardDropped = true;
		while(drop());
		nextBlock();
	}
	
	/**
	 * check can move to next state with block coordinate 
	 * 
	 */
	public boolean canMoveDown() {
		for(int i = 0 ; i < 4 ; i++) {
			BTYPE t = board[coord[1]+1+bcoord[i][1]][coord[0]+bcoord[i][0]];
			if(t == BTYPE.BRICK || t == BTYPE.WALL)
				return false;
		}
		
		return true;
	}
	public void moveHorizontal(boolean rightside) {
		if(canMoveHorizontal(rightside)){
			delBlock();
			coord[0] += rightside ? 1 : -1;
			setBlock();
		}
	}
	public boolean canMoveHorizontal(boolean rightside) {
		int off = rightside ? 1 : -1;
		for(int i = 0 ; i < 4 ; i++) {
			BTYPE t = board[coord[1]+bcoord[i][1]][coord[0]+off+bcoord[i][0]];
			if(t == BTYPE.BRICK || t == BTYPE.WALL)
				return false;
		}
		return true;
	}
	public boolean canRotate(boolean clockwise) {
		if(clockwise) {
			for(int i = 0 ; i < 4 ; i++) {
				int[] off = rotateCoord(new int[] {bcoord[i][0], bcoord[i][1]});
				BTYPE t = board[coord[1] + off[1]][coord[0] + off[0]];
				if(t == BTYPE.BRICK || t == BTYPE.WALL)
					return false;
			}
		}
		return true;
	}
	
	public void solidify() {
		for(int i = 0 ; i < 4 ; i++) 
			board[coord[1]+bcoord[i][1]][coord[0]+bcoord[i][0]] = BTYPE.BRICK;
	}
	
	public int getRandomBlock() {
		return (int)Math.floor(Math.random()*7);
	}
	/**
	 * rotateCoord
	 * @param c that is {x, y}
	 * @return calculated value
	 */
	public int[] rotateCoord(int[] c){
		int[] result = new int[2];
		result[0] = c[1]*-1;
		result[1] = c[0]*1;
		return result;
	}
	
	public void rotate() {
		delBlock();
		if(canRotate(true))
			for(int i = 0 ; i < 4 ; i++)
				bcoord[i] = rotateCoord(bcoord[i]).clone();
		setBlock();
	}
	
	public void setBlock() {
		for(int i = 0 ; i < 4 ; i++)
			board[coord[1]+bcoord[i][1]][coord[0]+bcoord[i][0]] = BTYPE.MOVE;
	}
	
	public void delBlock() {
		for(int i = 0 ; i < 4 ; i++)
			board[coord[1]+bcoord[i][1]][coord[0]+bcoord[i][0]] = BTYPE.VOID;
	}
	
	public int checkLine() {
		int line = 0;
		for(int i = 0 ; i < ROW  ; i++) {
			boolean isLine = true;
			for(int j = 1 ; j < COL + 1; j++) {
				if(board[i][j] == BTYPE.VOID) {
					isLine = false;
					break;
				}
			}
			if(isLine) {
				for(int j = 1 ; j < COL + 1; j++) {
					board[i][j] = BTYPE.VOID;
				}
				for(int ii = i ; ii >= 2 ; ii--) {
					for(int jj = 1 ; jj < COL + 1 ; jj++) {
						try {
							if(board[ii-1][jj] == BTYPE.VOID || board[ii-1][jj] == BTYPE.BRICK)
								board[ii][jj] = board[ii - 1][jj];
						}catch(ArrayIndexOutOfBoundsException e) {
							if(board[ii][jj]!=BTYPE.MOVE)
								board[ii][jj] = BTYPE.VOID;
						}
					}
				}
				line++;
			}
		}
		game.repaint();
		System.out.println("line clear! ==================");
		return line;
	}
	class TetrisGame extends JPanel{
		
		int[] offsetxy;
		int[][] offsetCoord = new int[4][2];
		
		public TetrisGame() {
			for(int i = 0 ; i < ROW + 1; i++) 
				for(int j = 0 ; j < COL + 2 ; j++){
					board[i][j] = iBoard[i][j] == 1 ? BTYPE.WALL : BTYPE.VOID;
					tmpBoard[i][j] = BTYPE.VOID;
				}
		}

		@Override
		public void paintComponent(Graphics g) {
			g.setColor(getBackground());
			g.fillRect(marginX + (COL*2+2)*blockSize, marginY + 0, 100, 100);
			g.setColor(Color.black);
			g.drawString("Score: "+score, marginX + (COL*2+2)*blockSize, marginY + blockSize*1);
			for(int y = 0 ; y < ROW + 1; y++) {
				for(int x = 0 ; x < COL + 2 ; x++) {
					if(tmpBoard[y][x] != board[y][x]) {
						tmpBoard[y][x] = board[y][x];
						BTYPE s = board[y][x];
						switch(s) {
						case VOID:
							g.setColor(getBackground());
							g.fillRect(marginX + x*blockSize, marginY + y*blockSize, blockSize, blockSize);
							break;
						case BRICK:
							g.setColor(Color.gray);
							g.fillRect(marginX + x*blockSize+1, marginY + y*blockSize+1, blockSize-2, blockSize-2);
							break;
						case MOVE:
							g.setColor(colorTable[lastType]);
							g.fillRect(marginX + x*blockSize+1, marginY + y*blockSize+1, blockSize-2, blockSize-2);
							break;
						case WALL:
							g.setColor(Color.black);
							g.fillRect(marginX + x*blockSize, marginY + y*blockSize, blockSize, blockSize);
							break;
						case SHADOW:
							g.setColor(Color.LIGHT_GRAY);
							g.drawRect(marginX + x*blockSize+1, marginY + y*blockSize+1, blockSize-2, blockSize-2);
						}
					}
				}
			}

			g.setColor(getBackground());
			g.fillRect(
					marginX + (COL+2)*blockSize + blockSize*3 + 1 + -1*blockSize,
					marginY + blockSize*3 + 1 + -1*blockSize,
					blockSize*4-2,
					blockSize*4-2);
			for(int i = 0 ; i < 4 ; i++) {
				int t = blockQ.peek();
				g.setColor(colorTable[t]);
				g.fillRect(
						marginX + (COL+2)*blockSize + blockSize*3 + 1 + coordsTable[t][i][0]*blockSize,
						marginY + blockSize*3 + 1 + coordsTable[t][i][1]*blockSize,
						blockSize-2,
						blockSize-2);
			}
			repaint();
		}
	}
	
	static public void main(String[] args) {
		new Tetris();
		
		return;
	}
}
