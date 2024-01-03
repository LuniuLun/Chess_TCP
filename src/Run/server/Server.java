package Run.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import GameLogic.Move;
import Run.server.server_voice.server_fr;

public class Server extends JFrame {
    private static final long serialVersionUID = 1L;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static List<Game> games = new ArrayList<>();
    private JTextArea textArea;
    private JPanel contentPane;

    int countGameRoom = 0;

    public static void main(String[] args) {
        server_fr server_voice = new server_fr();
        server_voice.setVisible(true);

        Server frame = new Server();
        frame.initServer(); 

    }

    public Server() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBounds(10, 10, 416, 243);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 10, 416, 200);
        contentPane.add(scrollPane);
        contentPane.add(textArea);

        this.setVisible(true);
        initServer();
    }

    private void initServer() {
        try (ServerSocket serverSocket = new ServerSocket(6969)) {
            textArea.append("Server is started!!!" + "\n");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                textArea.append("Client connected: " + clientSocket + "\n");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
                if (games.size() == 0) {
                    Game newGame = new Game();
                    newGame.addPlayer(clientHandler);
                    games.add(newGame);
                } else {
                    boolean isAddition = false;
                    for (Game game : games) {
                        if (!game.isFull()) {
                            game.addPlayer(clientHandler);
                            isAddition = true;
                        }
                    }
                    if (isAddition == false) {
                        Game newGame = new Game();
                        newGame.addPlayer(clientHandler);
                        games.add(newGame);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ClientHandler implements Runnable {
        public Socket clientSocket;
        private DataInputStream dis;
        private DataOutputStream dos;

        public ClientHandler(Socket clientSocket) {
            try {
                this.clientSocket = clientSocket;
                dis = new DataInputStream(clientSocket.getInputStream());
                dos = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                dos.writeInt(clientSocket.getPort());
                dos.flush();
                while (true) {
                    boolean isString = dis.readBoolean();
                    System.out.println(isString);
                    if (isString) {
                        // Read a text-based message
                        String receivedMessage = dis.readUTF();

                        // Check if it's a specific text message
                        if (receivedMessage.equals("CreateRound")) {
                            textArea.append("Received message from client having port " +
                                    clientSocket.getPort() + ": " + receivedMessage + "\n");
                            for (Game game : games) {
                                if (game.getPlayer1().clientSocket.getPort() == clientSocket.getPort()
                                        || game.getPlayer2().clientSocket.getPort() == clientSocket.getPort()) {
                                    if (game.isFull()) {
                                        game.getPlayer1().sendMessage("UP");
                                        game.getPlayer2().sendMessage("DOWN");
                                    }
                                    break;
                                }
                            }
                        } else {
                            String sendedMessage = "PlayerPort" + clientSocket.getPort() + ": " +
                                    receivedMessage;
                            for (Game game : games) {
                                if (game.getPlayer1().clientSocket.getPort() == clientSocket.getPort()) {
                                    game.getPlayer2().sendMessage(sendedMessage);
                                    break;
                                }
                                if (game.getPlayer2().clientSocket.getPort() == clientSocket.getPort()) {
                                    game.getPlayer1().sendMessage(sendedMessage);
                                    break;
                                }
                            }
                        }
                    } else {
                        // Đọc kích thước dữ liệu từ DataInputStream
                        int dataSize = dis.readInt();
                        byte[] data = new byte[dataSize];
                        dis.readFully(data); // Đọc dữ liệu từ DataInputStream vào mảng byte data
                        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
                            Move move = (Move) objectInputStream.readObject();
                            System.out.println("Nhan duoc move tu client " + move.toString());
                            for (Game game : games) {
                                if (game.getPlayer1().clientSocket.getPort() == clientSocket.getPort()) {
                                    game.getPlayer2().sendMoveForClient(move);
                                    break;
                                }
                                if (game.getPlayer2().clientSocket.getPort() == clientSocket.getPort()) {
                                    game.getPlayer1().sendMoveForClient(move);
                                    break;
                                }
                            }

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public void sendMessage(String message) {
            try {
                dos.writeBoolean(true);// xac dinh truyen di 1 string
                dos.writeUTF(message);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMoveForClient(Move move) {
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
                objectOutputStream.writeObject(move);
                byte[] data = byteArrayOutputStream.toByteArray();

                dos.writeBoolean(false);// xac dinh truyen di 1 object
                dos.writeInt(data.length); // Ghi kích thước dữ liệu vào DataOutputStream
                dos.write(data); // Ghi dữ liệu vào DataOutputStream

                dos.flush(); // Đảm bảo dữ liệu được gửi đi
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendAudio(byte[] audioData) {
            try {
                dos.writeBoolean(true);
                dos.writeUTF("Response_audio");
                dos.writeInt(audioData.length); // Ghi kích thước dữ liệu vào DataOutputStream
                dos.write(audioData, 0, audioData.length); // Ghi dữ liệu vào DataOutputStream

                dos.flush(); // Đảm bảo dữ liệu được gửi đi
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<Game> getGames() {
        return games;
    }
}