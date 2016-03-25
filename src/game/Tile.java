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
import java.awt.Point;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * @author ????
 * @author Shaya Wolf
 * 
 */
public class Tile extends JLabel {
    // Avoid compiler complaints
    private static final long serialVersionUID = 1L;

    // Instance Variables
    private int ID;
    private Point location;

    // Constructor -- Creates a Tile
    // Given -- NA
    public Tile(int x, Point p) {
        ID = x;
        location = p;

        setBackground(Color.white);
        setOpaque(true);
        setText(Integer.toString(x));

        Border border = BorderFactory.createLineBorder(Color.black, 1);
        setBorder(border);

        setPreferredSize(new Dimension(100, 100));
        setVisible(true);
    };

    // Get Location Method -- Returns location of tile
    // Given -- NA
    public Point getLoc() {
        return location;
    }

    // Set Location Method -- Sets the location of tile
    // Given -- x and y coordinates
    public void setLoc(int x, int y) {
        location.setLocation(x, y);
    }

    // Set Location Method -- Sets the location of tile
    // Given -- New point
    public void setLoc(Point p) {
        location.setLocation(p);
    }

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

}
