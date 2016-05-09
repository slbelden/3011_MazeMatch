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
 * @version May 6, 2016
 *
 * This is the actual "game".
 * This class handles all game logic, as well as rendering the game board.
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
import java.util.concurrent.TimeUnit;

public class GameWindow extends JFrame implements ActionListener {
    // Avoid compiler complaints
    public static final long serialVersionUID = 1;

    // the hex string read at the top of the file
    private static String hexString = "";

    // Declare Buttons
    public static JButton fileButton, resetButton, quitButton, saveButton,
            loadButton;

    // Data used for game logic
    private static Tile lastClicked;

    // creates arrays of tiles
    private static Tile[] tiles = new Tile[16];
    private static Tile[] grid = new Tile[16];

    // Data for determining when to prompt the user about saving
    private static boolean played = false;
    private static boolean start_timer = false;
    private static long startTime = System.currentTimeMillis() / 1000;
    private static long loadTime;

    // Layout
    private GridBagConstraints basic = new GridBagConstraints();

    /**
     * Constructor: Sets the window name using super() and changes the layout
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
        start_timer = false;
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
        	// an attempt to make the workspace the default directory
            String userhome = System.getProperty("user.home");
            final JFileChooser chose =
                    new JFileChooser(userhome + "\\workspace");
            
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
                reset();
            }
            break;
        default:
            break; // Cancel option, so do nothing else
        }
    }

    /**
     * @author Shaya Wolf
     * @author Stephen Belden
     */
    public boolean saveGame() {
        long stopTime = System.currentTimeMillis()/1000;
        loadTime = stopTime - startTime;
        boolean bool = false;
        // an attempt to make the workspace the default directory
        String userhome = System.getProperty("user.home");
        JFileChooser chose = new JFileChooser(userhome + "\\workspace");
        
        chose.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int response = chose.showSaveDialog(saveButton);
        if (response == JFileChooser.CANCEL_OPTION) {
            // Do nothing
        } else if (chose.getSelectedFile().exists()) {
            // If the file already exists:
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
                // "Yes" option
                try {
                    writeFile(path, fileOutArray());
                    // Don't ask to save again until another move
                    played = false;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Keep trying to save the game until Cancel or Approve
                saveGame();
            }
        } else if (response == JFileChooser.APPROVE_OPTION) {
            // Error-free save case
            File file = chose.getSelectedFile();
            String path = file.getAbsolutePath();
            try {
                writeFile(path, fileOutArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
            bool = true;
            // Don't ask to save again until another move
            played = false;
        } else {
            JOptionPane.showMessageDialog(this,
                    "An error occured while trying to save the file.",
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            // Try until Cancel or Approve
            saveGame();
        }
        return bool;
    }

    /**
     * @author Colin Riley
     * @author James Scott
     */
    public void reset() {
        newWindow();
        start_timer =false;
        Main.game.setUp(null, false, false);
    }

    /**
     * @author Shaya Wolf
     * @return
     */
    public boolean checkWinCond() {
        boolean win = true;
        int count = 0;

        for (Tile t1 : grid) {
            if (!(Main.writeTileArray[count].getID() == t1.getID())
                    || t1.getOrient() != 0) {
                win = false;
            }
            count++;
        }
        return win;
    }

    private void newWindow() {
        // When reset is pressed, it pulls the window position data here
        int windowWidth = Main.game.getWidth();
        int windowHeight = Main.game.getHeight();
        int x = Main.game.getX();
        int y = Main.game.getY();

        // Tears down the window and makes a new one
        Main.game.dispose();
        Main.game = new GameWindow("Group E Maze");
        Main.game.setResizable(false);

        // Sets the window x and y to the original locations
        Main.game.setBounds(x, y, windowWidth, windowHeight);

        // Window appearance and behavior
        Main.game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Main.game.getContentPane().setBackground(Color.cyan);
        Main.game.setVisible(true);
    }

    /**
     * Establishes the initial board
     * @author Colin Riley
     * @author Stepen Belden
     * @author Shaya Wolf
     * @author James Scott
     */
    public void setUp(File file, boolean newGame, boolean shuffle) {
        // Location holder for iterating through the GridBag
        int gridCount = 0;

        // GridBag setup
        basic.anchor = GridBagConstraints.FIRST_LINE_START;
        basic.gridx = 0;
        basic.gridy = 0;
        basic.gridwidth = 1;
        basic.gridheight = 1;
        basic.ipadx = 0;
        basic.ipady = 0;
        basic.fill = GridBagConstraints.RELATIVE;
        // TODO: Fix for low-resolution. Remove before final version.
        // basic.gridheight = 2;

        // Sets the initial tile state for the reset button
        if (newGame == true) {
            // reads from default on initial run. Adds value to the tiles and
            // draws lines on them
            readFromFile(file);

            if (hexString.equals("CAFEBEEF"))
                shuffle = true;

            if (shuffle)
                shuffleArray(tiles);
            for (int i = 0; i < tiles.length; i++) {
                Main.initialTileState[i] = new Tile(tiles[i]);
                if (Main.initialTileState[i].getID() != -1)
                    Main.initialTileState[i].makeLive();
                else
                	Main.initialTileState[i].makeEmpty();

                Main.initialGridState[i] = new Tile(grid[i]);
                Main.initialGridState[i].setLoc(grid[i].getLoc());
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
                if (grid[i].getID() != -1) {
                    grid[i].makeLive();
                } else {
                    grid[i].makeEmpty();
                    grid[i].setLoc(i + 16);
                }

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
    public void readFromFile(File file) {
        // data containers
        int num = 0;
        float fnum = 0;

        // count is keeps track of what is being read in (tile #, # lines, etc)
        int count = -2;

        // how many lines does the tile have, how many points
        int numPoints = 0;
        int numXY = 0;

        // is the loop reading an x value or a y
        int countxy = 0;

        // the id of the tile
        int tileID = 0;
        int tileLoc = 0;

        // the tile orientation
        int tileOrient = 0;

        // used to create a point
        float x = 0, y = 0;

        // an array of lines
        Point[] points = null;

        // a 4 byte array, used for converting to ints or floats
        byte[] b = new byte[4];
        byte[] longb = new byte[8];

        // try catch for reading from the file
        try {
            // creates an array of bytes that is the entire file
            byte[] full = Files.readAllBytes(file.toPath());

            for (int i = 0; i < file.length(); i += 4) {
                // System.out.println("i = "+i);
            
                if( i == 8 && !hexString.equals("CAFEBEEF")){
                    for (int j = 0; j < 8;++j){ longb[j] = full[i+j]; }
                }
                else{
                    for (int j = 0; j < 4; ++j) { b[j] = full[i + j]; }
                }

                if (i == 0) {
                    if (Main.verbose)
                        System.out.println(byteArrayToHexString(b));
                    hexString = byteArrayToHexString(b);
                }
                if (!hexString.equals("CAFEBEEF")
                        && !hexString.equals("CAFEDEED")) {
                	JOptionPane.showMessageDialog(this,
                		    "The file type entered is invalid.",
                		    "Invalid File",
                		    JOptionPane.ERROR_MESSAGE);
                	revalidate();
                	
                    throw new IOException();
                }
                if (i == 0) {
                } else if (i == 4) {
                    num = convertToInt(b);
                    if (Main.verbose)
                        System.out.println("Num Tiles " + num);
                    tiles = new Tile[num];
                    grid = new Tile[num];
                    Main.writeTileArray = new Tile[num];
                    for (int k = 0; k < 16; ++k) {
                        Main.writeTileArray[k] = new Tile(-1);
                        tiles[k] = new Tile(-1);
                        tiles[k].setLoc(k);
                        tiles[k].makeEmpty();
                        grid[k] = new Tile(-1);
                        grid[k].setLoc(k + 16);
                        grid[k].makeEmpty();
                    }
                } 
                else if(i == 8 && !hexString.equals("CAFEBEEF")){
                    loadTime= convertToLong(longb);
                    String hms = String.format("%02d:%02d:%02d",
                            TimeUnit.SECONDS.toHours(loadTime),
                            TimeUnit.SECONDS.toMinutes(loadTime)
                                    - TimeUnit.HOURS.toMinutes(
                                            TimeUnit.SECONDS.toHours(loadTime)),
                            TimeUnit.SECONDS.toSeconds(loadTime)
                                    - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS
                                            .toMinutes(loadTime)));
                    System.out.println(hms);
                    i+=4;
                }
                else {
                    // the loop is going over the id of the tile
                    // convert the input and store in title
                    if (count == -2) {
                        tileLoc = convertToInt(b);
                        if (hexString.equals("CAFEDEED")) {
                            ++count;
                        } else {
                            count += 2;
                        }
                        if (Main.verbose)
                            System.out.println("Tile loc = " + tileLoc + '\n'
                                    + "tile ID = " + tileID);
                    }

                    else if (count == -1 && hexString.equals("CAFEDEED")) {
                        tileOrient = convertToInt(b);
                        if (Main.verbose)
                            System.out.println("Tile orient = " + tileOrient);
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
                        if (Main.verbose)
                            System.out.println("Number of lines " + num
                                    + ", num points = " + num * 2);
                    }

                    /*
                     * Here, the last point for this tile is being read. This
                     * creates and sets a point using the x and y values, y is
                     * being read. A new tile with the Id and points is
                     * constructed, and makeLive() is called. Counters are reset
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

                        Main.writeTileArray[tileID] = new Tile(tileID, lines);
                        Main.writeTileArray[tileID].setOrient(tileOrient);
                        Main.writeTileArray[tileID].setLoc(tileLoc);

                        ++tileID;
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
            for (int i = 0; i < 16; ++i) {
                tileLoc = Main.writeTileArray[i].getLoc();
                if (tileLoc < 16) {
                    tiles[tileLoc] = new Tile(Main.writeTileArray[i]);
                    tiles[tileLoc]
                            .setOrient(Main.writeTileArray[i].getOrient());
                    tiles[tileLoc].setLoc(tileLoc);
                } else {
                    grid[tileLoc - 16] = new Tile(Main.writeTileArray[i]);
                    grid[tileLoc - 16]
                            .setOrient(Main.writeTileArray[i].getOrient());
                    grid[tileLoc - 16].setLoc(tileLoc);
                }
            }

            for (int i = 0; i < 16; ++i) {
                if (tiles[i].isEmpty()) {
                    tiles[i] = new Tile(-1);
                    tiles[i].setLoc(i);
                    tiles[i].makeEmpty();
                }
                if (grid[i].isEmpty()) {
                    grid[i] = new Tile(-1);
                    grid[i].setLoc(i + 16);
                    grid[i].makeEmpty();
                }
            }
            /*
             * if (hexString.equals("CAFEBEEF")) { for (int j = 0; j < 16; ++j)
             * { grid[j] = new Tile(-1); grid[j].makeEmpty(); } }
             */
        }

        catch (Exception e) {
            // Reset all arrays to clean, safe states
            tiles = new Tile[16];
            
            for (int i = 0; i < 16; ++i) {
                tiles[i] = new Tile(-1);
                tiles[i].setLoc(i);
                tiles[i].makeEmpty();
                grid[i] = new Tile(-1);
                grid[i].setLoc(i + 16);
                grid[i].makeEmpty();
            }
            
            /*
            JOptionPane.showMessageDialog(this,
                    "There was a problem loading the file.",
                    "Load Error",
                    JOptionPane.ERROR_MESSAGE);
                      
            loadGame();*/
        }
    }

    /**
     * @author Colin Riley
     */
    public byte[] fileOutArray() {
            byte[] outByte = new byte[2432]; // if cafebeef 2360, cafedeed 2488?
            byte[] ob = new byte[4];
            byte[] longob = new byte[4];
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
            toFullByteArray(outByte, ob, byteCount);

            byteCount += 4;

            ob = convertIntToByteArray(16);
            toFullByteArray(outByte, ob, byteCount);

            byteCount += 4;
            
            longob = convertLongToByteArray(loadTime);
            toFullByteArray(outByte, longob, byteCount);
            
            byteCount += 8;

            /*
             * if i <16 look at tiles, else at grid if foo[i].getID() ==-1 write
             * that to file else foo[i].getOtherAttributes and write to file
             */
            
            
            
            for (int i = 0; i < 16; ++i) {
                //System.out.println("pre "+Main.writeTileArray[i].getLoc());
                for (int j = 0; j < 16; ++j) {
                    if (Main.writeTileArray[i].getID() == tiles[j].getID()) {
                        //System.out.println("ID match..."+Main.writeTileArray[i].getID());
                        //System.out.println("loc in tile"+tiles[i].getLoc());
                        Main.writeTileArray[i].setLoc(tiles[j].getLoc());
                        Main.writeTileArray[i].setOrient(tiles[j].getOrient());
                    }
                    if (Main.writeTileArray[i].getID() == grid[j].getID()) {
                        //System.out.println("ID match...");
                        //System.out.println("loc in grid "+grid[i].getID());
                        Main.writeTileArray[i].setLoc(grid[j].getLoc());
                        Main.writeTileArray[i].setOrient(grid[j].getOrient());
                    }
                }
                //System.out.println();
            }

            for (int i = 0; i < 16; ++i) {
                Line[] line = Main.writeTileArray[i].getLines();
                

                //System.out.println("new loc of wta " + i+Main.writeTileArray[i].getLoc());
                ob = convertIntToByteArray(Main.writeTileArray[i].getLoc());
                toFullByteArray(outByte, ob, byteCount);
                byteCount += 4;

                ob = convertIntToByteArray(Main.writeTileArray[i].getOrient());
                toFullByteArray(outByte, ob, byteCount);
                byteCount += 4;

                ob = convertIntToByteArray(line.length);
                toFullByteArray(outByte, ob, byteCount);
                byteCount += 4;

                for (int j = 0; j < line.length; ++j) {
                    ob = convertFloatToByteArray((float) line[j].getBegin().getX());
                    toFullByteArray(outByte, ob, byteCount);
                    byteCount += 4;

                    ob = convertFloatToByteArray((float) line[j].getBegin().getY());
                    toFullByteArray(outByte, ob, byteCount);
                    byteCount += 4;

                    ob = convertFloatToByteArray((float) line[j].getEnd().getX());
                    toFullByteArray(outByte, ob, byteCount);
                    byteCount += 4;

                    ob = convertFloatToByteArray((float) line[j].getEnd().getY());
                    toFullByteArray(outByte, ob, byteCount);
                    byteCount += 4;
                }
            }
            startTime = System.currentTimeMillis() / 1000;
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

        saveButton = new JButton("Save");
        saveButton.setPreferredSize(new Dimension(100, 30));
        saveButton.setMinimumSize(saveButton.getPreferredSize());
        saveButton.setActionCommand("save");
        saveButton.addActionListener(this);
        saveButton.setVisible(false);

        loadButton = new JButton("Load");
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
        basic.gridy = 0;
        this.add(resetButton, basic);

        basic.gridx = 2;
        basic.gridy = 0;
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
            tiles[i].setLoc(i);
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
        if (!start_timer){
            startTime = System.currentTimeMillis() / 1000;
        }
            
        start_timer = true;
        if (checkWinCond() == true) {
            winPopup();
        }
    }

    /**
     * Handles the game logic for swapping tiles only after 2 different tiles
     * have been clicked.
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
                int tempLoc = clickedTile.getLoc();
                int tempOrient = clickedTile.getOrient();
                Line[] tempLines = clickedTile.getLines();
                clickedTile.setID(lastClicked.getID());
                clickedTile.setLoc(tempLoc);
                clickedTile.setLines(lastClicked.getLines());
                clickedTile.setOrient(lastClicked.getOrient());
                lastClicked.setID(tempID);
                lastClicked.setLoc(tempLoc);
                lastClicked.setLines(tempLines);
                lastClicked.setOrient(tempOrient);
                clickedTile.makeLive();
                lastClicked.makeLive();
                Main.game.repaint();
                Main.game.repaint();
                lastClicked = null;
                played = true;
                if (!start_timer){
                    startTime = System.currentTimeMillis() / 1000;
                }
                start_timer = true;
                // Case in which two empty game board positions are clicked
            } else if (clickedTile.isEmpty() == true
                    && lastClicked.isEmpty() == true) {
                clickedTile.reset();
                lastClicked.reset();
                lastClicked = null;
                // Case in which one tile and one empty spot are clicked
            } else if (clickedTile.isEmpty() != lastClicked.isEmpty()) {
                int tempID = clickedTile.getID();
                int tempLoc = clickedTile.getLoc();
                int tempOrient = clickedTile.getOrient();
                Line[] tempLines = clickedTile.getLines();
                clickedTile.setID(lastClicked.getID());
                clickedTile.setLoc(tempLoc);
                clickedTile.setLines(lastClicked.getLines());
                clickedTile.setOrient(lastClicked.getOrient());
                lastClicked.setID(tempID);
                lastClicked.setLoc(tempLoc);
                lastClicked.setLines(tempLines);
                lastClicked.setOrient(tempOrient);
                clickedTile.switchState();
                lastClicked.switchState();
                lastClicked = null;
                played = true;
                if (!start_timer){
                    startTime = System.currentTimeMillis() / 1000;
                }
                start_timer = true;

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
        if (checkWinCond() == true) {
            winPopup();
        }
    }

    public void winPopup() {
        // System.out.println("Winner winner chicken dinner");
        long stopTime = System.currentTimeMillis() / 1000;
        long elapsedTime = stopTime - startTime + loadTime;
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.SECONDS.toHours(elapsedTime),
                TimeUnit.SECONDS.toMinutes(elapsedTime)
                        - TimeUnit.HOURS.toMinutes(
                                TimeUnit.SECONDS.toHours(elapsedTime)),
                TimeUnit.SECONDS.toSeconds(elapsedTime)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS
                                .toMinutes(elapsedTime)));
        JOptionPane.showMessageDialog(this, "You Win!" + '\n' + hms);
        played = false;
    }

    /**
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
    public static void toFullByteArray(byte[] outByte, byte[] ob, int i) {
        for (int k = 0; k < ob.length ; ++k) {
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
    
    public static long convertToLong(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.getLong();
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
    
    public static byte[] convertLongToByteArray(long value) {

        byte[] bytes = new byte[8];
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.putLong(value);
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
