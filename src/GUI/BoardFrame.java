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

public class BoardFrame extends JFrame {
	private BoardPanel board;
	private TurnTimerPanel timerPanel;
	private JPanel sidePanel;
	private ChatBox chatbox;
	public BoardFrame(Core core, ChatBox existingChatBox) throws IOException {
        super("Chinese Chess");
        board = core.getBoardPanel();
        timerPanel = core.getTurnTimerPanel();
        chatbox = existingChatBox; // Khởi tạo ChatBox
		// chatbox = new ChatBox(socket);
        add(board, BorderLayout.CENTER);

        sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(3, 0, 0, 3)); 
        sidePanel.add(timerPanel);
        sidePanel.add(chatbox); // Thêm ChatBox vào sidePanel

        PreviousMove previousMove = new PreviousMove(core);
		System.setOut(new PrintStream(new StreamIntake(previousMove, System.out)));
		sidePanel.add(previousMove);
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
