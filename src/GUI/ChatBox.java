package GUI;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import GUI.test_voice.test;

import javax.swing.JTextArea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;

public class ChatBox extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	public static boolean calling = false;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private JTextArea textArea;

	/**
	 * Create the panel.
	 * 
	 * @throws IOException
	 */

	public static AudioFormat getAudioFormat() {
		Float sampleRate = 8000.0F;
		Integer sampleSizeInbits = 16;
		Integer channel = 2;
		Boolean signed = true;
		Boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
	}

	TargetDataLine audio_in;

	public void init_audio(Socket socket) throws LineUnavailableException, UnknownHostException, SocketException {
		AudioFormat format = getAudioFormat();
		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)) {
			System.out.println("not support");
			System.exit(0);
		}
		audio_in = (TargetDataLine) AudioSystem.getLine(info);
		audio_in.open(format);
		audio_in.start();
		recorder_thread r = new recorder_thread();
		r.audioSocket = socket;
		r.audio_in = audio_in;
		calling = true;
		r.start();
	}

	public ChatBox() {
		// Initialize your components or perform any other necessary setup here
	}

	public ChatBox(Socket socket) throws IOException {
		this.socket = socket;
		this.dis = new DataInputStream(socket.getInputStream());
		this.dos = new DataOutputStream(socket.getOutputStream());
		setLayout(null);

		textField = new JTextField();
		textField.setBounds(87, 154, 73, 27);
		add(textField);
		textField.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 221, 134);
		add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		JButton btn_Mic = new JButton("OnMic");
		btn_Mic.setBounds(10, 154, 77, 25);
		add(btn_Mic);
		btn_Mic.addActionListener((ActionListener) new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btn_Mic.getText().equals("OnMic")) {
					// try {
					// init_audio(socket);
					// } catch (UnknownHostException | SocketException | LineUnavailableException
					// e1) {
					// // TODO Auto-generated catch block
					// e1.printStackTrace();
					// }
					test t = new test();
					t.setVisible(true);
					btn_Mic.setText("OffMic");
				} else {
					calling = false;
					btn_Mic.setText("OnMic");
				}
			}
		});
		JButton btnNewButton = new JButton("Send");
		btnNewButton.setBounds(157, 154, 74, 25);
		add(btnNewButton);
		btnNewButton.addActionListener((ActionListener) new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (textField.getText() != "") {
					displayMessage("Toi: " + textField.getText());
					sendMessage();
				}
			}
		});
		// createReceivedMessageThread();
		this.setVisible(true);
	}

	private void sendMessage() {
		String mess = textField.getText();
		try {
			dos.writeBoolean(true);
			dos.writeUTF(mess);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		textField.setText("");
	}

	void displayMessage(String message) {
		if (message != null) {
			textArea.append(message + "\n");
		}
	}

	public class recorder_thread extends Thread {
		public TargetDataLine audio_in = null;
		public Socket audioSocket;
		byte byte_buff[] = new byte[512];

		@Override
		public void run() {
			int i = 0;
			while (calling) {
				audio_in.read(byte_buff, 0, byte_buff.length);
				try {
					dos.writeBoolean(true);
					dos.write(byte_buff, 0, byte_buff.length);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// System.out.println("send #" + i++);
			}
		}
	}

}
