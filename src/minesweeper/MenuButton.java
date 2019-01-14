/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author library
 */
public class MenuButton extends JButton {
    String buttonAction;
    JPanel scorePanel = new JPanel();
    File highScores = new File("highScores.txt");
    JFrame scoreFrame = new JFrame();
    JPanel panel;
    Minesweeper game;
    public MenuButton(String action, Minesweeper game) {
        buttonAction = action;
        setPreferredSize(new Dimension(20, 20));
        //setText(action.substring(0,1));
        setFont(new Font("Arial", Font.PLAIN, 10));
        this.game = game;
        
    }
    public String getButtonAction() {
        return buttonAction;
    }
    public void changeDifficulty(Minesweeper game) {
        // Initialize game stats based on difficulty
        if (getButtonAction().equals("Beginner")) {
                game.NUM_MINES = 1;
                game.rows = 8;
                game.cols = 8;
                game.newGame();
                game.difficulty = 0;
            }
            else if (getButtonAction().equals("Intermediate")) {
                game.NUM_MINES = 40;
                game.rows = 16;
                game.cols = 16;  
                game.newGame();
                game.difficulty = 1;
            }
            else if (getButtonAction().equals("Expert")) {
                game.NUM_MINES = 99;
                game.rows = 16;
                game.cols = 30;
                game.newGame();
                game.difficulty = 2;
            }
            else if (getButtonAction().equals("Custom")) {
                int[] newValues = customGame(game);
                if (newValues[3] == 1) {
                    game.rows = newValues[0];
                    game.cols = newValues[1];
                    game.NUM_MINES = newValues[2];
                    game.newGame();
                    game.difficulty = -1;
                }
            }
            else {
                displayScores();
            }
    }
    public int[] customGame(Minesweeper game) {
        // Creates a new window that asks the user for the number of rows, columns
        // and mines. If valid, will create a new game with those numbers and
        // store them in a field called values.
        // values stores row, col, num mines, and valid game
        // info stored values, 
        JPanel customPanel = new JPanel();
        customPanel.setLayout(new BoxLayout(customPanel, BoxLayout.Y_AXIS));
        JLabel rowLabel = new JLabel("Insert number of rows, 1 to 30");
        JTextField numRows = new JTextField("30");
        JLabel colLabel = new JLabel("Insert number of cols, 1 to 30");
        JTextField numCols = new JTextField("30");
        JLabel mineLabel = new JLabel("Insert number of mines, 1 to 899");
        JTextField numMines = new JTextField("100");
        customPanel.add(rowLabel);
        customPanel.add(numRows);
        customPanel.add(colLabel);
        customPanel.add(numCols);
        customPanel.add(mineLabel);
        customPanel.add(numMines);
        int result = JOptionPane.showConfirmDialog(null, customPanel, "Custom Game", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        int[] values = {game.rows,game.cols,game.NUM_MINES, 0};
        if (result == JOptionPane.OK_OPTION) {
            try {
                int rows = Integer.parseInt(numRows.getText());
                int cols = Integer.parseInt(numCols.getText());
                int mines = Integer.parseInt(numMines.getText());
                
                if ((1 <= rows && rows <= 30) && (1 <= cols && cols <= 30) && 
                        (1 <= mines && mines<= rows*cols-1)) {
                    values[0] = rows;
                    values[1] = cols;
                    values[2] = mines;
                    values[3] = 1;
                }
                return values;
            }
            catch (NumberFormatException e){
                return values;
            }
        }
        else {
            return values;
        }
        
    }
    
    public void displayScores() {
        // Displays stored in a column-like fashion
        scorePanel = new JPanel(new GridLayout(11, 6));
        scorePanel.add(new JLabel("Beginner"));
        scorePanel.add(new JLabel(""));
        scorePanel.add(new JLabel("Intermediate   "));
        scorePanel.add(new JLabel(""));
        scorePanel.add(new JLabel("Expert"));
        scorePanel.add(new JLabel(""));
        
        int[][] scores = game.getScores();
        String[][] names = game.getNames();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 3; j++) {
                String name = names[j][i];
                scorePanel.add(new JLabel(name));

                int score = scores[j][i];
                JLabel tempLabel = new JLabel();
                tempLabel.setText(Integer.toString(score));
                scorePanel.add(tempLabel);
            }
        }
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(scorePanel);
        
        scoreFrame.add(panel);
        scoreFrame.pack();
        scoreFrame.setResizable(false);
        scoreFrame.setVisible(true);
    }
        
        
    
}
