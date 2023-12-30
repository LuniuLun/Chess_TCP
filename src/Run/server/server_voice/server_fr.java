package Run.server.server_voice;

import java.awt.EventQueue;
import java.net.DatagramSocket;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import Run.server.Server;

import javax.swing.JButton;
import javax.sound.sampled.SourceDataLine;

public class server_fr extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JButton btn_start;
	static JTextArea textArea;

	public int port = 8888;

	public static AudioFormat getAudioFormat() {
		Float sampleRate = 8000.0F;
		Integer sampleSizeInbits = 16;
		Integer channel = 2;
		Boolean signed = true;
		Boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
	}

	public SourceDataLine audio_out;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					server_fr frame = new server_fr();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void init_server() throws LineUnavailableException, SocketException {
		AudioFormat format = getAudioFormat();
		DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
		if (!AudioSystem.isLineSupported(info_out)) {
			System.out.println("not support");
			System.exit(0);
		}
		audio_out = (SourceDataLine) AudioSystem.getLine(info_out);
		audio_out.open(format);
		audio_out.start();
		player_thread p = new player_thread();
		p.setGames(Server.getGames());
		p.serverSocket = new DatagramSocket(port);
		p.audio_out = audio_out;
		server_voice.calling = true;
		p.start();

		// btn_start.setEnabled(false);
	}

	/**
	 * Create the frame.
	 */
	public server_fr() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		textArea = new JTextArea();
		textArea.setBounds(10, 10, 416, 152);
		contentPane.add(textArea);
		try {
			init_server();
		} catch (SocketException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public static void updateTextArea(String message) {
        textArea.append(message + "\n");
    }
}
