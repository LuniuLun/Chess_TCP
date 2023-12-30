package Run.server.server_voice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.SourceDataLine;

import Run.server.Game;

public class player_thread extends Thread {
    public DatagramSocket serverSocket;
    public SourceDataLine audio_out;
    public List<Game> games;
    private List<PairOfVoiceChat> pairOfVoiceChat = new ArrayList<>();
    byte[] buffer = new byte[512];

    public void setGames(List<Game> games) {
        this.games = games;
    }

    @Override
    public void run() {
        int i = 0;
        DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);

        while (true) {
            try {
                serverSocket.receive(receivePacket);
                byte[] data = receivePacket.getData();
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                if (message.equals("1")) {
                    System.out.println("nhan port");
                    serverSocket.receive(receivePacket);
                    int dataPort = Integer.parseInt(new String(receivePacket.getData(), 0, receivePacket.getLength()));
                    handleDataport(dataPort, receivePacket);
                } else {
                    serverSocket.receive(receivePacket);
                    handleDataVoice(receivePacket);
                    // audio_out.write(buffer, 0, buffer.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleDataport(int dataPort, DatagramPacket receivePacket) {
        server_fr.updateTextArea("Received dataport: " + dataPort);

        for (int i = 0; i < games.size(); i++) {
            while (pairOfVoiceChat.size() <= i) {
                pairOfVoiceChat.add(new PairOfVoiceChat());
            }
            Game currentGame = games.get(i);
            if (currentGame.getPlayer1().clientSocket.getPort() == dataPort) {
                pairOfVoiceChat.get(i).setPortVoiceChat1(receivePacket.getPort());
                pairOfVoiceChat.get(i).setInetAddressVoiceChat1(receivePacket.getAddress());
                break;
            }
            if (currentGame.getPlayer2().clientSocket.getPort() == dataPort) {
                pairOfVoiceChat.get(i).setPortVoiceChat2(receivePacket.getPort());
                pairOfVoiceChat.get(i).setInetAddressVoiceChat2(receivePacket.getAddress());
                break;
            }
        }
    }

    private void handleDataVoice(DatagramPacket receivePacket) throws IOException {
        buffer = receivePacket.getData();
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        for (PairOfVoiceChat pair : pairOfVoiceChat) {
            if (pair.getPortVoiceChat1() == clientPort && pair.getInetAddressVoiceChat1().equals(clientAddress) && pair.getInetAddressVoiceChat2() != null && pair.getPortVoiceChat2() != 0) {
                InetAddress destinationAddress = pair.getInetAddressVoiceChat2();
                int destinationPort = pair.getPortVoiceChat2();
                DatagramPacket sendVoice = new DatagramPacket(buffer, buffer.length, destinationAddress,
                        destinationPort);
                serverSocket.send(sendVoice);
                break;
            }
            if (pair.getPortVoiceChat2() == clientPort && pair.getInetAddressVoiceChat2().equals(clientAddress) && pair.getInetAddressVoiceChat1() != null && pair.getPortVoiceChat1() != 0) {
                InetAddress destinationAddress = pair.getInetAddressVoiceChat1();
                int destinationPort = pair.getPortVoiceChat1();
                DatagramPacket sendVoice = new DatagramPacket(buffer, buffer.length, destinationAddress,
                        destinationPort);
                serverSocket.send(sendVoice);
                break;
            }
        }
    }

    public class PairOfVoiceChat {
        private int portVoiceChat1 = 0;
        private InetAddress inetAddressVoiceChat1;
        private int portVoiceChat2 = 0;
        private InetAddress inetAddressVoiceChat2;

        // Các phương thức getter và setter cho portVoiceChat1
        public int getPortVoiceChat1() {
            return portVoiceChat1;
        }

        public void setPortVoiceChat1(int portVoiceChat1) {
            this.portVoiceChat1 = portVoiceChat1;
        }

        // Các phương thức getter và setter cho inetAddressVoiceChat1
        public InetAddress getInetAddressVoiceChat1() {
            return inetAddressVoiceChat1;
        }

        public void setInetAddressVoiceChat1(InetAddress inetAddressVoiceChat1) {
            this.inetAddressVoiceChat1 = inetAddressVoiceChat1;
        }

        // Các phương thức getter và setter cho portVoiceChat2
        public int getPortVoiceChat2() {
            return portVoiceChat2;
        }

        public void setPortVoiceChat2(int portVoiceChat2) {
            this.portVoiceChat2 = portVoiceChat2;
        }

        // Các phương thức getter và setter cho inetAddressVoiceChat2
        public InetAddress getInetAddressVoiceChat2() {
            return inetAddressVoiceChat2;
        }

        public void setInetAddressVoiceChat2(InetAddress inetAddressVoiceChat2) {
            this.inetAddressVoiceChat2 = inetAddressVoiceChat2;
        }
    }
}