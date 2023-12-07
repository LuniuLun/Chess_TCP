package GameLogic;

import GameLogic.Pieces.*;

public class Board {

    // test

    public static final int PLAYER1_WINS = 1;
    public static final int PLAYER2_WINS = 2;
    public static final int PLAYER1_TIMEOUT_WIN = 3;
    public static final int PLAYER2_TIMEOUT_WIN = 4;
    public static final int DRAW = 0;
    public static final int NA = -1;

    private Point[][] gBoard;
    private int upGeneralX = 4;
    private int upGeneralY = 0;
    private int downGeneralX = 4;
    private int downGeneralY = 9;
    private boolean upCheck = false; // up is in check
    private boolean downCheck = false; // down is in check

    private static int winner;

    public Board() {
        winner = NA;
        gBoard = new Point[10][9];
        initialize(gBoard);

    }

    public boolean tryMove3(Move move, Player player) {

        if (new MoveTester(this, move).isLegal()) {

            Piece curr = gBoard[move.getOriginY()][move.getOriginX()].getPiece();
            Piece captured = this.gBoard[move.getFinalY()][move.getFinalX()].getPiece();

            int x = move.getOriginX();
            int y = move.getOriginY();
            int finalX = move.getFinalX();
            int finalY = move.getFinalY();

            if (curr.getSide() == player.getPlayerSide()) {
                doMove(move);
                testCheck();
                if (curr.getSide() == Piece.Side.UP && upCheck) {
                    System.out.println(" Illegal Move, you're in check");
                    undoMove(move, captured);
                    return false;
                }
                if (curr.getSide() == Piece.Side.DOWN && downCheck) {
                    System.out.println(" Illegal Move, you're in check");
                    undoMove(move, captured);
                    return false;

                } else {
                    if (upCheck && curr.getSide() == Piece.Side.DOWN) {
                        if (checkMate(Piece.Side.UP)) {
                            winner = PLAYER1_WINS;
                        }
                        // return true;

                    } else if (downCheck && curr.getSide() == Piece.Side.UP) {
                        if (checkMate(Piece.Side.DOWN)) {
                            winner = PLAYER2_WINS;
                        }
                        // return true;

                        // the move is legal, now lets see if it's a stalemate
                    } else if (curr.getSide() == Piece.Side.DOWN) {
                        if (checkMate(Piece.Side.UP) || separated()) {
                            winner = DRAW;
                            // System.out.println("ITS A DRAW");
                            // System.out.println("##########################STALE
                            // MATE#############################");
                        }
                        // return true;
                    } else if (curr.getSide() == Piece.Side.UP) {
                        if (checkMate(Piece.Side.DOWN) || separated()) {
                            winner = DRAW;
                            // System.out.println("ITS A DRAW");
                            // System.out.println("##########################STALE
                            // MATE#############################");
                        }
                        // return true;
                    }

                    // if (!checkMate) { //LEGAL MOVE AND NOT IN CHECKMATE?
                    System.out.println(
                            "Moved " + curr + " from (" + x + ", " + y + ") to (" + finalX + ", " + finalY + ")");
                    if (captured != null) {
                        player.addPieceCaptured(captured);
                        System.out.println(captured + " Captured!");
                        MoveLogger.addMove(new Move(curr, captured, x, y, finalX, finalY));
                    } else {
                        MoveLogger.addMove(new Move(curr, x, y, finalX, finalY));
                    }

                    return true;
                    // }

                }
            } else {
                // System.out.println("That's not your piece");
                System.out.println("That's not your turn");

                return false;
            }
        }
        System.out.println("Illegal Move");
        return false;

    }

    void doMove(Move move) {
        Piece curr = gBoard[move.getOriginY()][move.getOriginX()].getPiece();
        // Piece captured = this.gBoard[move.getFinalY()][move.getFinalX()].getPiece();
        this.gBoard[move.getFinalY()][move.getFinalX()].setPiece(curr);
        this.gBoard[move.getOriginY()][move.getOriginX()].setPiece(null);
    }

