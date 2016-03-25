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
 * This class Handles tasks and data that are the same in every tile.
 */

package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * @author Colin Riley
 * @author Shaya Wolf
 */
public class Tile extends JLabel implements MouseListener{
    // Avoid compiler complaints
    private static final long serialVersionUID = 1L;

    // Instance Variables
    private int ID;
    private boolean isEmpty; // True iff this tile is a blank game board space
    protected Border border;
    protected Border NoBorder;

    // Constructor -- Creates a Tile
    // Given -- NA
    public Tile(int x) {
        ID = x;

        setBackground(Color.white);
        setOpaque(true);
        setPreferredSize(new Dimension(100, 100));
        setVisible(true);
        

        border = BorderFactory.createLineBorder(Color.black, 1);
        NoBorder = BorderFactory.createLineBorder(Color.black, 0);
        setBorder(border);
        
        addMouseListener(this);
    };

    // Get ID Method -- Returns the ID of a tile
    // Given -- NA
    public int getID() {
        return ID;
    }

    // Set ID Method -- Sets the ID of a tile
    // Given -- New ID
    public void setID(int x) {
        ID = x;
    }

    /**
     * @return empty
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    // Sets this tile up to display as an empty game board tile
    public void makeEmpty() {
        isEmpty = true;
        setBackground(Color.gray);
        setBorder(border);
        setText("");
    }
    
    // Sets this tile up to display as a real Tile
    public void makeLive() {
        isEmpty = false;
        setBackground(Color.white);
        setBorder(NoBorder);
        setText("<html><span style='font-size:35px'>" + 
                Integer.toString(ID) + "</span></html>");
        setHorizontalAlignment(CENTER);
    }
    
    // Switches tile between live and empty
    public void switchState() {
        if(isEmpty) makeLive();
        else makeEmpty();
    }
    
    // Makes the tile un-selected
    public void reset() {
        if(isEmpty) {
            setBackground(Color.gray);
            setBorder(border);
        } else {
            setBackground(Color.white);
            setBorder(NoBorder);
        }
        repaint();
    }

    /**
     * @author Stephen Belden
     * (wrote mouse and movement functions)
     */
    @Override
    public void mouseClicked(MouseEvent arg0) {
        // Do nothing
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // Do nothing
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        // Do nothing
    }

    @Override
    public void mousePressed(MouseEvent arg0) {
        Main.game.setClicked(this);
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // Do nothing
    }
}
