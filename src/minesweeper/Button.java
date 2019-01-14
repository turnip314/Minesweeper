/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author library
 */
public class Button extends JButton {
    Minesweeper game;
    private int value;
    private int[] location = new int[2];
    private boolean exposed = false;
    private boolean flagged = false;
    //counts how many mines exposed
    static int count = 0;
    static int flagCount = 0;
    public Button(Minesweeper currentGame) {
        setPreferredSize(new Dimension(30, 30));
        game = currentGame;
    }
    public void setValue(int num) {
        //sets value of button according to grid
        value = num;
    }
    public int getValue() {
        //gives value stored in button
        return value;
    }
    @Override
    public void setLocation(int row, int col) {
        //set location of button [row, col]
        location[0] = row;
        location[1] = col;
    }
    public int[] findLocation() {
        //gives location of button
        return location;
    }
    public void expose(Minesweeper game) {
        if (exposed) {
            return;
        }
        
        if (count == 0) {
            game.timer.start();
               
        }
        
        
        //can change exposed status of button
        exposed = true;

        if (value == 0) {
            setIcon(Minesweeper.BLANK_TILE);
        }
        else if (value == -1) {
            if (!game.lose) {
                setIcon(Minesweeper.CLICKED_MINE);
            }
            else {
                setIcon(Minesweeper.MINE_TILE);
            }
            game.gameOver();
            return;
        }
        else {
            setIcon(Minesweeper.NUM_TILES[value-1]);
        }
        
        if (value != -1 && game.lose && isFlagged()) {
            setIcon(Minesweeper.NOT_MINE);
        }
        
        //win game 
        count += 1;
        if (count == game.rows * game.cols - game.NUM_MINES && !game.lose) {
            game.gameWin(); 
        }
        
    }
    
    
    public boolean isExposed() {
        //gives whether button is exposed or not
        return exposed;
    }
    public void flag() {
        if (isFlagged()) {
            return;
        }
        flagged = true;
        setIcon(Minesweeper.FLAG_TILE);
        flagCount++;
        game.mineLabel.setText(Integer.toString(game.NUM_MINES - flagCount));
    }
    public void unflag() {
        flagged = false;
        setIcon(Minesweeper.TILE);
        flagCount--;
        game.mineLabel.setText(Integer.toString(game.NUM_MINES - flagCount));
    }
    public boolean isFlagged() {
        return flagged;
    }
    
    void exposeNearby() {
        int row = findLocation()[0];
        int col = findLocation()[1];
        //iterates through a 3 by 3 grid around the 0 tile
        //if tile value is 0, expose tiles near that too
        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i <= game.rows - 1 && j >= 0 && j <= game.cols - 1
                        && (i != row || j != col)) {
                    Button button = game.buttonGrid[i][j];
                    if (!button.isExposed() && !button.isFlagged()) {                      
                        button.expose(game);
                        if (button.getValue() == 0) {
                            button.exposeNearby();
                        } 
                    }
                }
            }
        }
    }
}
