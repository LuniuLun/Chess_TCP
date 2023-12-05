package Run.server.GameLogic.Pieces;

import Run.server.GameLogic.Move;

/**
 * Cannon Piece
 */
public class Cannon extends Piece {
    public Cannon(Side side) {
        super(side);
        this.type = "Cannon";
        this.canWinAlone = false;
        // Corrected comparison
        if (side == Side.DOWN) {
            interactive = true; // Set interactive to true for DOWN side
        } else {
            interactive = false; // Set interactive to false for UP side
        }
        
    }
    public boolean isInteractive() {
        return interactive;
    }

    @Override

    public void checkPattern(Move move) {
        super.checkPattern(move);

        if (!(move.isHorizontal() || move.isVertical())) {
            move.setValid(false);
        }
    }
}
