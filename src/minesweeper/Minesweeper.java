/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.Timer;
/**
 *
 * @author library
 */
public class Minesweeper {
    //arraylist for storing mine positions
    private ArrayList<Integer> mineNums;
    //info about # of mines and grid properties
    int NUM_MINES = 40;
    int rows = 16;
    int cols = 16;
    Random rand = new Random();
    int[][] grid;
    int time = 0;
    int difficulty = 1;
    
    // Determines size of buttons based on screen resolution
    // So that images are drawn by pixel density rather than absolute number of
    // pixels. Optimized for 1080p.
    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    int screenWidth = gd.getDisplayMode().getWidth();
    int screenHeight = gd.getDisplayMode().getHeight();
    
    int buttonWidth = 30;
    int buttonHeight = 30;
    
    boolean lose = false;
    boolean win = false;
    
    private JFrame frame;
    private JPanel panel;
    private JPanel gamePanel;
    private JPanel menuPanel = new JPanel();
    private JPanel facePanel = new JPanel();
    private JPanel otherPanel = new JPanel();
    Button[][] buttonGrid;
    MenuButton[] menuGrid = new MenuButton[5];
    JLabel mineLabel;
    JLabel timeLabel;
    MenuButton faceButton;
    Timer timer;
    
    // Loads high scores and name of high-score achiever files here
    File highScores = new File("highScores.txt");
    File topNames = new File("names.txt");
    
    Minesweeper game;
    
