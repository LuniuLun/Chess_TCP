package Run;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
    public static void main(String[] args) {
        Core core = new Core();        
    }

}
