/**
 * @author James Scott
 * @author Colin Riley
 * @author Stephen Belden
 * @author Shaya Wolf
 * @author Neil Carrico
 * @version May 2, 2016
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
    private static final long serialVersionUID = 1;

    // Instance Variables
    private int ID; // 0-15, in the original (correct) order of the tiles
    private Line[] lines; // Holds maze lines, read from file
    private boolean isEmpty; // True iff this tile is a blank game board space
    private int orient; // 0-3 multiplied by 90 when used

    // Constants
    private static final Border border =
            BorderFactory.createLineBorder(Color.white, 1);
    private static final Border noBorder =
            BorderFactory.createLineBorder(Color.black, 0);

    /**
     * Constructor for a tile, takes id and array of lines
     * @author Shaya Wolf
     */
    public Tile(int x, Line[] l) {
        ID = x;
        lines = l;

        setBackground(Color.white);
        setOpaque(true);
        setPreferredSize(new Dimension(100, 100));
        this.setMinimumSize(this.getPreferredSize());
        setVisible(true);
        setBorder(border);

        addMouseListener(this);
    };

    /**
     * Copy Constructor
     * @author Stephen Belden
     */
    public Tile(Tile in) {
        ID = in.ID;

        if (ID != -1) {
            lines = in.lines;
            isEmpty = in.isEmpty;
            orient = in.orient;
        }

        setBackground(Color.white);
        setOpaque(true);
        setPreferredSize(new Dimension(100, 100));
        this.setMinimumSize(this.getPreferredSize());
        setVisible(true);
        setBorder(border);

        addMouseListener(this);
    }

    /**
     * Constructor for a tile takes only the id
     * @author Shaya Wolf
     */
    public Tile(int x) {
        ID = x;

        setBackground(Color.white);
        setOpaque(true);
        setPreferredSize(new Dimension(100, 100));
        this.setMinimumSize(this.getPreferredSize());
        setVisible(true);
        setBorder(border);

        addMouseListener(this);
    };

    // Getter and Setter methods:
    
    public int getID() {
        return ID;
    }

    public void setID(int x) {
        ID = x;
    }

    public int getOrient() {
        return orient;
    }

    public void setOrient(int x) {
        orient = x;
    }

    public void incOrient() {
        orient++;
    }

    public Line[] getLines() {
        return lines;
    }

    public void setLines(Line[] l) {
        lines = l;
    }
    
    public boolean isEmpty() {
        return isEmpty;
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
        at.rotate(Math.toRadians(orient * 90), 50, 50);
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
    }

    /**
     * Sets this tile up to display as an empty game board tile
     * @author Stephen Belden
     */
    public void makeEmpty() {
        isEmpty = true;
        setBackground(Color.gray);
        setBorder(border);
        setText("");
    }

    /**
     * Sets this tile up to display as a real Tile
     * @author Stephen Belden
     */
    public void makeLive() {
        isEmpty = false;
        setBackground(Color.white);
        setBorder(noBorder);
        if (Main.verbose) {
            setText("<html><span style='font-size:35px'>" + Integer.toString(ID)
                    + "</span></html>");
        }
        setHorizontalAlignment(CENTER);
    }

    /**
     * Switches tile between live and empty
     * @author Stephen Belden
     */
    public void switchState() {
        if (isEmpty)
            makeLive();
        else
            makeEmpty();
    }

    /**
     * Makes the tile un-selected
     * @author Stephen Belden
     */
    public void reset() {
        if (isEmpty) {
            setBackground(Color.gray);
            setBorder(border);
        } else {
            setBackground(Color.white);
            setBorder(noBorder);
        }
        repaint();
    }

    /**
     * @author Stephen Belden
     */
    public void debugPrint() {
        System.out.println("Tile with ID: " + ID + " & isEmpty = "
                + (isEmpty ? "true" : "false") + " has orientation " + orient +
                " holds these lines:");
        if (ID != -1) {
            for (Line l : lines)
                if (l != null)
                    l.debugPrint();
        }
    }

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
     * @author Stephen Belden
     * @author Colin Riley
     */
    @Override
    public void mousePressed(MouseEvent arg0) {
        if (SwingUtilities.isLeftMouseButton(arg0)) {
            Main.game.setLeftClicked(this);
        } else if (SwingUtilities.isRightMouseButton(arg0)) {
            Main.game.setRightClicked(this);
        }
        if (Main.verbose) {
            System.out.print("initialTileState ID's : ");
            for (Tile t : Main.initialTileState) {
                System.out.print(t.getID() + ", ");
            }
            System.out.println();
        }
    }

    /**
     * @author Neil Carrico
     */
    @Override
    public void mouseReleased(MouseEvent arg0) {
        // Like the author of this function, this function body does nothing
    }
}