    // All image are loaded here
    static ImageIcon TILE = new ImageIcon("minesweeperTile.jpg");
    static ImageIcon MINE_TILE = new ImageIcon("mineTile.jpg");
    static ImageIcon BLANK_TILE = new ImageIcon("blankTile.jpg");
    static BufferedImage NUM_TILE = null;
    static ImageIcon FLAG_TILE = new ImageIcon("flagTile2.png");
    static ImageIcon[] NUM_TILES = new ImageIcon[8];
    static ImageIcon HAPPY_FACE = new ImageIcon("mineFace.png");
    static ImageIcon DEAD_FACE = new ImageIcon("deadFace.png");
    static ImageIcon WOW_FACE = new ImageIcon("wowFace.png");
    static ImageIcon COOL_FACE = new ImageIcon("coolFace.png");
    static ImageIcon CLICKED_MINE = new ImageIcon("clickedMine.jpg");
    static ImageIcon NOT_MINE = new ImageIcon("notMine.jpg");
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Minesweeper();
    }
    
    public Minesweeper() {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        try {
            NUM_TILE = ImageIO.read(new File("numTile.jpg"));
        } catch (IOException ex) {
            Logger.getLogger(Minesweeper.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < 8; i++) {
            ImageIcon tempTile = new ImageIcon((Image)NUM_TILE.getSubimage(
                    128*(i%4), 128*(i/4), 128, 128));
            tempTile = getScaledIcon(tempTile, buttonWidth, buttonHeight);
            NUM_TILES[i] = tempTile;
        }
        
        // Scaled all images to desired width and height
        TILE = getScaledIcon(TILE, buttonWidth, buttonHeight);
        MINE_TILE = getScaledIcon(MINE_TILE, buttonWidth, buttonHeight);
        BLANK_TILE = getScaledIcon(BLANK_TILE, buttonWidth, buttonHeight);
        FLAG_TILE = getScaledIcon(FLAG_TILE, buttonWidth, buttonHeight);
        HAPPY_FACE = getScaledIcon(HAPPY_FACE, buttonWidth, buttonHeight);
        DEAD_FACE = getScaledIcon(DEAD_FACE, buttonWidth, buttonHeight);
        WOW_FACE = getScaledIcon(WOW_FACE, buttonWidth, buttonHeight);
        COOL_FACE = getScaledIcon(COOL_FACE, buttonWidth, buttonHeight);
        CLICKED_MINE = getScaledIcon(CLICKED_MINE, buttonWidth, buttonHeight);
        NOT_MINE = getScaledIcon(NOT_MINE, buttonWidth, buttonHeight);
        
        mineGrid();
        
        timer = new Timer(1000, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (time < 999) {
                    time += 1;
                    timeLabel.setText(Integer.toString(time));
                }
            }
            
        });
        
        panel.add(menuPanel);
        panel.add(facePanel);
        panel.add(gamePanel);
        panel.add(otherPanel);
        //panel.add(timePanel);
        frame.add(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

    }
    
    private ImageIcon getScaledIcon(ImageIcon imageIcon, int width, int height) {
        //scales imageIcon to given size
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        return scaledIcon;
    }

    private void mineGrid() {
        System.out.println(buttonWidth + " " + buttonHeight);
        
        mineNums = new ArrayList<>();
        //The tiles are listed from 0 to number of tiles-1. 
        //The while loop picks random numbers that represent
        //where mines will be placed, until there is one number
        //for each mine in the arraylist.
       
        while (mineNums.size() < NUM_MINES) {
            int tile = (rand.nextInt(rows*cols));
            if (!mineNums.contains(tile)) {
                mineNums.add(tile);
            }
        }
        //creates the grid for the game
        grid = new int[rows][cols];
        for (int i = 0; i < NUM_MINES; i++) {
            //places mines into corresponding tile number
             int row = mineNums.get(i)/cols;
             int col = mineNums.get(i)%cols;
             grid[row][col] = -1;
        }
        //counts number of mines around all non mine squares
        //inputs that as value of that square
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] != -1) {
                    grid[i][j] = countMines(i, j);
                }
            }
        } 
        
        // Main panel for the game
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        // Menu buttons 
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.X_AXIS));

        // Creates the four menu buttons of the game
        String[] buttonNames = {"Beginner", "Intermediate", "Expert", "Custom"};

        for (int i = 0; i < 4; i++) {
            MenuButton button = new MenuButton(buttonNames[i], this);
            button.setPreferredSize(new Dimension(60, 30));
            button.addMouseListener(new MouseClickListener(this));
            button.setText(buttonNames[i]);
            menuPanel.add(button);
            menuGrid[i] = button;
        }
        
        // Panel containing happy face button
        facePanel = new JPanel();
        facePanel.setLayout(new BoxLayout(facePanel, BoxLayout.X_AXIS));

        // Label displaying number of mines left
        mineLabel = new JLabel(Integer.toString(NUM_MINES));
        mineLabel.setPreferredSize(new Dimension(buttonWidth * 2, buttonHeight));
        mineLabel.setHorizontalAlignment(JLabel.CENTER);
        mineLabel.setFont(new Font("Arial", Font.PLAIN, buttonHeight));
        
        // Happy face button
        faceButton = new MenuButton("New", this);
        faceButton.setPreferredSize(new Dimension(buttonWidth,buttonHeight));
        faceButton.addMouseListener(new MouseClickListener(this));
        faceButton.setIcon(HAPPY_FACE);
        
        // Label displaying time elapsed
        timeLabel = new JLabel("0");
        timeLabel.setPreferredSize(new Dimension(buttonWidth * 2, buttonHeight));
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, buttonHeight));
        
        facePanel.add(mineLabel);
        facePanel.add(Box.createHorizontalGlue());
        facePanel.add(faceButton);
        facePanel.add(Box.createHorizontalGlue());
        facePanel.add(timeLabel);

        
        //Create JPanel 
        gamePanel = new JPanel(new GridLayout(rows, cols));
        
        //Array for storing 2D grid of buttons
        buttonGrid = new Button[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                buttonGrid[i][j] = new Button(this);
                buttonGrid[i][j].setValue(grid[i][j]); 
                buttonGrid[i][j].setLocation(i, j);
                buttonGrid[i][j].addMouseListener(new MouseClickListener(this));
                buttonGrid[i][j].setIcon(TILE);
                gamePanel.add(buttonGrid[i][j]);
            }
        }
        
        // Buttons at the bottom
        
        otherPanel.setLayout(new BoxLayout(otherPanel, BoxLayout.X_AXIS));
        MenuButton scoreButton = new MenuButton("Scores", this);
        scoreButton.setPreferredSize(new Dimension(buttonWidth * 2, buttonHeight));
        scoreButton.addMouseListener(new MouseClickListener(this));
        scoreButton.setText("Scores");
        otherPanel.add(scoreButton);
        
        
    }//ends method
    public void newGame() {
        //clear buttonGrid
        frame.remove(panel);
        panel.removeAll();
        gamePanel.removeAll();
        menuPanel.removeAll();
        otherPanel.removeAll();
        
        // Generates new grdi for the game
        mineGrid();
        
        // Initializes main panel
        panel.add(menuPanel);
        panel.add(facePanel);
        panel.add(gamePanel);
        panel.add(otherPanel);

        frame.add(panel);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
        
        // Resets in-game conditions
        lose = false;
        win = false;
        Button.count = 0;
        Button.flagCount = 0;
        time = 0;
        timer.stop();
    }
    public int countMines(int row, int col) {
        //return the number of mines adjacent to location i,j
        int count = 0;
        for (int m = row-1; m <= row+1; m++) {
            for (int n = col-1; n <= col+1; n++) {
                if ((m >= 0) && (m < rows) &&
                     (n >= 0) && (n < cols)
                      && (m != row || n != col)) {
                    if (grid[m][n] == -1) {
                        count += 1;
                    }
                }
            }
        }
        return count;
        
    }
    public int[][] getScores() {
        // Reads scores files and creates 2-D array of scores
        int[][] scores = new int[3][10];
        try {
            FileReader reader = new FileReader(highScores);
            
            int count = 0;
            String tempScore = "";
            int data = 0;
            while (data != -1) {
                data = reader.read();
                
                if ((char) data == ' ') {
                    scores[count/10][count%10] = Integer.parseInt(tempScore);
                    tempScore = "";
                    count++;
                }
                else {
                    tempScore += Character.toString((char)data);
                }
                
            }
            reader.close();
        }
        catch(IOException e) {
            File f = new File("highScores.txt");
            try {
                f.createNewFile();
                
                try {
                    FileWriter writer = new FileWriter(highScores);
                    for (int i = 0; i < 30; i++) {
                        writer.write("999 ");
                    }
                    writer.close();
                }
                catch(IOException ex) {
                    System.out.println("wut");
                }
                
                return getScores();
              
            } catch (IOException ex) {
                Logger.getLogger(Minesweeper.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return scores;
    }
    
    public String[][] getNames() {
        // Same as getScores but for names
        String[][] names = new String[3][10];
        try {
            FileReader reader = new FileReader(topNames);
            
            int count = 0;
            String tempName = "";
            int data = 0;
            while (data != -1) {
                data = reader.read();
                
                if ((char) data == '*') {
                    names[count/10][count%10] = tempName;
                    tempName = "";
                    count++;
                }
                else {
                    tempName += Character.toString((char)data);
                }
                
            }
            reader.close();
        }
        catch(IOException e) {
            File f = new File("names.txt");
            try {
                f.createNewFile();
                
                try {
                    FileWriter writer = new FileWriter(topNames);
                    for (int i = 0; i < 30; i++) {
                        writer.write("N/A*");
                    }
                    writer.close();
                }
                catch(IOException ex) {
                    System.out.println("wut2");
                }
                
                return getNames();
              
            } catch (IOException ex) {
                Logger.getLogger(Minesweeper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return names;
    }
    
    public void updateScores(String userName) {
        // If a better score is achieved, will write that into scores
        // file and push all later scores back by one
        int[][] scores = getScores();
        String[][] names = getNames();
        for (int i = 0; i < 10; i++) {
            if (time < scores[difficulty][i]) {
                for (int j = 9; j > i; j--) {
                    scores[difficulty][j] = scores[difficulty][j-1];
                    names[difficulty][j] = names[difficulty][j-1];
                }
                scores[difficulty][i] = time;
                names[difficulty][i] = userName;
                break;
            }
        }
        
        try {
            FileWriter writer = new FileWriter(highScores);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 10; j++) {
                    writer.write(Integer.toString(scores[i][j]) + " ");
                }
            }
            writer.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }
        
        try {
            FileWriter writer = new FileWriter(topNames);
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 10; j++) {
                    writer.write(names[i][j] + "*");
                }
            }
            writer.close();
        }
        catch (IOException e) {
            System.out.println(e);
        }

    }
    
    void gameWin() {
        // When game is won, flag all mines, stop timer,
        // Change happy face, and if difficulty is one of 
        // Beginner, Intermediate, Expert, update the high scores
        win = true;
        for (Button[] row : buttonGrid) {
            for (Button button : row) {
                if (button.getValue() == -1) {
                    button.flag();
                }
            }
        }
        faceButton.setIcon(Minesweeper.COOL_FACE);
        timer.stop();

        if (difficulty >= 0) {
            String userName = "Anon E. Mouse";
            
            JPanel insertNamePanel = new JPanel();
            insertNamePanel.setLayout(new BoxLayout(insertNamePanel, BoxLayout.Y_AXIS));
            JLabel nameLabel = new JLabel("Insert your name (max 12 characters):");
            JTextField userNameField = new JTextField("");
            insertNamePanel.add(nameLabel);
            insertNamePanel.add(userNameField);
            int result = JOptionPane.showConfirmDialog(null, insertNamePanel, "Winner!", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                userName = userNameField.getText();
                String tempName = "";
                for (int i = 0; i < userName.length(); i++) {
                    if (userName.charAt(i) == '*') {
                        tempName += " ";
                    }
                    else {
                        tempName += userName.charAt(i);
                    }
                }
                if (userName.length() == 0) {
                    userName = "Anon E. Mouse";
                }
                else if (userName.length() > 12) {
                    System.out.println(1);
                    userName = userName.substring(0,12);
                }
            }
            
            updateScores(userName + " ");
        }
    }
    
    void gameOver() {
        lose = true;
        //loses the game and exposes all mines in the game
        for (Button[] row : buttonGrid) {
            for (Button button : row) {
                if ((!button.isFlagged() && button.getValue() == -1) || 
                   (button.isFlagged() && button.getValue() != -1)) {
                    button.expose(this);
                }
            }
        } 
        faceButton.setIcon(Minesweeper.DEAD_FACE);
        timer.stop();
    }
}
