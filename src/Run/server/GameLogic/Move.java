package Run.server.GameLogic;

import java.io.Serializable;

import Run.server.GameLogic.Pieces.Piece;

public class Move implements Serializable{
    private Piece piece;
    private Piece capturedPiece;

    private int originX;
    private int originY;
    private int finalX;
    private int finalY;


    private int dx;
    private int dy;
    private boolean isHorizontal;
    private boolean isVertical;
    private boolean isDiagonal;
    private boolean isValid;
    //private boolean isClear;
    private int numObstacles; //number of pieces on the path
    public Move(int originX, int originY, int finalX, int finalY) {
        this.originX = originX;
        this.originY = originY;
        this.finalX = finalX;
        this.finalY = finalY;

        this.dx = finalX - originX;
        this.dy = finalY - originY;
        if (dx == 0 && dy != 0) {
            this.isVertical = true;
        }
        if (dy == 0 && dx != 0) {
            this.isHorizontal = true;
        }
        if (Math.abs(dx) == Math.abs(dy) && dx != 0) {
            this.isDiagonal = true;
        }

    }
    public Move(Piece piece, int originX, int originY, int finalX, int finalY) {
        //this iteration makes the move object hold the piece? Not sure how to structure
        this.piece = piece;
        this.originX = originX;
        this.originY = originY;
        this.finalX = finalX;
        this.finalY = finalY;

        this.dx = finalX - originX;
        this.dy = finalY - originY;
        if (dx == 0 && dy != 0) {
            this.isVertical = true;
        }
        if (dy == 0 && dx != 0) {
            this.isHorizontal = true;
        }
        if (Math.abs(dx) == Math.abs(dy) && dx != 0) {
            this.isDiagonal = true;
        }
    }

    public Move(Piece piece, Piece capturedPiece, int originX, int originY, int finalX, int finalY) {

        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.originX = originX;
        this.originY = originY;
        this.finalX = finalX;
        this.finalY = finalY;

        this.dx = finalX - originX;
        this.dy = finalY - originY;
        if (dx == 0 && dy != 0) {
            this.isVertical = true;
        }
        if (dy == 0 && dx != 0) {
            this.isHorizontal = true;
        }
        if (Math.abs(dx) == Math.abs(dy) && dx != 0) {
            this.isDiagonal = true;
        }
    }
    public Move(Move otherMove) {
        this.piece = otherMove.getPiece();
        this.capturedPiece = otherMove.getCapturedPiece();
        this.originX = otherMove.getOriginX();
        this.originY = otherMove.getOriginY();
        this.finalX = otherMove.getFinalX();
        this.finalY = otherMove.getFinalY();
        this.dx = otherMove.getDx();
        this.dy = otherMove.getDy();
        this.isHorizontal = otherMove.isHorizontal();
        this.isVertical = otherMove.isVertical();
        this.isDiagonal = otherMove.isDiagonal();
        this.isValid = otherMove.isValid();
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }

    public int getFinalX() {
        return finalX;
    }

    public int getFinalY() {
        return finalY;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public boolean isDiagonal() {
        return isDiagonal;
    }

    boolean isValid() {
        return isValid;
    }
    public void setValid(boolean v) {
        this.isValid = v;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public String toString() {
        return originX + ", " + originY + ", " + finalX + ", " + finalY;
    }


}
