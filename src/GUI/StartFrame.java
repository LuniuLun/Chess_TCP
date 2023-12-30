package GUI;

import Run.Core;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.*;

import GameLogic.Move;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * This is the starting menu. It accepts user input on a gui to create a profile
 * object which is passed to core.start()
 * <p>
 * It has a very nice live preview which is based on the current profile.
 */
public class StartFrame extends JFrame {

    private JPanel tippyTop, top, middle, bottom; // larger containers
    private JPanel topLeftPanel, topRightPanel; // medium containers
    private JPanel p1Colors, p1Names, bgColors, timers, fgColors, lineColors; // basic containers
    private JButton p1Chooser, bgChooser, begin, loadGame, fgChooser, lineChooser;
    private JTextField p1Name;
    private BoardFrame boardFrame;
    private JSpinner minutes;
    private Profile[] themes;
    private JComboBox profileSelector;
    private Image logo;
    private Profile profile;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean gameCreated = false; // Thêm biến boolean

    public static AudioFormat getAudioFormat() {
        Float sampleRate = 8000.0F;
        Integer sampleSizeInbits = 16;
        Integer channel = 2;
        Boolean signed = true;
        Boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInbits, channel, signed, bigEndian);
    }

    public SourceDataLine audio_out;

    public StartFrame(Core core) {
        super("Start Menu");
        this.setLayout(new GridLayout(4, 0));
        logo = new ImageIcon(getClass().getResource("/Pictures/CC_logo.png")).getImage();
        JLabel logoPic = new JLabel(new ImageIcon(logo.getScaledInstance(120, 120, 50)));

        // To make a theme make the profile, add it to the themes array, and a string to
        // match in themes2
        profile = new Profile();
        Profile dark = new Profile(Color.magenta, new Color(0, 255, 132), Color.DARK_GRAY, Color.LIGHT_GRAY,
                Color.BLACK);
        Profile light = new Profile(Color.pink, Color.LIGHT_GRAY, new Color(230, 216, 195), Color.white,
                Color.DARK_GRAY);
        Profile minimal = new Profile(Color.LIGHT_GRAY, Color.LIGHT_GRAY, new Color(86, 86, 86), Color.BLACK,
                Color.white);
        Profile basic = new Profile();

        themes = new Profile[4];
        themes[0] = basic;
        themes[1] = dark;
        themes[2] = light;
        themes[3] = minimal;

        String[] themes2 = new String[themes.length];
        themes2[0] = "Basic";
        themes2[1] = "Dark";
        themes2[2] = "Light";
        themes2[3] = "Minimal";

        Font bigFont = new Font("Sans_Serif", Font.PLAIN, 40);
        Font mediumFont = new Font("Sans_Serif", Font.BOLD, 15);

        // tippy top stuff
        tippyTop = new JPanel(new GridLayout(0, 2));
        JLabel rightTitle = new JLabel("Xiang Qi");
        rightTitle.setFont(bigFont);
        rightTitle.setHorizontalAlignment(SwingConstants.LEFT);
        logoPic.setHorizontalAlignment(SwingConstants.RIGHT);
        tippyTop.add(logoPic);
        tippyTop.add(rightTitle);

        // Player 1 stuff

        JPanel p1TitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        JLabel p1Title = new JLabel("Player 1:");
        p1Title.setFont(mediumFont);
        p1TitlePanel.add(p1Title);

        p1Names = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5));
        p1Names.add(new JLabel("Name: "));
        p1Name = new JTextField("Player 1", 12);
        p1Names.add(p1Name);

        p1Colors = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        p1Colors.add(new JLabel("Color: "));
        p1Chooser = new JButton("Select");
        p1Colors.add(p1Chooser);

        topLeftPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 5));
        topLeftPanel.add(p1TitlePanel);
        topLeftPanel.add(p1Names);
        topLeftPanel.add(p1Colors);

        // top panel stuff
        top = new JPanel(new GridLayout(0, 2));
        top.add(topLeftPanel);

        // background stuff
        bgColors = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 5));
        bgColors.add(new JLabel("Background Color"));
        bgChooser = new JButton("Select");
        bgColors.add(bgChooser);

        // foreground stuff
        fgColors = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 5));
        fgColors.add(new JLabel("  Foreground Color"));
        fgChooser = new JButton("Select");
        fgColors.add(fgChooser);

        // lineColor stuff
        lineColors = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 5));
        lineColors.add(new JLabel("             Line Colors"));
        lineChooser = new JButton("Select");
        lineColors.add(lineChooser);

        // timer stuff
        timers = new JPanel(new FlowLayout(FlowLayout.RIGHT, 30, 5));
        timers.add(new JLabel("Time Limit"));
        minutes = new JSpinner(new SpinnerNumberModel(10, 1, 60, 1));
        timers.add(minutes);

        // Theme Selector Stuff
        JPanel ComboPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 10));
        profileSelector = new JComboBox(themes2);
        ComboPanel.add(new JLabel("Or select a default theme:"));
        ComboPanel.add(profileSelector);
        ComboPanel.add(timers);

        // middle stuff
        middle = new JPanel(new GridLayout(0, 2));
        JPanel middleLeftHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        middleLeftHolder.add(bgColors);
        middleLeftHolder.add(fgColors);
        middleLeftHolder.add(lineColors);
        JPanel middleRightHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        middleRightHolder.add(ComboPanel);

        middle.add(middleLeftHolder);
        middle.add(ComboPanel);

        // bottom stuff

        bottom = new JPanel(new GridLayout(0, 2));
        begin = new JButton("Start New Game");

        JPanel preview2 = new preview();
        JPanel beginPanel = new JPanel();
        beginPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        beginPanel.add(begin, CENTER_ALIGNMENT);
        bottom.add(preview2);
        bottom.add(beginPanel);

        // Action listeners

        p1Chooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color temp = JColorChooser.showDialog(null, "Choose Player 1 Color", profile.getP1Color());
                profile.setP1Color(temp);
                preview2.repaint();
            }
        });

        bgChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color temp = JColorChooser.showDialog(null, "Choose Background Color", profile.getBackground());
                profile.setBackGround(temp);
                preview2.repaint();
            }
        });

        fgChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color temp = JColorChooser.showDialog(null, "Choose Foreground Color", Color.LIGHT_GRAY);
                profile.setForeGround(temp);
                preview2.repaint();
            }
        });

        lineChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color temp = JColorChooser.showDialog(null, "Choose Line Color", profile.getLineColor());
                profile.setLineColor(temp);
                preview2.repaint();
            }
        });

        profileSelector.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseProfile();
                preview2.repaint();
            }
        });

        begin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer(core);
            }
        });

        add(tippyTop);
        add(top);
        add(middle);
        add(bottom);

        this.setPreferredSize(new Dimension(600, 600));
        this.pack();
        this.setResizable(false);
        setVisible(true);
    }

    private void connectToServer(Core core) {
        try {
            socket = new Socket("localhost", 6969);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            sendStartGameRequest(profile);
            System.out.println("Gui yeu cau ket noi");
            Integer port = dis.readInt();
            while (!gameCreated) { // Sửa đổi điều kiện vòng lặp
                Boolean isString = dis.readBoolean();
                if (isString) {
                    String messageResponse = dis.readUTF();
                    System.out.println(messageResponse);
                    if (messageResponse.equals("UP")) {
                        createBoard(core, "UP", port);
                    } else if (messageResponse.equals("DOWN")) {
                        createBoard(core, "DOWN", port);
                    }
                    gameCreated = true;
                }
            }

            Thread receiveThread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            boolean isString = dis.readBoolean();
                            if (isString == false) {
                                int dataSize = dis.readInt();
                                byte[] data = new byte[dataSize];
                                dis.readFully(data); // Đọc dữ liệu từ DataInputStream vào mảng byte data
                                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                                        ObjectInputStream objectInputStream = new ObjectInputStream(
                                                byteArrayInputStream)) {
                                    Move move = (Move) objectInputStream.readObject();
                                    Move reciveMove = new Move(8 - move.getOriginX(), 9 - move.getOriginY(),
                                            8 - move.getFinalX(), 9 - move.getFinalY());
                                    core.playMove(reciveMove);
                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                String message = dis.readUTF();
                                core.getChatBox().displayMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
            receiveThread.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createBoard(Core core, String direction, int port) throws IOException {
        profile.setMinutes((int) minutes.getValue());
        profile.setP1String(p1Name.getText());
        profile.setP2String("Competitor");
        core.start(profile, direction, socket, port);

        // core.getBoardPanel().setProfile(profile);
        boardFrame = core.getBoardFrame();
        // core.getBoardPanel().setProfile(profile);
        boardFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        boardFrame.setSize(900, 800);
        boardFrame.setVisible(true);
        setVisible(false);
    }

    // Add this method to send a start game request to the server
    public void sendStartGameRequest(Profile profile) {
        try {
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            dos.writeBoolean(true);
            dos.writeUTF("CreateRound"); // or any other suitable message
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    private void chooseProfile() {
        this.profile = themes[profileSelector.getSelectedIndex()];
    }

    // Preview Panel inner class
    class preview extends JPanel {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(profile.getBackground());
            int xOffset = 45;
            int yOffset = 10;
            g2.fill(new Rectangle2D.Double(xOffset, yOffset, 200, 100));

            g2.setColor(profile.getLineColor());
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(0 + xOffset, 50 + yOffset, 200 + xOffset, 50 + yOffset);
            g2.drawLine(50 + xOffset, 0 + yOffset, 50 + xOffset, 100 + yOffset);
            g2.drawLine(150 + xOffset, 0 + yOffset, 150 + xOffset, 100 + yOffset);

            g2.setColor(profile.getForeGround());
            g2.fill(new Ellipse2D.Double(xOffset + 10, 10 + yOffset, 80, 80));

            g2.setStroke(new BasicStroke(5));
            g2.setColor(profile.getP1Color());
            g2.draw(new Ellipse2D.Double(10 + xOffset, 10 + yOffset, 80, 80));
            g2.drawString("General", 27 + xOffset, 55 + yOffset);
        }
    }
}
