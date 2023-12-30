package GameLogic;

import java.util.ArrayList;

// import GUI.Profile;
import GUI.TurnTimerPanel;
import GameLogic.Pieces.*;
import Run.Core;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
// import java.util.Scanner;

public class Player {

    protected Timer timer;

    private int id;
    private String name;
    private Piece.Side side;
    private ArrayList<Piece> piecesCaptured;
    private boolean checkmateStatus;
    private long timeElapsed;
    private Core core;

    private String color;

    // public Player(int id, Piece.Side side, String direction, Profile profile) {
    // this.id = id;
    // this.name = "Player" + id;
    // this.side = side;
    // this.piecesCaptured = new ArrayList<Piece>();
    // this.checkmateStatus = false;
    // this.timeElapsed = 0;

    // timer = new Timer();

    // // Set the color of the player based on their side. Color will be used for
    // // theming.

    // if (direction.equals("UP")) {
    // if(side == Piece.Side.DOWN) color = "Black";
    // } else color = "Red";

    // if (direction.equals("DOWN")) {
    // if(side == Piece.Side.DOWN) color = "Red";
    // } else color = "Black";

    // }

    public Player(int id, String name, Piece.Side side, String direction, Core core) {
        this.id = id;
        this.name = name;
        this.side = side;
        this.piecesCaptured = new ArrayList<Piece>();
        this.checkmateStatus = false;
        this.timeElapsed = 0;
        this.core = core;

        timer = new Timer();

        /*
         * Set the color of the player based on their side. Color will be used for
         * theming.
         */
        if (direction.equals("UP")) {
            if (side == Piece.Side.UP) {
                this.color = "Red";
            } else {
                this.color = "Black";
            }

        }

        if (direction.equals("DOWN")) {
            if (side == Piece.Side.DOWN) {
                this.color = "Red";
            } else {
                this.color = "Black";
            }
        }

    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getColor() {
        return color;
    }

    public Piece.Side getPlayerSide() {
        return this.side;
    }

    public void setPlayerSide(Piece.Side side) {
        this.side = side;
    }

    public void addPieceCaptured(Piece pieceCaptured) {
        piecesCaptured.add(pieceCaptured);
    }

    public ArrayList<Piece> getPiecesCaptured() {
        return piecesCaptured;
    }

    public void printPiecesCaptured() {
        System.out.print("Pieces Captured: ");

        // loop through ArrayList and print out all pieces captured
        for (int i = 0; i < piecesCaptured.size(); i++) {
            if (piecesCaptured.size() - 1 == i) {
                System.out.println(piecesCaptured.get(i));
            } else {
                System.out.print(piecesCaptured.get(i) + ", ");
            }
        }
    }

    public int getNumPiecesCaptured() {
        return this.piecesCaptured.size();
    }

    public void clearPiecesCaptured() {
        piecesCaptured.clear();
    }

    public boolean getCheckmateStatus() {
        return checkmateStatus;
    }

    public void setCheckmateStatus(Boolean checkmateStatus) {
        this.checkmateStatus = checkmateStatus;
    }

    public void startTurnTimer() {
        timer.start();

    }

    public void startTurnTimer(TurnTimerPanel panel) {
        timer.start();

        if (this.side == Piece.Side.DOWN) { // red
            panel.updateRedTime();

        } else {
            panel.updateBlackTime();

        }
    }

    public void stopTurnTimer() {
        timer.stop();
        timeElapsed += timer.getTime();
    }

    public long getElapsedTime() {
        return timeElapsed;
    }

    public String printElapsedTime() {
        Date date = new Date(timeElapsed);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String formatted = formatter.format(date);
        return formatted;

    }

    public String elapsedTimeToString() {
        // format to mm:ss.
        Date date = new Date(timer.getTime() + timeElapsed);
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        String formatted = formatter.format(date);
        return formatted;

    }

    public String elapsedTimeToString(int timeLimit) throws IOException {

        // convert the timeLimit (min) to milliseconds
        timeLimit = timeLimit * 60000;
        if (timer.getTime() + timeElapsed >= timeLimit) { // check if the timeElapsed have passed the timeLimit
            if (this.getPlayerSide() == Piece.Side.DOWN) { // player 1 is always down river
                Board.setWinner(Board.PLAYER2_TIMEOUT_WIN); // call end game since timer limit has passed
                core.callEnd();
                return "00:00";

            } else {
                Board.setWinner(Board.PLAYER1_TIMEOUT_WIN); // player 2 up river
                core.callEnd();
                return "00:00";
            }

        } else {

            // if time elapsed have not passed the time limit, return time left in mm:ss
            // format
            Date date = new Date(timeLimit - (timer.getTime() + timeElapsed));
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
            String formatted = formatter.format(date);
            return formatted;
        }

    }

    public boolean isTimerRunning() {
        return timer.isStillRunning();
    }

}
