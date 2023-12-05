package Run.server;

import Run.server.Server.ClientHandler;

public class Game {
    private ClientHandler player1;
    private ClientHandler player2;

    public Game() {
        this.player1 = null;
        this.player2 = null;
    }

    public ClientHandler getPlayer1() {
        return player1;
    }

    public void setPlayer1(ClientHandler player1) {
        this.player1 = player1;
    }

    public ClientHandler getPlayer2() {
        return player2;
    }

    public void setPlayer2(ClientHandler player2) {
        this.player2 = player2;
    }

    public boolean isFull() {
        return player1 != null && player2 != null;
    }

    public void addPlayer(ClientHandler player) {
        if (player1 == null) {
            player1 = player;
        } else if (player2 == null) {
            player2 = player;
        }
    }
}
