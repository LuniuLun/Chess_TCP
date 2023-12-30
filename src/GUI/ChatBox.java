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

	public ChatBox() {
		// Initialize your components or perform any other necessary setup here
	}

	public ChatBox(Socket socket) throws IOException {
		this.socket = socket;
		this.dis = new DataInputStream(socket.getInputStream());
		this.dos = new DataOutputStream(socket.getOutputStream());
		setLayout(null);

		textField = new JTextField();
		textField.setBounds(10, 154,143, 27);
		add(textField);
		textField.setColumns(10);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 221, 134);
		add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

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

}
