package GameLogic.Pieces;

import GameLogic.Move;

/**
 * Elephant Piece
 */
public class Elephant extends Piece {
    public Elephant(Side side) {
        super(side);
        this.type = "Elephant";
        this.canWinAlone = false;
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

        if (!move.isDiagonal()) {
            move.setValid(false);
        }
        if (Math.abs(move.getDx()) != 2) {
            move.setValid(false);
        }

        //river crossing prevention
        if (side == Side.UP) {
            if (move.getFinalY() > 4) {
                move.setValid(false);
            }
        }
        if (side == Side.DOWN) {
            if (move.getFinalY() < 5) {
                move.setValid(false);
            }
        }

    }
}
