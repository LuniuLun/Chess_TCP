package GUI;

import Run.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class BoardFrame extends JFrame {
	private BoardPanel board;
	private TurnTimerPanel timerPanel;
	private JPanel sidePanel;
	private ChatBox existingChatBox;
	public BoardFrame(Core core, ChatBox existingChatBox, VoiceChat voiceChat, Socket socket) throws IOException {
        super("Chinese Chess");
        board = core.getBoardPanel();
        timerPanel = core.getTurnTimerPanel();

		// chatbox = new ChatBox(socket);
        add(board, BorderLayout.CENTER);
		
        sidePanel = new JPanel();
        sidePanel.setLayout(new GridBagLayout()); 
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;

		// Add timerPanel
		gbc.gridy = 0;
		gbc.weighty = 2.7;
		gbc.fill = GridBagConstraints.BOTH;
		sidePanel.add(timerPanel, gbc);

		// Add existingChatBox
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weighty = 0.2;
		sidePanel.add(existingChatBox, gbc);

		JTextArea jarea = existingChatBox.getTextArea();
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weighty = 1.0;
		sidePanel.add(jarea, gbc);

		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weighty = 0.5;
		sidePanel.add(voiceChat, gbc);
        // sidePanel.add(timerPanel);
        // sidePanel.add(existingChatBox); // Thêm ChatBox vào sidePanel
		// sidePanel.add(voiceChat);
        PreviousMove previousMove = new PreviousMove(core, socket);
		System.setOut(new PrintStream(new StreamIntake(previousMove, System.out)));


		// sidePanel.add(previousMove, BorderLayout.SOUTH);
		gbc.gridy = GridBagConstraints.RELATIVE;
		gbc.weighty = 1.0;
		sidePanel.add(previousMove, gbc);

		add(sidePanel, BorderLayout.EAST);

		ActionListener saveHandler = new ActionListener() {
			//saves the board when the player presses saveItem
			public void actionPerformed(ActionEvent event) {
				//add code to save the board
				try {
					core.saveGame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		BoardMenu boardMenu = core.getBoardMenu();
		setJMenuBar(boardMenu);

		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem popupSave = new JMenuItem("Save");
		popupSave.addActionListener(saveHandler);

		popupMenu.add(popupSave);
		addMouseListener(new MouseAdapter() {
			// handle mouse press event
			public void mousePressed(MouseEvent event) {
				checkForTriggerEvent(event); // check for trigger
			}

			// handle mouse release event
			public void mouseReleased(MouseEvent event) {
				checkForTriggerEvent(event); // check for trigger
			}

			// determine whether event should trigger popup menu
			private void checkForTriggerEvent(MouseEvent event) {
				if (event.isPopupTrigger())
					popupMenu.show(
							event.getComponent(), event.getX(), event.getY());
			}
		}); // end call to addMouseListener
	}
}