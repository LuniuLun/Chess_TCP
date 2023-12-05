package Run.server.GameLogic.Pieces;

import Run.server.GameLogic.Move;

/**
 * Chariot Piece
 */
public class Chariot extends Piece{

    public Chariot(Side side) {
        super(side);
        this.type = "Chariot";
        this.canWinAlone = true;
        // Corrected comparison
        if (side == Side.DOWN) {
            interactive = true; // Set interactive to true for DOWN side
        } else {
            interactive = false; // Set interactive to false for UP side
        }
        
    }

    @Override

    public void checkPattern(Move move) {
        super.checkPattern(move);

        if (!move.isHorizontal() && !move.isVertical()) {
            move.setValid(false);
        }
    }
}
