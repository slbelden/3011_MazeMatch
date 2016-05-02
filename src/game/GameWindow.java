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
 * @version April 29, 2016
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameWindow extends JFrame implements ActionListener {
    // Avoid compiler complaints
    public static final long   serialVersionUID = 1;

    /**
     * Declare Buttons
     * 
     * @author Kim Buckner
     */
    public static JButton      fileButton, resetButton, quitButton, saveButton,
            loadButton;

    // Data used for game logic
    private static Tile        lastClicked;

    // creates an array of tiles
    private static Tile[]      tiles            = new Tile[16];
    private static Tile[]      grid             = new Tile[16];

    // Data for saving and loading
    private boolean            played           = false;

    private GridBagConstraints basic            = new GridBagConstraints();

    /**
     * Constructor: Sets the window name using super() and changes the layout
     * 
     * @author Kim Buckner
     * @param s
     *            is the window title
     */
    public GameWindow(String s) {
        super(s);
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
        this.setSize(new Dimension(900, 1000));
        this.setMinimumSize(this.getPreferredSize());
        this.setResizable(false);
    }

    /**
     * Top buttons
     * 
     * @author Stephen Belden
     * @param e
     *            is the ActionEvent BTW can ask the event for the name of the
     *            object generating event. The odd syntax for non-java people is
     *            that "exit" for instance is converted to a String object, then
     *            that object's equals() method is called.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        int n = -1;
        if ("exit".equals(e.getActionCommand())) {
            if (played) {
                String[] options = { "Save", "Don't Save", "Cancel" };
                n = JOptionPane.showOptionDialog(this,
                        "Do you want to save your current game?", "Save?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[2]);
                switch (n) {
                case 0:
                    boolean result = saveGame();
                    if (result)
                        System.exit(0);
                    break;
                case 1:
                    System.exit(0);
                default:
                    break;
                }
            } else {
                System.exit(0);
            }
        }

        if ("reset".equals(e.getActionCommand()))
            reset();// System.out.println("reset pressed\n");
        if ("file".equals(e.getActionCommand()))
            file();// System.out.println("new pressed\n");
        if ("save".equals(e.getActionCommand()))
            saveGame();
        if ("load".equals(e.getActionCommand()))
            loadGame();
    }

    /**
     * @author James Scott
     */
    public void file() {
        if (saveButton.isVisible() == false) {
            saveButton.setVisible(true);
            loadButton.setVisible(true);
        } else {
            saveButton.setVisible(false);
            loadButton.setVisible(false);
        }
    }

    /**
     * @author Stephen Belden
     */
    public void loadGame() {
        int n = -1;
        if (played) {
            String[] options = { "Save", "Don't Save", "Cancel" };
            n = JOptionPane.showOptionDialog(this,
                    "Do you want to save your current game?", "Save?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
        }
        switch (n) {
        case 0:
            saveGame();
        case 1:
        case -1:
            final JFileChooser chose = new JFileChooser();
            chose.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int response = chose.showOpenDialog(this);
            if (!chose.getSelectedFile().exists()) {
                JOptionPane.showMessageDialog(this,
                        "The selected file does not exist.", "Load Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (response == JFileChooser.APPROVE_OPTION) {
                File openFile = chose.getSelectedFile();
                if (Main.verbose) {
                    System.out.println("Attempted to read:");
                    System.out.println(openFile.getAbsolutePath());
                }
                newWindow();
                Main.game.setUp(openFile, true, false);
            }
            break;
        default:
            break; // Cancel option, so do nothing else
        }

        // New Game Code
        /*
         * Main.game.dispose(); Main.game = new GameWindow("Group E Maze");
         * Main.game.setSize(new Dimension(900, 1000));
         * Main.game.setResizable(false);
         * Main.game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         * Main.game.getContentPane().setBackground(Color.cyan);
         * Main.game.setUp(true); Main.game.setVisible(true);
         */
    }

    /**
     * @author Shaya Wolf
     */
    public boolean saveGame() {
        boolean bool = false;
        JFileChooser chose = new JFileChooser();
        chose.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int response = chose.showSaveDialog(saveButton);
        if (response == JFileChooser.CANCEL_OPTION) {
            // Do nothing
        } else if (chose.getSelectedFile().exists()) {
            int n = -1;
            File file = chose.getSelectedFile();
            String path = file.getAbsolutePath();
            Object[] options = { "Yes", "No" };
            n = JOptionPane.showOptionDialog(this,
                    "The file you selected already exists.\n"
                            + "Do you want to overwrite this file?",
                    "Overwrite?", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (n == 0) {
                try {
                    writeFile(path, fileOutArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                saveGame();
            }
        } else if (response == JFileChooser.APPROVE_OPTION) {
            File file = chose.getSelectedFile();
            String path = file.getAbsolutePath();
            try {
                writeFile(path, fileOutArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            bool = true;
            played = false; // Don't ask to save again until another move
        } else {
            JOptionPane.showMessageDialog(this,
                    "An error occured while trying to save the file.",
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            saveGame(); // Try until Cancel or Approve
        }
        return bool;
    }

    /**
     * @author Colin Riley
     * @author James Scott
     */
    public void reset() {
        newWindow();
        Main.game.setUp(null, false, false);
    }

    private void newWindow() {
        // when reset is pressed, it pulls the window data here
        int windowWidth = Main.game.getWidth();
        int windowHeight = Main.game.getHeight();
        int x = Main.game.getX();
        int y = Main.game.getY();

        // tears down the window and makes a new one
        Main.game.dispose();
        Main.game = new GameWindow("Group E Maze");

        // Main.game.setSize(new Dimension(900, 1000));
        Main.game.setResizable(false);
        // sets the window x and y to the original locations
        Main.game.setBounds(x, y, windowWidth, windowHeight);

        Main.game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Main.game.getContentPane().setBackground(Color.cyan);
        Main.game.setVisible(true);
    }

    /**
     * Establishes the initial board
     * 
     * @author Colin Riley work on tiles, grid, and reading from file
     * @author Stepen Belden (code cleanup)
     */
    public void setUp(File file, Boolean newGame, Boolean shuffle) {
        int gridCount = 0;

        // Need to play around with the dimensions and the grid x/y values
        // These constraints are going to be added to the pieces/parts I
        // stuff into the "GridBag".

        basic.anchor = GridBagConstraints.FIRST_LINE_START;
        basic.gridx = 0;
        basic.gridy = 0;
        basic.gridwidth = 1;
        basic.gridheight = 1;
        basic.ipadx = 0;
        basic.ipady = 0;
        basic.fill = GridBagConstraints.RELATIVE;
        // basic.gridheight = 2; // comment this out

        // Sets the initial tile state for the reset button
        if (newGame == true) {
            // reads from default on initial run. Adds value to the tiles and
            // draws lines on them
            readFromFile(file);
            if (shuffle)
                shuffleArray(tiles);
            for (int i = 0; i < tiles.length; i++) {
                //System.out.println("tile id " + tiles[i].getID());
                Main.initialTileState[i] = new Tile(tiles[i]);
                if (Main.initialTileState[i].getID() != -1)
                    Main.initialTileState[i].makeLive();
                else
                    Main.initialTileState[i].makeEmpty();
                int ID = grid[i].getID();
                System.out.println("grid id" + ID);
                Main.initialGridState[i] = new Tile(grid[i]);
                if (Main.initialGridState[i].getID() != -1)
                    Main.initialGridState[i].makeLive();
                else
                    Main.initialGridState[i].makeEmpty();
            }
        } else {
            for (int i = 0; i < tiles.length; i++) {
                tiles[i] = new Tile(Main.initialTileState[i]);
                if (tiles[i].getID() != -1)
                    tiles[i].makeLive();
                else
                    tiles[i].makeEmpty();
                grid[i] = new Tile(Main.initialGridState[i]);
                if (grid[i].getID() != -1)
                    grid[i].makeLive();
                else
                    grid[i].makeEmpty();

            }
        }

        if (Main.verbose)
            for (Tile t : tiles)
                t.debugPrint();

        // nested for loop to iterate through the grid (9 rows and 7 columns)
        for (int i = 0; i < 10; ++i) {
            for (int j = 0; j < 8; ++j) {
                // if the first cell is selected call the addButtons method
                if (i == 0 && j == 0) {
                    this.addButtons(basic);
                } else if (i == 0 && j > 2 || i == 1) {
                    emptyRow(basic, i, j, tiles);
                } else if (j == 0 || j == 7 && i > 0) {
                    sidePanels(basic, i, j, tiles);
                } else if (i > 3 && i < 8 && j > 1 && j < 6) {
                    centerTiles(basic, i, j, gridCount);
                    ++gridCount;
                }
            }
        }
    }

    /**
     * @author Colin Riley
     * @param file
     * @param path
     */
    public static void readFromFile(File file) {
        // counter that changes which array is being filled
        int tileOrGrid = 0;

        // data containers
        int num = 0;
        float fnum = 0;

        // count is keeps track of what is being read in (tile #, # lines, etc)
        int count = -2;

        // how many lines does the tile have, how many points
        int numPoints = 0;
        int numXY = 0;

        int numTiles = 0;

        // is the loop reading an x value or a y
        int countxy = 0;

        // the id of the tile
        int tileID = 0;

        // the tile orientation
        int tileOrient = 0;

        // the hex string read at the top of the file
        String hexString = "";

        // used to create a point
        float x = 0, y = 0;

        // an array of lines
        Point[] points = null;

        // a 4 byte array, used for converting to ints or floats
        byte[] b = new byte[4];

        // try catch for reading from the file
        try {
            // creates an array of bytes that is the entire file
            byte[] full = Files.readAllBytes(file.toPath());

            for (int i = 0; i < file.length(); i += 4) {
                // System.out.println("i = "+i);
                for (int j = 0; j < 4; ++j) {
                    b[j] = full[i + j];
                }

                if (i == 0) {
                    if (Main.verbose)
                        System.out.println(byteArrayToHexString(b));
                    hexString = byteArrayToHexString(b);
                }

                if (hexString.equals("CAFEBEEF")) {
                    if (i == 0) {
                    } else if (i == 4) {
                        num = convertToInt(b);
                        if (Main.verbose)
                            System.out.println("Num Tiles " + num);
                        tiles = new Tile[num];
                    } else {
                        // the loop is going over the id of the tile
                        // convert the input and store in title
                        if (count == -2) {
                            num = convertToInt(b);
                            tileID = num;
                            count += 2;
                            if (Main.verbose)
                                System.out.println("Tile ID = " + tileID);
                        }

                        /*
                         * The loop is going over the number of lines a tile
                         * has. It converts input, sets size of array, sets how
                         * many lines will be read
                         */
                        else if (count == 0) {
                            num = convertToInt(b);
                            numXY = num * 4;
                            points = new Point[num * 2];
                            ++count;
                            if (Main.verbose)
                                System.out.println("Number of lines " + num
                                        + ", num points = " + num * 2);
                        }

                        /*
                         * Here, the last point for this tile is being read.
                         * This creates and sets a point using the x and y
                         * values, y is being read. A new tile with the Id and
                         * points is constructed, and makeLive() is called.
                         * Counters are reset
                         */
                        else if (count == numXY) {
                            fnum = convertToFloat(b);
                            y = fnum;
                            Point p = new Point();
                            p.setLocation(x, y);
                            points[numPoints] = p;
                            if (Main.verbose)
                                System.out.println("point: " + p);
                            Line[] lines = new Line[numXY / 4];
                            int tempLineCount = 0;
                            for (int k = 0; k < numXY / 2; k += 2) {
                                lines[tempLineCount] = new Line(points[k],
                                        points[k + 1]);
                                tempLineCount++;
                            }
                            tiles[tileID] = new Tile(tileID, lines);
                            tiles[tileID].makeLive();
                            countxy = 0;
                            count = -2;
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
                                if (Main.verbose)
                                    System.out.println("point " + p);
                                ++countxy;
                                points[numPoints] = p;
                                ++numPoints;
                            }
                            ++count;
                        }
                    }
                }

                // cafedeed was read
                else {
                    if (i == 0) {
                    } else if (i == 4) {
                        numTiles = convertToInt(b);
                        tiles = new Tile[16];
                        grid = new Tile[16];
                        for (int j = 0; j < 16; ++j) {
                            tiles[i] = new Tile(-1);
                            grid[i] = new Tile(-1);
                        }
                    } else {
                        if (count == -2) {
                            tileID = convertToInt(b);

                            System.out.println("tileOrGrid " + tileOrGrid);
                            if (tileOrGrid < 16) {
                                tiles[tileOrGrid] = new Tile(tileID);
                                if(tileID != -1){
                                    tiles[tileOrGrid].makeLive();
                                    ++count;
                                }
                                else{
                                    tiles[tileOrGrid].makeEmpty();
                                    count = -2;
                                    ++tileOrGrid;
                                }
                            } else if(tileOrGrid > 15){
                                grid[tileOrGrid-16] = new Tile(tileID);
                                if(tileID != -1){
                                    grid[tileOrGrid-16].makeLive();
                                    ++count;
                                }
                                else{
                                    grid[tileOrGrid-16].makeEmpty();
                                    count = -2;
                                    ++tileOrGrid;
                                }
                            }
                            

                        }

                        else {
                            // orient
                            if (count == -1 && tileID != -1) {
                                tileOrient = convertToInt(b);
                                if (tileOrGrid < 16){
                                    tiles[tileOrGrid].setOrient(tileOrient);
                                }
                                else{
                                    grid[tileOrGrid-16].setOrient(tileOrient);
                                }
                                ++count;
                            }
                            else if(count == 0){
                                num = convertToInt(b);
                                numXY = num * 4;
                                points = new Point[num * 2];
                                ++count;
                            }
                            
                            else if (count == numXY && tileID != -1) {
                                fnum = convertToFloat(b);
                                y = fnum;
                                Point p = new Point();
                                p.setLocation(x, y);
                                points[numPoints] = p;
                                if (Main.verbose)
                                    System.out.println("point: " + p);
                                Line[] lines = new Line[numXY / 4];
                                int tempLineCount = 0;
                                for (int k = 0; k < numXY / 2; k += 2) {
                                    lines[tempLineCount] = new Line(points[k],
                                            points[k + 1]);
                                    tempLineCount++;
                                }
                                
                                if (tileOrGrid < 16)
                                    tiles[tileOrGrid].setLines(lines);
                                else
                                    grid[tileOrGrid-16].setLines(lines);
                                
                                ++tileOrGrid;
                                countxy = 0;
                                count = -2;
                                numPoints = 0;
                            }
                             
                            else if (count < numXY && count > 0 && tileID != -1){
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
                                    if (Main.verbose)
                                        System.out.println("point " + p);
                                    ++countxy;
                                    points[numPoints] = p;
                                    ++numPoints;
                                }
                                ++count;
                            }
                        }
                    }
                }

            }

            if (hexString.equals("CAFEBEEF") || numTiles == 16) {
                for (int j = 0; j < 16; ++j) {
                    grid[j] = new Tile(-1);
                    grid[j].makeEmpty();
                }
            }
        }

        catch (IOException ioe) {
            System.out.println("File not read\n");
            tiles = new Tile[16];
            for (int i = 0; i < 16; ++i) {
                tiles[i] = new Tile(-1);
                grid[i] = new Tile(-1);
                grid[i].makeEmpty();
            }
        }
    }

    /**
     * @author Colin Riley
     */
    public byte[] fileOutArray() {
        byte[] outByte = new byte[2488]; // if cafebeef 2360, cafedeed 2488?
        byte[] ob = new byte[4];
        int byteCount = 0;

        for (int k = 0; k < 4; ++k) {
            if (k == 0)
                ob[k] = (byte) 0xca;
            else if (k == 1)
                ob[k] = (byte) 0xfe;
            else if (k == 2)
                ob[k] = (byte) 0xde;
            else
                ob[k] = (byte) 0xed;
        }
        byteArr4ToFullByteArr(outByte, ob, byteCount);

        byteCount += 4;

        ob = convertIntToByteArray(32);
        byteArr4ToFullByteArr(outByte, ob, byteCount);

        byteCount += 4;

        /*
         * if i <16 look at tiles, else at grid if foo[i].getID() ==-1 write
         * that to file else foo[i].getOtherAttributes and write to file
         */

        for (int i = 0; i < 32; ++i) {
            if (i < 16) {
                if (tiles[i].getID() != -1) {
                    ob = convertIntToByteArray(tiles[i].getID());
                    byteArr4ToFullByteArr(outByte, ob, byteCount);
                    byteCount += 4;

                    ob = convertIntToByteArray(tiles[i].getOrient());
                    byteArr4ToFullByteArr(outByte, ob, byteCount);
                    byteCount += 4;

                    Line[] line = tiles[i].getLines();
                    ob = convertIntToByteArray(line.length);
                    byteArr4ToFullByteArr(outByte, ob, byteCount);
                    byteCount += 4;

                    for (int j = 0; j < line.length; ++j) {
                        ob = convertFloatToByteArray(
                                (float) line[j].getBegin().getX());
                        byteArr4ToFullByteArr(outByte, ob, byteCount);
                        byteCount += 4;

                        ob = convertFloatToByteArray(
                                (float) line[j].getBegin().getY());
                        byteArr4ToFullByteArr(outByte, ob, byteCount);
                        byteCount += 4;

                        ob = convertFloatToByteArray(
                                (float) line[j].getEnd().getX());
                        byteArr4ToFullByteArr(outByte, ob, byteCount);
                        byteCount += 4;

                        ob = convertFloatToByteArray(
                                (float) line[j].getEnd().getY());
                        byteArr4ToFullByteArr(outByte, ob, byteCount);
                        byteCount += 4;
                    }
                } else {
                    ob = convertIntToByteArray(-1);
                    byteArr4ToFullByteArr(outByte, ob, byteCount);
                    byteCount += 4;
                }
            } else {

                if (grid[i - 16].getID() != -1) {
                    ob = convertIntToByteArray(grid[i - 16].getID());
                    byteArr4ToFullByteArr(outByte, ob, byteCount);
                    byteCount += 4;

                    ob = convertIntToByteArray(grid[i - 16].getOrient());
                    byteArr4ToFullByteArr(outByte, ob, byteCount);
                    byteCount += 4;

                    Line[] line = grid[i - 16].getLines();
                    ob = convertIntToByteArray(line.length);
                    byteArr4ToFullByteArr(outByte, ob, byteCount);
                    byteCount += 4;

                    for (int j = 0; j < line.length; ++j) {
                        // System.out.println(line[j]);
                        ob = convertFloatToByteArray(
                                (float) line[j].getBegin().getX());
                        byteArr4ToFullByteArr(outByte, ob, byteCount);
                        byteCount += 4;

                        ob = convertFloatToByteArray(
                                (float) line[j].getBegin().getY());
                        byteArr4ToFullByteArr(outByte, ob, byteCount);
                        byteCount += 4;

                        ob = convertFloatToByteArray(
                                (float) line[j].getEnd().getX());
                        byteArr4ToFullByteArr(outByte, ob, byteCount);
                        byteCount += 4;

                        ob = convertFloatToByteArray(
                                (float) line[j].getEnd().getY());
                        byteArr4ToFullByteArr(outByte, ob, byteCount);
                        byteCount += 4;
                    }
                } else {
                    ob = convertIntToByteArray(-1);
                    byteArr4ToFullByteArr(outByte, ob, byteCount);
                    byteCount += 4;
                }
            }
        }
        return outByte;
    }

    // if anything besides the first 3 cells of row 1 or any of row
    // 2 are selected add empty cells to the board
    public void emptyRow(GridBagConstraints basic, int i, int j, Tile[] tiles) {
        // sets the grid cell
        basic.gridx = j;
        basic.gridy = i;

        // create a blank label, set its size, and it to the grid
        JLabel label = new JLabel("");
        label.setPreferredSize(new Dimension(100, 45));
        label.setMinimumSize(label.getPreferredSize());
        this.add(label, basic);
    }

    // if the cell selected is in the first column or last column,
    // but is after the first row add panels. These are on the side
    // and hold tiles before being placed
    private void sidePanels(GridBagConstraints basic, int i, int j,
            Tile[] tiles) {
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
    private void centerTiles(GridBagConstraints basic, int i, int j,
            int gridCount) {
        // set the cell
        basic.gridx = j;
        basic.gridy = i;

        // create a panel, set its size and border, then add to grid
        // grid[gridCount] = new Tile(-1);
        // grid[gridCount].makeEmpty();
        this.add(grid[gridCount], basic);
    }

    /**
     * Used by setUp() to configure the buttons on a button bar and add it to
     * the gameBoard Takes a GridBagConstraints to position the buttons
     * 
     * @author Colin Riley
     * @param basic
     *            This doesn't need to be a function because it's only called
     *            once, but it was part of the template so we're leaving it here
     *            for now
     */
    private void addButtons(GridBagConstraints basic) {
        // create new buttons for newButton, resetButton, and quitButton
        // set their text, size, and action command

        fileButton = new JButton("File");
        fileButton.setPreferredSize(new Dimension(100, 30));
        fileButton.setMinimumSize(fileButton.getPreferredSize());
        fileButton.setActionCommand("file");
        fileButton.addActionListener(this);

        resetButton = new JButton("Reset");
        resetButton.setPreferredSize(new Dimension(100, 30));
        resetButton.setMinimumSize(resetButton.getPreferredSize());
        resetButton.setActionCommand("reset");
        resetButton.addActionListener(this);

        quitButton = new JButton("Quit");
        quitButton.setPreferredSize(new Dimension(100, 30));
        quitButton.setMinimumSize(quitButton.getPreferredSize());
        quitButton.setActionCommand("exit");
        quitButton.addActionListener(this);

        saveButton = new JButton("Save Game");
        saveButton.setPreferredSize(new Dimension(100, 30));
        saveButton.setMinimumSize(saveButton.getPreferredSize());
        saveButton.setActionCommand("save");
        saveButton.addActionListener(this);
        saveButton.setVisible(false);

        loadButton = new JButton("Load Game");
        loadButton.setPreferredSize(new Dimension(100, 30));
        loadButton.setMinimumSize(loadButton.getPreferredSize());
        loadButton.setActionCommand("load");
        loadButton.addActionListener(this);
        loadButton.setVisible(false);

        // set the cells to the first row, and the first 3 cells of that
        basic.gridy = 0;

        basic.gridx = 0;
        this.add(fileButton, basic);

        basic.gridx = 1;
        this.add(resetButton, basic);

        basic.gridx = 2;
        this.add(quitButton, basic);

        basic.gridx = 0;
        basic.gridy = 1;
        this.add(saveButton, basic);

        basic.gridx = 1;
        basic.gridy = 1;
        this.add(loadButton, basic);
    }

    /**
     * @author Colin Riley
     * @param tiles
     */
    private void shuffleArray(Tile[] tiles) {
        // shuffle the tile array. Create a list with the array then use
        // shuffle. Then convert back.
        List<Tile> tList = Arrays.asList(tiles);
        Collections.shuffle(tList);
        tiles = (Tile[]) tList.toArray();

        // create an array of objects/ints for orientation. create list from
        // this and then convert back to array.
        Object[] orientArray = { 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3,
                3 };
        List<Object> orientList = Arrays.asList(orientArray);

        Collections.shuffle(orientList);
        orientArray = orientList.toArray();

        // sets the orientation for the tiles.
        for (int i = 0; i < orientArray.length; ++i) {
            tiles[i].setOrient((int) orientArray[i]);
            if (tiles[i].getID() == -1) {
                tiles[i].makeEmpty();
            }
        }
    }

    // Handles game logic for a right click
    // Rotates a tile 90 degrees without changing it's
    // location on the board. Rotations stick if the
    // tile is then clicked and/or swapped.
    public void setRightClicked(Tile clickedTile) {
        // Orient the Tile
        if (clickedTile.getOrient() < 3) {
            clickedTile.incOrient();
        } else {
            clickedTile.setOrient(0);
        }
        repaint();
    }

    /**
     * Handles the game logic for swapping tiles only after 2 different tiles
     * have been clicked.
     * 
     * @author Stephen Belden
     * @param clickedTile
     *            is the tile that was most recently left-clicked
     */
    public void setLeftClicked(Tile clickedTile) {
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
            } else if (clickedTile.isEmpty() == false
                    && lastClicked.isEmpty() == false) {
                int tempID = clickedTile.getID();
                int tempOrient = clickedTile.getOrient();
                Line[] tempLines = clickedTile.getLines();
                clickedTile.setID(lastClicked.getID());
                clickedTile.setLines(lastClicked.getLines());
                clickedTile.setOrient(lastClicked.getOrient());
                lastClicked.setID(tempID);
                lastClicked.setLines(tempLines);
                lastClicked.setOrient(tempOrient);
                clickedTile.makeLive();
                lastClicked.makeLive();
                Main.game.repaint();
                lastClicked = null;
                played = true;
                // Case in which two empty game board positions are clicked
            } else if (clickedTile.isEmpty() == true
                    && lastClicked.isEmpty() == true) {
                clickedTile.reset();
                lastClicked.reset();
                lastClicked = null;
                // Case in which one tile and one empty spot are clicked
            } else if (clickedTile.isEmpty() != lastClicked.isEmpty()) {
                int tempID = clickedTile.getID();
                int tempOrient = clickedTile.getOrient();
                Line[] tempLines = clickedTile.getLines();
                clickedTile.setID(lastClicked.getID());
                clickedTile.setLines(lastClicked.getLines());
                clickedTile.setOrient(lastClicked.getOrient());
                lastClicked.setID(tempID);
                lastClicked.setLines(tempLines);
                lastClicked.setOrient(tempOrient);
                clickedTile.switchState();
                lastClicked.switchState();
                lastClicked = null;
                played = true;

                if (Main.verbose) {
                    System.out.println("grid");
                    for (Tile t : grid) {
                        System.out.println(t.getID());
                    }
                    System.out.println("tiles");
                    for (Tile t : tiles) {
                        System.out.println(t.getID());
                    }
                }
            }
        }
    }

    /**
     ********************** CONSOLE STUFF TEMPORARY, MUST BE CHANGED*************
     * 
     * @author Colin Riley user enters a file name, .mze is added to it. array
     *         of bytes is written to created file of that name
     * @param outByte
     * @throws IOException
     */
    public static void writeFile(String path, byte[] outByte)
            throws IOException {
        FileOutputStream fos = null;
        try {
            if (!path.endsWith(".mze"))
                path += ".mze";
            fos = new FileOutputStream(path);
            fos.write(outByte);
        } catch (IOException ioe) {
            System.out.println("File failed to write\n");
        } finally {
            fos.close();
        }
    }

    /**
     * @author Colin Riley function that adds 4byte arrays to a larger byte
     *         array
     * @param outByte
     * @param ob
     * @param i
     */
    public static void byteArr4ToFullByteArr(byte[] outByte, byte[] ob, int i) {
        for (int k = 0; k < 4; ++k) {
            outByte[i + k] = ob[k];
        }
    }

    /**
     * @author Java2s.com Following code taken from
     *         http://www.java2s.com/Book/Java/Examples/
     *         Convert_data_to_byte_array_back_and_forth.htm
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

    public static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] convertIntToByteArray(int value) {
        byte[] bytes = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putInt(value);
        return buffer.array();
    }

    public static byte[] convertFloatToByteArray(float value) {
        byte[] bytes = new byte[4];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putFloat(value);
        return buffer.array();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
};
