public class Tile {

    public int row, col;
    public boolean revealed, flagged, isBomb;
    public int bombsNear;

    public Tile(int inRow, int inCol, boolean inIsBomb) {
        revealed = false;
        flagged = false;
        row = inRow; col = inCol;
        isBomb = inIsBomb;
    }


    public void reveal() {
        revealed = true;
        bombsNear = findBombsNear();
    }

    public void changeFlagState() {flagged = !flagged;}

    public void setAsBomb() {
        isBomb = true;
    }


    public int findBombsNear() {
        int bombCtr = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (checkCoordsExist(row + i, col + j)) {
                    if (GamePanel.board[row + i][col + j].isBomb) bombCtr++;
                }
            }
        }
        return bombCtr;
    }

    private boolean checkCoordsExist(int row, int col) {
        return (row >= 0 && row < Game.HEIGHT && col >= 0 && col < Game.WIDTH);
    }
}
