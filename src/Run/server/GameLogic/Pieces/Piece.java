package Run.server.GameLogic.Pieces;

import Run.server.GameLogic.Move;


public abstract class Piece {
    protected String type;
    protected boolean canWinAlone;
    protected Side side;
    private boolean captured;

    public Piece(Side side) {
        this.side = side;
        this.captured = false;
    }
    public void checkPattern(Move move) {
        move.setValid(true);
    }

    public Side getSide() {
        return side;
    }
    public void Capture() {
        this.captured = true;
    }

    public String toString() {

        return this.type;
    }
    public String getImageName() {
        String fileName = "";
        if (side == Piece.Side.UP)
            fileName += "black_";
        else
            fileName += "red_";

        if (type.equals("Soldier"))
            fileName += "soldier";
        else if (type.equals("General"))
            fileName += "general";
        else if (type.equals("Cannon"))
            fileName += "cannon";
        else if (type.equals("Horse"))
            fileName += "horse";
        else if (type.equals("Elephant"))
            fileName += "elephant";
        else if (type.equals("Guard"))
            fileName += "guard";
        else if (type.equals("Chariot"))
            fileName += "chariot";

        fileName += ".png";
        return fileName;
    }
    public boolean canWinAlone() {
        return canWinAlone;
    }
    public enum Side {
        UP,
        DOWN
    }

}
