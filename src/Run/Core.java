package Run;

import GUI.*;
import GameLogic.Board;
import GameLogic.Move;
import GameLogic.MoveLogger;
import GameLogic.Player;
import GameLogic.Pieces.Piece;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.*;

public class Core {

    // private static StartFrame startFrame;
    private BoardFrame boardFrame;
    private BoardPanel boardPanel;
    private TurnTimerPanel timerPanel;
    private BoardMenu boardMenu;
    private Board board;
    private int counter;
    private Player player1;
    private Player player2;
    private static StartFrame startFrame;
    private EndScreen endScreen;
    private Profile profile;
    private ChatBox chatbox;
    private VoiceChat voiceChat;
    public Socket socket;
    public Core() {
        startFrame = new StartFrame(this);
    }

    public void start(Profile profile, String direction, Socket socket, int port) throws IOException {
        this.profile = profile;
        this.socket = socket;
        if (direction.equals("UP")) {
            player1 = new Player(1, profile.getP1String(), Piece.Side.UP, direction, this);
            player2 = new Player(2, profile.getP2String(), Piece.Side.DOWN, direction, this);
        } else {
            player1 = new Player(1, profile.getP1String(), Piece.Side.DOWN, direction, this);
            player2 = new Player(2, profile.getP2String(), Piece.Side.UP, direction, this);
        }

        board = new Board();
        boardPanel = new BoardPanel(this, socket, direction);
        boardMenu = new BoardMenu(this);
        timerPanel = new TurnTimerPanel(player1, player2, profile, direction);
        chatbox = new ChatBox(socket);
        voiceChat = new VoiceChat(port);
        boardFrame = new BoardFrame(this, chatbox, voiceChat, socket);

        // Initialize and set the chatbox visible
        voiceChat.setVisible(true);
        chatbox.setVisible(true);
        counter = 0;
        player1.startTurnTimer(timerPanel);
    }

    public void playMove(Move move) throws IOException {
        // System.out.println(counter);

        if (counter % 2 == 0) {
            if (board.tryMove3(move, player1)) {
                // first round
                player1.stopTurnTimer();
                player2.startTurnTimer(timerPanel);

                counter++;
            }
        } else if (counter % 2 == 1) {
            // player2.startTurnTimer(timerPanel);
            if (board.tryMove3(move, player2)) {
                player2.stopTurnTimer();
                player1.startTurnTimer(timerPanel);
                counter++;
            }
            // player2.stopTurnTimer();
        }
        // Broken Win Screen
        getBoardPanel().userRepaint();
        if (board.getWinner() != Board.NA) {
            System.out.println("GAME OVER");
            callEnd();
        }

    }

    /**
     * Calls an end to the game and opens the endScreen.
     * @throws IOException
     */
    public void callEnd() throws IOException {
        player1.stopTurnTimer();
        player2.stopTurnTimer();
        endScreen = new EndScreen(this, board.getWinner(), profile, socket);

    }

    public void saveGame() throws Exception {
        String os = System.getProperty("os.name").toLowerCase();
        JFrame parentFrame = new JFrame();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        if (os.contains("mac"))
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setDialogTitle("Specify where to save game");

        int userSelection = fileChooser.showSaveDialog(parentFrame);

        parentFrame.setVisible(true);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            System.out.println("Current Directory: " + fileChooser.getCurrentDirectory());
            System.out.println("Selected File" + fileChooser.getSelectedFile());
            MoveLogger.saveAllMoves(player1, player2, fileChooser.getSelectedFile());
            System.out.println("Game Saved");
            parentFrame.setVisible(false);
        } else {
            System.out.println("No Selection");
            parentFrame.setVisible(false);
        }
    }

    public void setInvisible() {
        boardFrame.setVisible(false);
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }

    public BoardFrame getBoardFrame() {
        return boardFrame;
    }

    public TurnTimerPanel getTurnTimerPanel() {
        return timerPanel;
    }

    public Board getBoard() {
        return board;
    }

    public BoardMenu getBoardMenu() {
        return boardMenu;
    }

    public void setProfile(Profile newProfile) {
        this.profile = newProfile;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public ChatBox getChatBox() {
        return this.chatbox;
    }
}
