package GameLogic.Pieces;

import GameLogic.Move;

/**
 * General Piece
 */
public class General extends Piece {
    public General(Side side) {
        super(side);

        this.type = "General";
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

        if (!move.isHorizontal() && !move.isVertical()) {
            move.setValid(false);
        }
        if (Math.abs(move.getDx()) > 1 || Math.abs(move.getDy()) > 1) {
            move.setValid(false);
        }

        //stays in generals quarters
        if (move.getFinalX() < 3 || move.getFinalX() > 5) {
            move.setValid(false);
        }

        if (side == Side.UP) {
            if (move.getFinalY() > 2) {
                move.setValid(false);
            }
        }
        if (side == Side.DOWN) {
            if (move.getFinalY() < 7) {
                move.setValid(false);
            }
        }


    }
}
