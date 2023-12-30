package Run.server.server_voice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.SourceDataLine;

public class player_thread extends Thread {
    public DatagramSocket din;
    public SourceDataLine audio_out;
    byte[] buffer = new byte[512];

    @Override
    public void run() {
        int i = 0;
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        while (server_voice.calling) {
            try {
                din.receive(incoming);
                System.out.println("#" + i++);;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            buffer = incoming.getData();
            audio_out.write(buffer, 0, buffer.length);
        }
        audio_out.close();
        audio_out.drain();
        System.out.println("stop");
    }
}
