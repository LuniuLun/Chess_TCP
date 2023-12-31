package GUI;

import Run.Core;

import javax.swing.*;

import GameLogic.Board;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

class PreviousMove extends JPanel {
    //    private JLabel systemOutput;
    private JTextArea systemOutput;

    public PreviousMove(Core core, Socket socket) {
        JLabel title = new JLabel("Previous moves");
        systemOutput = new JTextArea(10, 20);
        systemOutput.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(systemOutput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(title, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        systemOutput.setCaretPosition(systemOutput.getDocument().getLength());
        setPreferredSize(new Dimension(260, 150));


        //setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        JButton endGameBtn = new JButton("End Game");
        endGameBtn.addActionListener(new ActionListener() {
            //calls an end to the game by setting the game as a stalemate
            @Override
            public void actionPerformed(ActionEvent e) {
                Board.setWinner(0);
                try {
                    core.callEnd();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        add(endGameBtn, BorderLayout.SOUTH);
    }
    public void appendText(final String text) {
        systemOutput.setText(systemOutput.getText() + text);
    }
}

class StreamIntake extends OutputStream {

    private String string = "";
    private PreviousMove previousMove;
    private PrintStream system;

    public StreamIntake(PreviousMove previousMove, PrintStream system) {
        this.system = system;
        this.previousMove = previousMove;
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        String value = Character.toString(c);
        string += value;
        if (value.equals("\n")) {
            previousMove.appendText(string);
            string = "";
        }
        system.print(c);
    }
}