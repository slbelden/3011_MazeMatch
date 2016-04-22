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
 * This class Handles tasks and data that are the same in every tile.
 */

package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

/**
 * @author Colin Riley
 * @author Shaya Wolf
 */
public class Tile extends JLabel implements MouseListener {
    // Avoid compiler complaints
    private static final long   serialVersionUID = 1;

    // Instance Variables
    private int                 ID;
    private Line[]              lines;
    private boolean             isEmpty;  // True iff this tile is a blank game
                                          // board space
    private int                 orient; // 0-3 multiplied by 90 when used

    // Constants
    private static final Border border           = BorderFactory
            .createLineBorder(Color.white, 1);
    private static final Border NoBorder         = BorderFactory
            .createLineBorder(Color.black, 0);

    // Constructor for a tile, takes id and array of lines
    public Tile(int x, Line[] l) {
        ID = x;
        lines = l;

        setBackground(Color.white);
        setOpaque(true);
        setPreferredSize(new Dimension(100, 100));
        setVisible(true);
        setBorder(border);

        addMouseListener(this);
    };
    
    // copy constructor
    public Tile(Tile in) {
        ID = in.ID;
        lines = in.lines;
        isEmpty = in.isEmpty;
        orient = in.orient;
        
        setBackground(Color.white);
        setOpaque(true);
        setPreferredSize(new Dimension(100, 100));
        setVisible(true);
        setBorder(border);

        addMouseListener(this);
    }

    // constructor for a tile takes only the id
    public Tile(int x) {
        ID = x;

        setBackground(Color.white);
        setOpaque(true);
        setPreferredSize(new Dimension(100, 100));
        setVisible(true);
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

    public int getOrient() {
        return orient;
    }

    public void setOrient(int x) {
        orient = x;
    }
    
    public void incOrient(){
        orient++;
    }

    public Line[] getLines() {
        return lines;
    }

    public void setLines(Line[] l) {
        lines = l;
    }

    /**
     * @author Colin Riley
     */
    @Override
    public void paintComponent(Graphics g) {
        if (Main.verbose)
            System.out.println("Attempting to redraw tile " + ID + "...");
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform at = g2.getTransform();
        at.rotate(Math.toRadians(orient* 90), 50, 50);
        g2.setTransform(at);
        super.paintComponent(g2);
        if (lines != null) {
            g2.setStroke(new BasicStroke(3));
            for (int i = 0; i < lines.length; ++i) {
                float x0 = lines[i].getBegin().x, y0 = lines[i].getBegin().y,
                        x1 = lines[i].getEnd().x, y1 = lines[i].getEnd().y;
                Line2D line1 = new Line2D.Float(x0, y0, x1, y1);
                g2.draw(line1);
            }
        }
        if (Main.verbose)
            System.out.println("Tile " + ID + " was repainted");
    }

    /**
     * @author Colin Riley
     */

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
        if (Main.verbose) {
            setText("<html><span style='font-size:35px'>" + Integer.toString(ID)
                    + "</span></html>");
        }
        setHorizontalAlignment(CENTER);
    }

    // Switches tile between live and empty
    public void switchState() {
        if (isEmpty)
            makeLive();
        else
            makeEmpty();
    }

    // Makes the tile un-selected
    public void reset() {
        if (isEmpty) {
            setBackground(Color.gray);
            setBorder(border);
        } else {
            setBackground(Color.white);
            setBorder(NoBorder);
        }
       //setOrient(getStart_Orient());
       //setLocale(getStart_Loc());
        repaint();
    }

    public void debugPrint() {
        System.out.println("Tile with ID: " + ID + " & isEmpty = "
                + (isEmpty ? "true" : "false") + " holds these lines:");
        for (Line l : lines)
            l.debugPrint();
    }

    /**
     * @author Stephen Belden (wrote mouse and movement functions)
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

    /**
     * @author Stephen Belden (wrote mouse and movement functions)
     */
    @Override
    public void mousePressed(MouseEvent arg0) {
        if (SwingUtilities.isLeftMouseButton(arg0)) 
            { Main.game.setLeftClicked(this); }
        else if (SwingUtilities.isRightMouseButton(arg0)) 
            { Main.game.setRightClicked(this); }
        if(Main.verbose) {
            System.out.print("initialTileState ID's : ");
            for (Tile t : Main.initialTileState) {
                System.out.print(t.getID() + ", ");
            }
            System.out.println();
        }
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
        // Do nothing
    }
}