    void undoMove(Move move, Piece captured) {
        Piece curr = getPoint(move.getFinalX(), move.getFinalY()).getPiece();
        getPoint(move.getOriginX(), move.getOriginY()).setPiece(curr);
        getPoint(move.getFinalX(), move.getFinalY()).setPiece(captured);
        // System.out.print(" Illegal Move");
    }

    void updateGenerals() {
        // finds location of generals

        for (int x = 3; x < 6; x++) {
            for (int y = 0; y < 3; y++) {
                Piece curr = getPoint(x, y).getPiece();
                if (curr != null && curr.toString().equals("General")) {
                    setUpGeneralX(x);
                    setUpGeneralY(y);
                }

            }

            for (int y = 7; y < 10; y++) {
                Piece curr = getPoint(x, y).getPiece();
                if (curr != null && curr.toString().equals("General")) {
                    setDownGeneralX(x);
                    setDownGeneralY(y);
                }
            }
        }
    }

    private void testCheck() {
        updateGenerals();
        downCheck = false;
        upCheck = false;
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 10; y++) {
                if (getPoint(x, y).getPiece() != null) {

                    if (!downCheck && getPoint(x, y).getPiece().getSide() == Piece.Side.UP) {
                        if (new MoveTester(this, new Move(x, y, downGeneralX, downGeneralY), 0).isLegal()) {
                            downCheck = true;
                            // System.out.println("Down is in check");
                        }
                    } else if (!upCheck && getPoint(x, y).getPiece().getSide() == Piece.Side.DOWN) {
                        if (new MoveTester(this, new Move(x, y, upGeneralX, upGeneralY), 0).isLegal()) {
                            upCheck = true;
                            // System.out.println("up is in check");
                        }
                    }
                }
            }
        }
    }

    private boolean checkMate(Piece.Side loserSide) {
        updateGenerals();

        // running through every loser piece
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 10; y++) {

                if (getPoint(x, y).getPiece() != null && getPoint(x, y).getPiece().getSide() == loserSide) {

                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 10; j++) {
                            Move tempMove = new Move(x, y, i, j); // generating the temporary move
                            Piece tempCaptured = getPoint(i, j).getPiece();
                            // if that move is legal then attempt it.
                            if (new MoveTester(this, tempMove).isLegal()) { // trying every possible move for the piece
                                doMove(tempMove); // doing the temporary move
                                testCheck(); // updates check status
                                // if any of these moves were both legal, and result with us not being in check,
                                // we aren't in checkmate.
                                if (loserSide == Piece.Side.DOWN) {
                                    if (!downCheck) {
                                        undoMove(tempMove, tempCaptured);
                                        return false;
                                    }
                                }
                                if (loserSide == Piece.Side.UP) {
                                    if (!upCheck) {
                                        undoMove(tempMove, tempCaptured);
                                        return false;
                                    }
                                }
                                undoMove(tempMove, tempCaptured);

                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean separated() {

        int cannonCounter = 0;
        int pieceCounter = 0;

        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 10; y++) {
                if (getPoint(x, y).getPiece() != null) {
                    pieceCounter++;
                    if (getPoint(x, y).getPiece().canWinAlone()) {
                        return false;
                    }
                    if (getPoint(x, y).getPiece().toString().equals("Cannon")) {
                        cannonCounter++;
                    }
                }
            }
        }

        if (pieceCounter > 3 && cannonCounter >= 1) {
            return false;
        }
        return true;
    }

    private static void initialize(Point[][] board) {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                board[y][x] = new Point(x, y); // weird but basically transposes for easier coding and visualization. x
                                               // and y are coordinates if the origin was the top right of the
                                               // gameboard.
            }
        }

        // Chariots/Rook
        board[0][0].setPiece(new Chariot(Piece.Side.UP));
        board[0][8].setPiece(new Chariot(Piece.Side.UP));

        board[9][0].setPiece(new Chariot(Piece.Side.DOWN));
        board[9][8].setPiece(new Chariot(Piece.Side.DOWN));

        // Cannons
        board[2][1].setPiece(new Cannon(Piece.Side.UP));
        board[2][7].setPiece(new Cannon(Piece.Side.UP));

        board[7][1].setPiece(new Cannon(Piece.Side.DOWN));
        board[7][7].setPiece(new Cannon(Piece.Side.DOWN));

        // Horses/Knights
        board[0][1].setPiece(new Horse(Piece.Side.UP));
        board[0][7].setPiece(new Horse(Piece.Side.UP));

        board[9][1].setPiece(new Horse(Piece.Side.DOWN));
        board[9][7].setPiece(new Horse(Piece.Side.DOWN));

        // Elephants/bishops
        board[0][2].setPiece(new Elephant(Piece.Side.UP));
        board[0][6].setPiece(new Elephant(Piece.Side.UP));

        board[9][2].setPiece(new Elephant(Piece.Side.DOWN));
        board[9][6].setPiece(new Elephant(Piece.Side.DOWN));

        // Guard/Advisors
        board[0][3].setPiece(new Guard(Piece.Side.UP));
        board[0][5].setPiece(new Guard(Piece.Side.UP));

        board[9][3].setPiece(new Guard(Piece.Side.DOWN));
        board[9][5].setPiece(new Guard(Piece.Side.DOWN));

        // General/King
        board[0][4].setPiece(new General(Piece.Side.UP));
        board[9][4].setPiece(new General(Piece.Side.DOWN));

        // Solider/pawns
        board[3][0].setPiece(new Soldier(Piece.Side.UP));
        board[3][2].setPiece(new Soldier(Piece.Side.UP));
        board[3][4].setPiece(new Soldier(Piece.Side.UP));
        board[3][6].setPiece(new Soldier(Piece.Side.UP));
        board[3][8].setPiece(new Soldier(Piece.Side.UP));

        board[6][0].setPiece(new Soldier(Piece.Side.DOWN));
        board[6][2].setPiece(new Soldier(Piece.Side.DOWN));
        board[6][4].setPiece(new Soldier(Piece.Side.DOWN));
        board[6][6].setPiece(new Soldier(Piece.Side.DOWN));
        board[6][8].setPiece(new Soldier(Piece.Side.DOWN));

    }

    /**
     * An Ascii board used for testing purposes
     */
    protected void printBoard() {
        System.out.println(
                "       0         1        2         3         4         5         6         7         8     ");
        String hLine = "  -------------------------------------------------------------------------------------------";
        System.out.println(hLine);

        for (int y = 0; y < 10; y++) {
            System.out.print(y + " |");
            for (int x = 0; x < 9; x++) {

                if (gBoard[y][x].getPiece() == null) {
                    System.out.printf("%8s%2s", "", "|");
                } else {
                    System.out.printf("%8s%2s", gBoard[y][x].getPiece(), "|");
                }

            }
            System.out.println();
            System.out.println(hLine);

        }
    }

    public Point getPoint(int x, int y) {
        return gBoard[y][x];
    }

    int getUpGeneralX() {
        return upGeneralX;
    }

    private void setUpGeneralX(int upGeneralX) {
        this.upGeneralX = upGeneralX;
    }

    int getUpGeneralY() {
        return upGeneralY;
    }

    private void setUpGeneralY(int upGeneralY) {
        this.upGeneralY = upGeneralY;
    }

    int getDownGeneralX() {
        return downGeneralX;
    }

    private void setDownGeneralX(int downGeneralX) {
        this.downGeneralX = downGeneralX;
    }

    int getDownGeneralY() {
        return downGeneralY;
    }

    private void setDownGeneralY(int downGeneralY) {
        this.downGeneralY = downGeneralY;
    }

    public static void setWinner(int winnerNum) {
        winner = winnerNum;
    }

    public int getWinner() {
        return winner;
    }
}
