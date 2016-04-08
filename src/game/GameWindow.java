/**
 * Template:
 * @author Kim Buckner
 * 
 * Current Version:
 * @author James Scott
 * @author Colin Riley
 * @author Stephen Belden
 * @author Shaya Wolf
 * @author Neil Carrico
 * @version April 7, 2016
 *
 * This is the actual "game".
 * This class handles all game logic, as well as rendering the game board.
 *
 * Three buttons at the top of the game board have been put in place.
 * The only one that should do anything is Quit.
 *
 * The 4x4 grid of tiles is in place.
 * The two side columns that hold unused tiles are in place.
 */

package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GameWindow extends JFrame implements ActionListener {
    // Avoid compiler complaints
    public static final long serialVersionUID = 1;

    /**
     * Declare Buttons
     * 
     * @author Kim Buckner
     */
    public static JButton newButton, resetButton, quitButton;

    // We don't use this and we don't know what it's for.
    // But it was in the template so we are leaving it here.
    // private int startAt = 0;

    // Data used for game logic
    public static Tile lastClicked;

    /**
     * Constructor: Sets the window name using super() and changes the layout
     * 
     * @author Kim Buckner
     *
     * @param s
     *            is the window title
     */
    public GameWindow(String s) {
        super(s);
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
    }

    /**
     * Top buttons
     * 
     * @param e
     *            is the ActionEvent
     * 
     *            BTW can ask the event for the name of the object generating
     *            event. The odd syntax for non-java people is that "exit" for
     *            instance is converted to a String object, then that object's
     *            equals() method is called.
     */
    public void actionPerformed(ActionEvent e) {
        if ("exit".equals(e.getActionCommand()))
            System.exit(0);
        if ("reset".equals(e.getActionCommand()))
            reset();//System.out.println("reset pressed\n");
        if ("new".equals(e.getActionCommand()))
            newGame();//System.out.println("new pressed\n");
    }
    
    public void newGame(){
        Main.game.dispose();
        Main.game = new GameWindow("Group E aMaze");

        // have to override the default layout to reposition things!!!!!!!
        Main.game.setSize(new Dimension(900, 1000));
        Main.game.setResizable(false);

        // So the debate here was, do I make the GameWindow object the game
        // or do I make main() the game, manipulating a window?
        // Should GameWindow methods know what they store?
        // Answer is, have the "game" do it.
        Main.game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Main.game.getContentPane().setBackground(Color.cyan);
        Main.game.setUp();
        
        Main.game.setVisible(true);
    }
    
    public void reset(){
        Main.game.dispose();
        Main.game = new GameWindow("Group E aMaze");

        // have to override the default layout to reposition things!!!!!!!
        Main.game.setSize(new Dimension(900, 1000));
        Main.game.setResizable(false);

        // So the debate here was, do I make the GameWindow object the game
        // or do I make main() the game, manipulating a window?
        // Should GameWindow methods know what they store?
        // Answer is, have the "game" do it.
        Main.game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Main.game.getContentPane().setBackground(Color.cyan);
        Main.game.setUp();
        
        Main.game.setVisible(true);
    }

    /**
     * Establishes the initial board
     */
    public void setUp() {
        // Need to play around with the dimensions and the grid x/y values
        // These constraints are going to be added to the pieces/parts I
        // stuff into the "GridBag".
        GridBagConstraints basic = new GridBagConstraints();
        basic.anchor = GridBagConstraints.FIRST_LINE_START;
        basic.gridx = 0;
        basic.gridy = 0;
        basic.gridwidth = 1;
        basic.gridheight = 1;
        basic.ipadx = 0;
        basic.ipady = 0;
        basic.fill = GridBagConstraints.RELATIVE;

        /**
         * @author Colin Riley work on tiles, grid, and reading from file
         * @author Stepen Belden (code cleanup)
         */

        // creates an array of tiles
        Tile[] tiles = null;

        // num and fnum are data containers
        int num = 0;
        float fnum = 0;

        // count is keeps track of what is being read in (tile #, # lines, etc)
        int count = -1;
        // how many lines does the tile have, how many points
        int numPoints = 0;
        int numXY = 0;

        // is the loop reading an x value or a y
        int countxy = 0;

        // the id of the tile
        int tileId = 0;

        // used to create a point
        float x = 0, y = 0;

        // an array of lines
        Point[] points = null;

        // creates a file and a path
        File file = new File("default.mze");
        Path path = Paths.get(file.getPath());

        // a 4 byte array, used for converting to ints or floats
        byte[] b = new byte[4];

        // try catch for reading from the file
        try {
            // creates an array of bytes that is the entire file
            byte[] full = Files.readAllBytes(path);

            // iterate through full in groups of four bytes
            for (int i = 0; i < file.length(); i += 4) {
                // iterate four bytes in those groups and set b to them
                for (int j = 0; j < 4; ++j) {
                    b[j] = full[i + j];
                }

                // the first value read is the number of tiles
                // create the array of tiles with this
                if (i == 0) {
                    num = convertToInt(b);
                    tiles = new Tile[num];
                }

                else {
                    // the loop is going over the id of the tile
                    // convert the input and store in title
                    if (count == -1) {
                        num = convertToInt(b);
                        tileId = num;
                        ++count;
                    }

                    /*
                     * The loop is going over the number of lines a tile has. It
                     * converts input, sets size of array, sets how many lines
                     * will be read
                     */
                    else if (count == 0) {
                        num = convertToInt(b);
                        numXY = num * 4;
                        points = new Point[num * 2];
                        ++count;
                    }

                    /*
                     * Here, the last point for this tile is being read. This
                     * creates and sets a point using the x and y values,
                     * y is being read. A new tile with the Id and points is
                     * constructed, and makeLive() is called.
                     * Counters are reset
                     */
                    else if (count == numXY) {
                        fnum = convertToFloat(b);
                        y = fnum;
                        Point p = new Point();
                        p.setLocation(x, y);
                        points[numPoints] = p;
                        Line[] lines = new Line[numXY / 4];
                        int tempLineCount = 0;
                        if(Main.verbose) System.out.println("Constructing "
                                + lines.length + " lines from " + numXY
                                + " float values");
                        for (int k = 0; k < numXY / 2; k += 2) {
                            lines[tempLineCount] = 
                                    new Line(points[k],points[k + 1]);
                            tempLineCount++;
                        }
                        tiles[tileId] = new Tile(tileId, lines);
                        tiles[tileId].makeLive();
                        countxy = 0;
                        count = -1;
                        numPoints = 0;
                    }

                    // reading points. Convert input to a float
                    else {
                        fnum = convertToFloat(b);

                        // if the count is even set x
                        if ((countxy % 2) == 0) {
                            x = fnum;
                            ++countxy;
                        }

                        // if the count is odd set y and set location of p.
                        // add p to points
                        else {
                            y = fnum;
                            Point p = new Point();
                            p.setLocation(x, y);
                            ++countxy;
                            points[numPoints] = p;
                            ++numPoints;
                        }
                        ++count;
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println("File not read\n");
        }
        
        if(Main.verbose) for (Tile t : tiles) t.debugPrint();

        // for loop to assign the 16 tiles
        // for (int i = 1; i <= 16; ++i) {
        // tiles[i - 1] = new Tile(i);
        // tiles[i - 1].makeLive();
        // }

        // nested for loop to iterate through the grid (9 rows and 7 columns)
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 8; ++j) {
                // if the first cell is selected call the addButtons method
                if (i == 0 && j == 0) {
                    this.addButtons(basic);
                }

                // if anything besides the first 3 cells of row 1 or any of row
                // 2 are selected add empty cells to the board
                else if (i == 0 && j > 2 || i == 1) {
                    // sets the grid cell
                    basic.gridx = j;
                    basic.gridy = i;

                    // create a blank label, set its size, and it to the grid
                    JLabel label = new JLabel("");
                    label.setPreferredSize(new Dimension(100, 45));
                    this.add(label, basic);
                }

                // if the cell selected is in the first column or last column,
                // but is after the first row add panels. These are on the side
                // and hold tiles before being placed
                else if (j == 0 || j == 7 && i > 0) {
                    // sets the cell
                    basic.gridx = j;
                    basic.gridy = i;

                    if (j == 0) {
                        this.add(tiles[i - 2], basic);
                    } else {
                        this.add(tiles[i - 2 + 8], basic);
                    }
                }

                // if the middle 16 cells are selected, add panels. These are
                // where the user places tiles in the game grid to play
                else if (i > 3 && i < 8 && j > 1 && j < 6) {
                    // set the cell
                    basic.gridx = j;
                    basic.gridy = i;

                    // create a panel, set its size and border, then add to grid
                    Tile panel = new Tile(-1);
                    panel.setPreferredSize(new Dimension(100, 100));
                    panel.makeEmpty();
                    this.add(panel, basic);
                }
            }
        }
        // for (int t = 0; t < 16; t++) tiles[t].debugPrint();
    }

    /**
     * Used by setUp() to configure the buttons on a button bar and add it to
     * the gameBoard Takes a GridBagConstraints to position the buttons
     * 
     * @author Colin Riley (reworked from first)
     * @param basic
     * 
     *            This doesn't need to be a function because it's only called
     *            once, but it was part of the template so we're leaving it here
     *            for now
     */
    private void addButtons(GridBagConstraints basic) {
        // create new buttons for newButton, resetButton, and quitButton
        // set their text, size, and action command

        newButton = new JButton("New Game");
        newButton.setPreferredSize(new Dimension(100, 30));
        newButton.setActionCommand("new");
        newButton.addActionListener(this);

        resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(100, 30));
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(this);

        quitButton = new JButton("Quit");
        quitButton.setPreferredSize(new Dimension(100, 30));
        quitButton.setActionCommand("exit");
        quitButton.addActionListener(this);

        // set the cells to the first row, and the first 3 cells of that
        basic.gridy = 0;

        basic.gridx = 0;
        this.add(newButton, basic);

        basic.gridx = 1;
        this.add(resetButton, basic);

        basic.gridx = 2;
        this.add(quitButton, basic);
    }

    /**
     * Handles the game logic for swapping tiles only after 2 different tiles
     * have been clicked.
     * 
     * @author Stephen Belden
     * @param clickedTile
     *            is the tile that was most recently clicked
     */
    public void setClicked(Tile clickedTile) {
        // Case in which no tile has been selected yet.
        if (lastClicked == null) {
            lastClicked = clickedTile;
            clickedTile.setBackground(Color.green);
        } else {
            // This resets the tile to it's default un-clicked state
            // if you clicked the Tile that was already selected
            if (lastClicked == clickedTile) {
                clickedTile.reset();
                lastClicked = null;
                // Case in which two tiles are clicked
            } else if (clickedTile.isEmpty() == false &&
                    lastClicked.isEmpty() == false) {
                int tempID = clickedTile.getID();
                Line[] tempLines = clickedTile.getLines();
                clickedTile.setID(lastClicked.getID());
                clickedTile.setLines(lastClicked.getLines());
                lastClicked.setID(tempID);
                lastClicked.setLines(tempLines);
                clickedTile.makeLive();
                lastClicked.makeLive();
                Main.game.repaint();
                lastClicked = null;
                // Case in which two empty game board positions are clicked
            } else if (clickedTile.isEmpty() == true &&
                    lastClicked.isEmpty() == true) {
                clickedTile.reset();
                lastClicked.reset();
                lastClicked = null;
                // Case in which one tile and one empty spot are clicked
            } else if (clickedTile.isEmpty() != lastClicked.isEmpty()) {
                int tempID = clickedTile.getID();
                Line[] tempLines = clickedTile.getLines();
                clickedTile.setID(lastClicked.getID());
                clickedTile.setLines(lastClicked.getLines());
                lastClicked.setID(tempID);
                lastClicked.setLines(tempLines);
                clickedTile.switchState();
                lastClicked.switchState();
                lastClicked = null;
            }
        }
    }

    public int readInt(FileInputStream in) throws IOException {
        byte[] b = new byte[4];
        /*
         * for(int i = 0; i < 4; ++i) { b[i] = 00000000; }
         */
        // ByteBuffer bb = ByteBuffer.allocate(4);
        int num;
        in.read(b);

        // num = convertToInt(b);

        num = ByteBuffer.wrap(b).getInt();

        System.out.println(num + "\n");

        return num;
    }

    /**
     * @author Java2s.com Following code taken from
     *         http://www.java2s.com/Book/Java/Examples/
     *         Convert_data_to_byte_array_back_and_forth.htm
     * 
     * @param value
     * @return
     */
    public static int convertToInt(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getInt();
    }

    public static float convertToFloat(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getFloat();
    }
};
