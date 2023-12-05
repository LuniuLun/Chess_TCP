package GUI;

import Run.Core;
import Run.server.GameLogic.Board;
import Run.server.GameLogic.Move;
import Run.server.GameLogic.Point;
import Run.server.GameLogic.Pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BoardPanel extends JPanel {
	protected Board board;
	private String language = "English";
	// private JLabel[][] pointIcons = new JLabel[10][9];
	private Icons[][] pointIcons = new Icons[10][9];
	private int squareWidth;
	private int[] pressLoc = new int[2], releaseLoc = new int[2];
	private boolean pressed = false, pressIsValid = false;
	private Core core;
	private Profile profile;
	private DataInputStream dis;
	private DataOutputStream dos;

	/**
	 * Takes in all of the components of the board from the core class to draw them
	 * on the GUI.
	 *
	 * @param core the current running core object
	 * @throws IOException
	 */
	public BoardPanel(Core core, Socket socket, String direction) throws IOException {
		// setSize(500, 500);
		// board = new Board();
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		this.core = core;
		this.board = core.getBoard();
		this.profile = core.getProfile();
		this.setBackground(profile.background());
		// Loop initializes the 2d array of jlabels on the board
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				pointIcons[y][x] = new Icons(x, y, board.getPoint(x, y), direction);
				// pointIcons[y][x] = new JLabel();
				// pointIcons[y][x].addMouseListener(handler);
				// pointIcons[x][y].setBackground(Color.black);
				// pointIcons[x][y].setOpaque(true);
				add(pointIcons[y][x]);
			}
		}
		setLayout(null);
		// board.tryMove(new Move(4, 9, 4, 8));
	}

	void setLanguage(String language) {
		this.language = language;
	}

	public void paintComponent(Graphics g) {
		int xDisplacement, yDisplacement;
		// sets a new width for the grid squares
		if (8 * getHeight() > 9 * getWidth())
			squareWidth = getWidth() / 10;
		else
			squareWidth = getHeight() / 10;
		int radius = squareWidth * 4 / 10;

		// this loop sets new positions for each of the points in the grid for if the
		// board is resized.
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				xDisplacement = (4 - x) * squareWidth;
				yDisplacement = (10 - 2 * y) * squareWidth / 2 - squareWidth / 3;
				board.getPoint(x, y).setPosition(getWidth() / 2 - xDisplacement, 8 + getHeight() / 2 - yDisplacement);
				pointIcons[y][x].setLocation(board.getPoint(x, y).getX() - radius,
						board.getPoint(x, y).getY() - radius);
				pointIcons[y][x].setSize(radius * 2, radius * 2);
			}
		}
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		drawBoard(g2);
		drawRiver(g2);
		// drawPieces(g2);
	}

	/**
	 * Draws the lines on the board.
	 */
	private void drawBoard(Graphics2D g2) {
		g2.setColor(profile.getLineColor());
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				if (x == 8)
					continue;
				g2.drawLine(board.getPoint(x, y).getX(), board.getPoint(x, y).getY(), board.getPoint(x + 1, y).getX(),
						board.getPoint(x + 1, y).getY());
			}
			for (int x = 0; x < 9; x++) {
				if (y == 9)
					continue;
				else if (y == 4 && x < 8 && x > 0)
					continue;
				g2.drawLine(board.getPoint(x, y).getX(), board.getPoint(x, y).getY(), board.getPoint(x, y + 1).getX(),
						board.getPoint(x, y + 1).getY());
			}
		}
		// draws the middle square
		g2.drawLine(board.getPoint(3, 0).getX(), board.getPoint(3, 0).getY(), board.getPoint(5, 2).getX(),
				board.getPoint(5, 2).getY());
		g2.drawLine(board.getPoint(5, 0).getX(), board.getPoint(5, 0).getY(), board.getPoint(3, 2).getX(),
				board.getPoint(3, 2).getY());
		g2.drawLine(board.getPoint(3, 7).getX(), board.getPoint(3, 7).getY(), board.getPoint(5, 9).getX(),
				board.getPoint(5, 9).getY());
		g2.drawLine(board.getPoint(5, 7).getX(), board.getPoint(5, 7).getY(), board.getPoint(3, 9).getX(),
				board.getPoint(3, 9).getY());
		// marks up the top half of the board
		drawPoint(g2, board.getPoint(1, 2), 0, 360);
		drawPoint(g2, board.getPoint(7, 2), 0, 360);
		drawPoint(g2, board.getPoint(0, 3), -90, 90);
		drawPoint(g2, board.getPoint(2, 3), 0, 360);
		drawPoint(g2, board.getPoint(4, 3), 0, 360);
		drawPoint(g2, board.getPoint(6, 3), 0, 360);
		drawPoint(g2, board.getPoint(8, 3), 90, 270);
		// marks up the bottom half of the board
		drawPoint(g2, board.getPoint(0, 6), -90, 90);
		drawPoint(g2, board.getPoint(2, 6), 0, 360);
		drawPoint(g2, board.getPoint(4, 6), 0, 360);
		drawPoint(g2, board.getPoint(6, 6), 0, 360);
		drawPoint(g2, board.getPoint(8, 6), 90, 270);
		drawPoint(g2, board.getPoint(1, 7), 0, 360);
		drawPoint(g2, board.getPoint(7, 7), 0, 360);
	}

	private void drawPoint(Graphics2D g2, Point point, int startAngle, int endAngle) {
		for (int x = startAngle / 90; x < endAngle / 90; x++) {
			g2.drawLine(point.getX() + (int) (Math.cos(Math.toRadians(45 + 90 * x)) * squareWidth / 10),
					point.getY() + (int) (Math.sin(Math.toRadians(45 + 90 * x)) * squareWidth / 10),
					point.getX() + (int) (Math.cos(Math.toRadians(45 + 90 * x)) * squareWidth / 10),
					point.getY() + (int) (Math.sin(Math.toRadians(45 + 90 * x)) * squareWidth / 5));
			g2.drawLine(point.getX() + (int) (Math.cos(Math.toRadians(45 + 90 * x)) * squareWidth / 10),
					point.getY() + (int) (Math.sin(Math.toRadians(45 + 90 * x)) * squareWidth / 10),
					point.getX() + (int) (Math.cos(Math.toRadians(45 + 90 * x)) * squareWidth / 5),
					point.getY() + (int) (Math.sin(Math.toRadians(45 + 90 * x)) * squareWidth / 10));
		}
	}

	/**
	 * Draws the word "river" on the river if the language is set to English
	 *
	 * @param g2
	 */
	private void drawRiver(Graphics2D g2) {
		if (language.equals("English")) {
			g2.setFont(new Font("Sans_Serif", Font.PLAIN, 18));
			FontMetrics metrics = g2.getFontMetrics();
			int xCoord = (getWidth() - metrics.stringWidth("River")) / 2;
			int yCoord = getHeight() / 2 - metrics.getHeight() / 2 + metrics.getAscent();
			g2.drawString("River", xCoord, yCoord);
		}
	}

	/**
	 * Manually calls a repaint of every part of the object.
	 */
	public void userRepaint() {
		repaint();
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 9; x++) {
				pointIcons[y][x].repaint();
			}
		}
	}

	/**
	 * Defines the JLabel that will exist on every point on the board.
	 */
	private class Icons extends JLabel implements MouseListener {
		private int x, y;
		private Point point;
		private String direction;

		Icons(int x, int y, Point point, String direction) {
			this.x = x;
			this.y = y;
			this.point = point;
			this.direction = direction;
			setHorizontalAlignment(JLabel.CENTER);
			setVerticalAlignment(JLabel.CENTER);
			// if (point.getPiece() != null)
			// setText(point.getPiece().toString());
			addMouseListener(this);
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			this.point = board.getPoint(x, y);
			for (int y = 0; y < 10; y++) {
				for (int x = 0; x < 9; x++) {
					pointIcons[y][x].setIcon(null);
				}
			}
			switch (language) {
				case "English":
					createEnglishPieces(g2, direction);
					break;
				case "Pictures":
					setPieceImages(g2, "/Pictures/english_");
					break;
				case "Chinese":
					setPieceImages(g2, "/Pictures/chinese_");
					break;
			}
		}

		void createEnglishPieces(Graphics2D g2, String direction) {
			setIcon(null);
			g2.setFont(new Font("Sans_Serif", Font.PLAIN, 12));
			FontMetrics metrics = g2.getFontMetrics();
			int xCoord, yCoord;
			if (point.getPiece() != null) {
				g2.setColor(profile.getForeGround());
				g2.fillOval(3, 3, getWidth() - 6, getHeight() - 6);
				if (direction.equals("UP")) {
					if (point.getPiece().getSide() == Piece.Side.UP)
						g2.setColor(profile.getP2Color());
					else
						g2.setColor(profile.getP1Color());
				} else {
					if (point.getPiece().getSide() == Piece.Side.UP)
						g2.setColor(profile.getP1Color());
					else
						g2.setColor(profile.getP2Color());
				}
				g2.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
				xCoord = (getWidth()) / 2 - metrics.stringWidth(point.getPiece().toString()) / 2;
				yCoord = (getHeight()) / 2 - metrics.getHeight() / 2 + metrics.getAscent();
				g2.drawString(point.getPiece().toString(), xCoord, yCoord);
				if (pressed && point.getPiece().equals(board.getPoint(pressLoc[0], pressLoc[1]).getPiece())) {
					BasicStroke s = new BasicStroke(3);
					g2.setStroke(s);
					g2.setColor(Color.yellow);
					g2.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
				}
				if (point.getPiece().isInteractive() == false) {
					setEnabled(false);
				}
			}
		}

		void setPieceImages(Graphics2D g2, String fileName) {
			try {
				if (point.getPiece() != null) {
					fileName += point.getPiece().getImageName();
					Image scaledImage = new ImageIcon(getClass().getResource(fileName)).getImage();
					scaledImage = scaledImage.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);

					setIcon(new ImageIcon(scaledImage));
					g2.setClip(new Ellipse2D.Float(2, 2, getWidth() - 4, getHeight() - 4));
					g2.drawImage(scaledImage, 0, 0, null);
					// g2.setClip(null);
					if (pressed && point.getPiece().equals(board.getPoint(pressLoc[0], pressLoc[1]).getPiece())
							&& point.getPiece().isInteractive() == true) {
						BasicStroke s = new BasicStroke(3);
						g2.setStroke(s);
						g2.setColor(Color.yellow);
						g2.drawOval(2, 2, getWidth() - 4, getHeight() - 4);
					}
				}
			} catch (Exception e) {
				System.out.print(e);
			}
		}

		@Override
		// port,
		public void mouseClicked(MouseEvent e) {
			if (!pressed && isEnabled()) {
				if (storePressed()) {
					pressed = true;
				}
			}
			// if a piece has been selected, perform this first block of code
			else {
				if (storeReleased()) {
					// stores new pressed piece if the two locations have pieces that are on the
					// same side
					// so that you can choose a new piece without calling the move method
					if (board.getPoint(pressLoc[0], pressLoc[1]).getPiece() != null &&
							board.getPoint(releaseLoc[0], releaseLoc[1]).getPiece() != null &&
							(board.getPoint(pressLoc[0], pressLoc[1]).getPiece().getSide() == board
									.getPoint(releaseLoc[0], releaseLoc[1]).getPiece().getSide())
							|| board.getPoint(pressLoc[0], pressLoc[1]).getPiece().isInteractive() == false) {
						storePressed();
					} else {
						pressed = false;
						try {
							dos.writeBoolean(false);
							sendMove(new Move(pressLoc[0], pressLoc[1], releaseLoc[0], releaseLoc[1]));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			for (int y = 0; y < 10; y++) {
				for (int x = 0; x < 9; x++) {
					pointIcons[y][x].repaint();
				}
			}
		}

		/**
		 * Stores the piece that the mouse is pressed on.
		 */
		@Override
		public void mousePressed(MouseEvent e) {
			if (!pressed) {
				storePressed();
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// System.out.println("Released: " +e.getX() + " " + e.getY());
			// System.out.println("Released: " +x + " " + y);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// mouseReleased(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		/**
		 * Sends the move to be handled in the core class.
		 * 
		 * @throws IOException
		 */
		void sendMove(Move move) {
			core.playMove(move);
			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
				objectOutputStream.writeObject(move);
				byte[] data = byteArrayOutputStream.toByteArray();

				dos.writeInt(data.length); // Ghi kích thước dữ liệu vào DataOutputStream
				dos.write(data); // Ghi dữ liệu vào DataOutputStream

				dos.flush(); // Đảm bảo dữ liệu được gửi đi
				System.out.println("Da gui move den server");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/**
		 * Stores the location of the pressed piece.
		 */
		boolean storePressed() {
			if (board.getPoint(x, y).getPiece() == null)
				pressIsValid = false;
			else {
				pressIsValid = true;
				pressLoc[0] = x;
				pressLoc[1] = y;
				return true;
			}
			return false;
		}

		/**
		 * Stores the location of the released piece.
		 */
		boolean storeReleased() {
			if (pressIsValid) {
				releaseLoc[0] = x;
				releaseLoc[1] = y;
				return true;
			}
			return false;
		}
	}
}
