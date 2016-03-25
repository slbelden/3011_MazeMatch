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
 * @version March 25, 2016
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
     * Constructor:
     * Sets the window name using super() and
     *         changes the layout
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
            System.out.println("reset pressed\n");
        if ("new".equals(e.getActionCommand()))
            System.out.println("new pressed\n");
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
         * @author Colin Riley
         * (did work on tiles)
         * @author Stepen Belden
         * (code cleanup)
         */

        // creates an array of tiles
        Tile[] tiles = new Tile[16];

        // for loop to assign the 16 tiles
        for (int i = 1; i <= 16; ++i) {
            // creates a tile and adds it to the array, sets various label
            // properties
            tiles[i - 1] = new Tile(i);
            tiles[i - 1].makeLive();
        }

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
                    Tile panel = new Tile(0);
                    panel.setPreferredSize(new Dimension(100, 100));
                    panel.makeEmpty();
                    this.add(panel, basic);
                }
            }
        }
    }

    /**
     * Used by setUp() to configure the buttons on a button bar and add it to
     * the gameBoard Takes a GridBagConstraints to position the buttons
     * 
     * @author Colin Riley
     * (reworked from first)
     * @param basic
     * 
     * This doesn't need to be a function because it's only called once,
     * but it was part of the template so we're leaving it here for now
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
     * Handles the game logic for swapping tiles only after 2
     * different tiles have been clicked.
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
                clickedTile.setID(lastClicked.getID());
                lastClicked.setID(tempID);
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
                clickedTile.setID(lastClicked.getID());
                lastClicked.setID(tempID);
                clickedTile.switchState();
                lastClicked.switchState();
                lastClicked = null;
            }
        }
    }
};
