package GUI;

import Run.Core;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


public class BoardMenu extends JMenuBar {
    private String languageKey;

    public BoardMenu(Core core) {
        JMenuItem aboutItem = new JMenuItem("About...");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(
                new ActionListener() {
                    //display message dialog when user selects About...
                    public void actionPerformed(ActionEvent event) {
                        JOptionPane.showMessageDialog(BoardMenu.this,
                                "This Chinese Chess game was created by the following members:\nVenkat Pamulapati\nMichael Yu\nAndy Jiang",
                                "About", JOptionPane.PLAIN_MESSAGE);
                    }
                }
        );
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setMnemonic('S');
        saveItem.addActionListener(new ActionListener() {
            //Saves the board when the player presses saveItem
            public void actionPerformed(ActionEvent event) {
                try {
                    core.saveGame();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.addActionListener(new ActionListener() {
            //terminate application when user clicks exitItem
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(aboutItem);
        fileMenu.add(saveItem);
        fileMenu.add(exitItem);

        //creates options to change the appearance of the board pieces
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');
        String[] language = {"English", "Chinese", "Pictures"};
        JMenu languageMenu = new JMenu("Language");
        languageMenu.setMnemonic('a');
        ButtonGroup languageButtonGroup = new ButtonGroup(); // manages languages
        JRadioButtonMenuItem englishButton = new JRadioButtonMenuItem("English"); // create item
        JRadioButtonMenuItem chineseButton = new JRadioButtonMenuItem("Chinese");
        JRadioButtonMenuItem englishPicButton = new JRadioButtonMenuItem("Pictures");
        languageMenu.add(englishButton); // add item to language menu
        languageButtonGroup.add(englishButton);
        languageMenu.add(chineseButton);
        languageButtonGroup.add(chineseButton);
        languageMenu.add(englishPicButton);
        languageButtonGroup.add(englishPicButton);
        ActionListener itemHandler = new ActionListener() {
            // process language selection to change the appearance of the pieces
            public void actionPerformed(ActionEvent event) {
                // draws each piece and its english name with user chosen colors
                if (englishButton.isSelected()) {
                    core.getBoardPanel().setLanguage("English");
                    core.getBoardPanel().userRepaint();
                }
                // displays images of each piece's chinese character
                if (chineseButton.isSelected()) {
                    core.getBoardPanel().setLanguage("Chinese");
                    core.getBoardPanel().userRepaint();
                }
                // displays pictures of each piece
                if (englishPicButton.isSelected()) {
                    core.getBoardPanel().setLanguage("Pictures");
                    core.getBoardPanel().userRepaint();
                }
            }
        };
        englishButton.addActionListener(itemHandler);
        chineseButton.addActionListener(itemHandler);
        englishPicButton.addActionListener(itemHandler);
        englishButton.setSelected(true); // select first language item

        viewMenu.add(languageMenu);
        //viewMenu.addSeparator();
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        JMenuItem rules = new JMenuItem("How to Play...");
        rules.addActionListener(new ActionListener() {
            //opens up the wikipedia page for the rules of Chinese Chess
            public void actionPerformed(ActionEvent e) {
                try {
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create("https://en.wikipedia.org/wiki/Xiangqi"));
                } catch (IOException e1) {
                    System.out.println(e1.getMessage());
                }
            }
        });
        helpMenu.add(rules);
        add(fileMenu); // add file menu to menu bar
        add(viewMenu);
        add(helpMenu);
    }

    public String getLanguage() {
        return languageKey;
    }
}
