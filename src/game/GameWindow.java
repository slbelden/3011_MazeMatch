
/**
 * @author Kim Buckner
 * @author James Scott
 * @author Colin Riley
 * @author Stephen Belden
 * @author Shaya Wolf
 * @author Neil Carrico
 * Date: Feb 19, 2016
 *
 * This is the actual "game". May/will have to make some major changes.
 * This is just a "hollow" shell.
 *
 * When you get done, I should see the buttons at the top in the "play" area
 * (not a pull-down menu). The only one that should do anything is Quit.
 *
 * Should also see something that shows where the 4x4 board and the "spare"
 * tiles will be when we get them stuffed in.
 */

package game;

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame implements ActionListener {
    /**
     * because it is a serializable object, need this or javac complains a lot
     */
    public static final long serialVersionUID = 1;

    /*
     * Here I declare some buttons and declare an array to hold the grid
     * elements. But, you can do what you want.
     */
    public static JButton    newButton, resetButton, quitButton;
    private int              startAt          = 0;

    /**
     * Constructor sets the window name using super(), changes the layout, which
     * you really need to read up on, and maybe you can see why I chose this
     * one.
     *
     * @param s
     */

    public GameWindow(String s) {
        super(s);
        GridBagLayout gbl = new GridBagLayout();
        setLayout(gbl);
    }

    /**
     * For the buttons
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
     * Establishes the inital board
     */

    public void setUp() {
        // actually create the array for elements, make sure it is big enough

        // Need to play around with the dimensionts and the gridx/y values
        // These constraints are going to be added to the pieces/parts I
        // stuff into the "GridBag".

        GridBagConstraints basic = new GridBagConstraints();
        basic.anchor = GridBagConstraints.FIRST_LINE_START;
        basic.gridx = 0;
        basic.gridy = startAt;
        basic.gridwidth = 1;
        basic.gridheight = 1;
        basic.fill = GridBagConstraints.RELATIVE;

        addElements(basic);

        // Here I create 16 elements to put into my gameBoard

        // Now I add each one, modifying the default gridx/y and add
        // it along with the modified constraint

        return;

    }

    /**
     * Used by setUp() to configure the grid and add it to the game board Takes
     * a GridBagConstraints to position the buttons
     * 
     * @param basic
     */
    public void addElements(GridBagConstraints basic) {
        Border border = BorderFactory.createLineBorder(Color.black, 1);

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

                    // create a panel, set its size and border, then add to grid
                    JPanel panel = new JPanel();
                    panel.setBorder(border);
                    panel.setPreferredSize(new Dimension(100, 100));
                    this.add(panel, basic);
                }

                // if the middle 16 cells are selected, add panels. These are
                // where the user places tiles in the game grid to play
                else if (i > 3 && i < 8 && j > 1 && j < 6) {
                    // set the cell
                    basic.gridx = j;
                    basic.gridy = i;

                    // create a panel, set its size and border, then add to grid
                    JPanel panel = new JPanel();
                    panel.setBorder(border);
                    panel.setPreferredSize(new Dimension(100, 100));
                    this.add(panel, basic);
                }
            }
        }
    }

    /**
     * Used by setUp() to configure the buttons on a button bar and add it to
     * the gameBoard Takes a GridBagConstraints to position the buttons
     * 
     * @param basic
     */
    public void addButtons(GridBagConstraints basic) {
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

        // set the cells to the first row, and the first 3 cells of that row
        //add the buttons to the grid
        
        basic.gridy = 0;
        
        basic.gridx = 0;
        this.add(newButton, basic);

        basic.gridx = 1;
        this.add(resetButton, basic);

        basic.gridx = 2;
        this.add(quitButton, basic);

        return;
    }

};
