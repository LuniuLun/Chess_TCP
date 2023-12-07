package GUI;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JButton;

public class ChatBox extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextArea textArea;

	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;

	/**
	 * Create the panel.
	 * 
	 * @throws IOException
	 */
	public ChatBox(Socket socket) throws IOException {
		this.socket = socket;
		this.dis = new DataInputStream(socket.getInputStream());
		this.dos = new DataOutputStream(socket.getOutputStream());
		setLayout(null);

		textField = new JTextField();
		textField.setBounds(10, 154, 149, 25);
		add(textField);
		textField.setColumns(10);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 10, 244, 134);
        add(scrollPane);

        textArea = new JTextArea();
        // Set the JTextArea as non-editable
        textArea.setEditable(false);
        // Add the JTextArea to the JScrollPane
        scrollPane.setViewportView(textArea);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.setBounds(169, 154, 85, 25);
		add(btnNewButton);
		btnNewButton.addActionListener((ActionListener) new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayMessage("Toi: " + textField.getText());
				sendMessage();
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
		System.out.println(message);
		textArea.append(message + "\n");
	}

}
