import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {
    private final int pixelSize = 16;
    private final int scale = 2;
    private final int screenWidth = scale * pixelSize * Game.WIDTH;
    private final int screenHeight = scale * pixelSize * Game.HEIGHT;

    MouseHandler mouseHandler = new MouseHandler();

    Thread gameThread;

    private final int numBombs = (Game.WIDTH * Game.HEIGHT) / 5;
    public static Tile[][] board;

    final Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA};

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addMouseListener(mouseHandler);
        this.setFocusable(true);
    }

    public void startGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        initBoard();
        print();
    }

    public void initBoard() {
        board = new Tile[Game.HEIGHT][Game.WIDTH];
        for (int i = 0; i < Game.HEIGHT; i++) {
            for (int j = 0; j < Game.WIDTH; j++) {
                board[i][j] = new Tile(i, j, false);
            }
        }

        int bombCtr = 0;
        while (bombCtr <= numBombs) {
            Random random = new Random();
            int randRow = random.nextInt(Game.HEIGHT);
            int randCol = random.nextInt(Game.WIDTH);
            if (!board[randRow][randCol].isBomb) {
                board[randRow][randCol].setAsBomb();
                bombCtr++;
            }
        }
    }

    public void print() {
        for (int i = 0; i < Game.HEIGHT; i++) {
            for (int j = 0; j < Game.WIDTH; j++) {
                Tile currTile = board[i][j];
                if (currTile.isBomb) System.out.print("-1 ");
                else System.out.print(currTile.reveal() + " ");
            }
            System.out.print("\n");
        }
    }
}
