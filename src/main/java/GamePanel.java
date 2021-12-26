import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {
    private final int pixelSize = 16;
    private final int scale = 4;
    private final int screenWidth = scale * pixelSize * Game.WIDTH;
    private final int screenHeight = scale * pixelSize * Game.HEIGHT;

    MouseHandler mouseHandler = new MouseHandler();
    boolean lastLeftMouseState, lastRightMouseState;

    Thread gameThread;

    private final int numBombs = (Game.WIDTH * Game.HEIGHT) / 5;
    public static Tile[][] board;
    int mouseRow = -1, mouseCol = -1;
    int revealedTiles = 0;
    boolean foundBomb = false, completedBoard = false;

    final Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA, Color.MAGENTA};

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
        while (!foundBomb && !completedBoard) {
            lastLeftMouseState = mouseHandler.leftMouseClicked;
            lastRightMouseState = mouseHandler.rightMouseClicked;
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } //TODO: Find out why having a 0 Sleep Time makes the MouseListener work
            if (mouseHandler.leftMouseClicked || mouseHandler.rightMouseClicked) {
                mouseRow = mouseHandler.mouseY / scale / pixelSize;
                mouseCol = mouseHandler.mouseX / scale / pixelSize;
            }
            if (mouseRow >= 0 && mouseCol >= 0 && lastLeftMouseState && !mouseHandler.leftMouseClicked) {
                Tile clickedTile = board[mouseRow][mouseCol];
                if (!clickedTile.flagged && !clickedTile.revealed) {
                    clickedTile.reveal();
                    revealedTiles++;
                    if (clickedTile.findBombsNear() == 0) revealAdjacentEmptyTiles(mouseRow, mouseCol);
                    if (clickedTile.isBomb) foundBomb = true;
                }
                mouseRow = -1;
                mouseCol = -1;
                repaint();
            }

            if (mouseRow >= 0 && mouseCol >= 0 && !lastRightMouseState && mouseHandler.rightMouseClicked) {
                Tile clickedTile = board[mouseRow][mouseCol];
                clickedTile.changeFlagState();
                mouseRow = -1;
                mouseCol = -1;
                repaint();
            }
            completedBoard = (revealedTiles == (Game.HEIGHT * Game.WIDTH) - numBombs);
        }
        if (foundBomb) {
            System.out.println("GAME OVER!");
        } else {
            System.out.println("YOU WIN!");
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage image;

        if (board == null) return;


        for (int i = 0; i < Game.HEIGHT; i++) {
            for (int j = 0; j < Game.WIDTH; j++) {
                Tile currTile = board[i][j];
                image = getImage("/gameRes/tile.png");
                g2.drawImage(image, j * scale * pixelSize, i * scale * pixelSize, scale * pixelSize, scale * pixelSize, null);
                if (currTile.flagged) {
                    image = getImage("/gameRes/flag.png");
                    g2.drawImage(image, j * scale * pixelSize, i * scale * pixelSize, scale * pixelSize, scale * pixelSize, null);
                } else if (currTile.revealed) {
                    image = getImage("/gameRes/revealedTile.png");
                    g2.drawImage(image, j * scale * pixelSize, i * scale * pixelSize, scale * pixelSize, scale * pixelSize, null);
                }

            }
        }
        //TODO: Implement pressedTile.png

        if (foundBomb) {
            image = getImage("/gameRes/bomb.png");
            for (int i = 0; i < Game.HEIGHT; i++) {
                for (int j = 0; j < Game.WIDTH; j++) {
                    if (board[i][j].isBomb && !board[i][j].flagged) {
                        g2.drawImage(image, j * scale * pixelSize, i * scale * pixelSize, scale * pixelSize, scale * pixelSize, null);
                    }
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
        while (bombCtr < numBombs) {
            Random random = new Random();
            int randRow = random.nextInt(Game.HEIGHT);
            int randCol = random.nextInt(Game.WIDTH);
            if (!board[randRow][randCol].isBomb) {
                board[randRow][randCol].setAsBomb();
                bombCtr++;
            }
        }
    }

    public void revealAdjacentEmptyTiles(int row, int col) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (checkCoordsExist(row + i, col + j)) {
                    Tile currTile = board[row + i][col + j];
                    if (!currTile.revealed && !currTile.isBomb) {
                        currTile.reveal();
                        revealedTiles++;
                        if (currTile.findBombsNear() == 0) revealAdjacentEmptyTiles(row + i, col + j);
                    }
                }
            }
        }
    }

    public boolean checkCoordsExist(int row, int col) {
        return (row >= 0 && row < Game.HEIGHT && col >= 0 && col < Game.WIDTH);
    }


    public BufferedImage getImage(String resPath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(resPath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
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
