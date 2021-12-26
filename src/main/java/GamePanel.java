import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
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
    int mouseRow = -1, mouseCol = -1;
    int delayMillis = 10;

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

        boolean runGame = true;
        print();
        while (runGame) {
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mouseHandler.leftMouseClicked) {
                mouseRow = mouseHandler.mouseY / scale / pixelSize;
                mouseCol = mouseHandler.mouseX / scale / pixelSize;
            }
            if (mouseRow >= 0 && mouseCol >= 0 && !mouseHandler.leftMouseClicked) {
                Tile clickedTile = board[mouseRow][mouseCol];
                if (clickedTile.isBomb) runGame = false;
                mouseRow = -1;
                mouseCol = -1;
            }
        }
        System.out.println("GAME OVER");
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage image = null;

        if (board == null) return;
        for (int i = 0; i < Game.HEIGHT; i++) {
            for (int j = 0; j < Game.WIDTH; j++) {
                if (!board[i][j].revealed) {
                    try {
                        image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/gameRes/tile.png")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    g2.drawImage(image, j * scale * pixelSize, i * scale * pixelSize, scale * pixelSize, scale * pixelSize, null);
                }
            }
        }

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
                else System.out.print(currTile.findBombsNear() + " ");
            }
            System.out.print("\n");
        }
    }
}
