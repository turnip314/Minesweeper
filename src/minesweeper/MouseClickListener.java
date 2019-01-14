/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper;

import java.awt.event.*;
import javax.swing.SwingUtilities;

/**
 *
 * @author library
 */
public class MouseClickListener implements MouseListener {
    Minesweeper game;
    Button button;
    MenuButton menuButton;
    public MouseClickListener(Minesweeper currentGame) {
        game = currentGame;
    }

    @Override
    public void mouseClicked(MouseEvent me) { 
    }
    @Override
    public void mousePressed(MouseEvent me) {
        //finds which button was clicked by getSource
        //exposes button
        
        if (MenuButton.class.isInstance(me.getSource())) {
            MenuButton menuButton = (MenuButton) me.getSource();
            if (menuButton.getButtonAction().equals("New")) {
               game.newGame();
            }
            else {
                menuButton.changeDifficulty(game);
            }
            return;
            
        }

        Button thisButton = (Button) me.getSource();
        if (game.win || game.lose)
        {
            return;
        }
                
        
        //if double click, expose all tiles nearby
        //if number of flags around equals value of tile
        if (SwingUtilities.isLeftMouseButton(me) && 
                SwingUtilities.isRightMouseButton(me)) {
            if (thisButton.isExposed()) {
                int flagCount = 0;
                int row = thisButton.findLocation()[0];
                int col = thisButton.findLocation()[1];
                
                for (int i = row - 1; i <= row + 1; i++) {
                    for (int j = col - 1; j <= col + 1; j++) {
                        if (i >= 0 && i <= game.rows - 1 && j >= 0 && j <= game.cols - 1
                        && (i != row || j != col)) {
                            if (game.buttonGrid[i][j].isFlagged()) {
                                flagCount += 1;
                            }
                        }
                    }
                }
                if (flagCount == thisButton.getValue()) {
                    thisButton.exposeNearby();
                }
            }
        }
        
        //all other clicks on exposed tiles do nothing
        if (thisButton.isExposed()) {
            return;
        }
        
        //if right clicked, will flag or unflag tile
        if (me.isMetaDown()) {
            if (thisButton.isFlagged()) {
                thisButton.unflag();
            }
            else {
                thisButton.flag();
            }
            return;
        }
        //if is flagged, left click does nothing
        else if (thisButton.isFlagged()) {
            return;
        }
        
        // Guarantees that the first click is clear
        // Removes all the mines around the region that was initially clicked
        // And searches for available places to put them
        // Then resets the board and registers click in same place
        if (Button.count == 0) {
            int row = thisButton.findLocation()[0];
            int col = thisButton.findLocation()[1];
            int mineCount = 0;
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i >= 0 && i <= game.rows - 1 && j >= 0 && j <= game.cols - 1) {
                        
                        if (game.grid[i][j] == -1) {
                            mineCount++;
                            game.grid[i][j] = 0;
                        }
                            
                    }
                }
            }
            while (mineCount > 0) {
                int tile = game.rand.nextInt(game.rows*game.cols);
                int newRow = tile/game.cols;
                int newCol = tile%game.cols;
                if (game.grid[newRow][newCol] != -1 && 
                   (newRow < row-1 || newRow > row+1 || 
                    newCol < col-1 || newCol > col+1)) {
                    game.grid[newRow][newCol] = -1;
                    mineCount--;
                }
            }
            for (int i = 0; i < game.rows; i++) {
                for (int j = 0; j < game.cols; j++) {
                    if (game.grid[i][j] != -1) {
                        game.grid[i][j] = game.countMines(i, j);
                        
               
                    }
                }
            }
            for (int i = 0; i < game.rows; i++) {
                for (int j = 0; j < game.cols; j++) {
                    game.buttonGrid[i][j].setValue(game.grid[i][j]); 
                }
            }
                
        }
        
        
        //if no mines around, display all valuse around that
        if (thisButton.getValue() == 0) {
            thisButton.exposeNearby();
        }
        
        thisButton.expose(game);
    
        
        if (game.lose == false && game.win == false) {
            game.faceButton.setIcon(Minesweeper.WOW_FACE);
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        if (game.lose == false && game.win == false) {
            game.faceButton.setIcon(Minesweeper.HAPPY_FACE);
        }
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        
    }

    @Override
    public void mouseExited(MouseEvent me) {
        
    }
    
}
