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
 * The version of Main.java we are using now is largely unmodified from
 * the original.
 */

package game;

import java.awt.*;
import java.io.File;

import javax.swing.*;

/**
 * @author Kim Buckner Modifications by the group as a whole
 */
public class Main {
    // Globally accessible:
    public static File defaultPath = new File("default.mze");

    // Set this to true if you want debug output printed to the console,
    // or if you want tileID's to be displayed on the maze.
    public static boolean verbose = true;

    // There should only be one game, and everything needs access to it.
    public static GameWindow game;

    // A game state needs to be stored globally for the reset button,
    // so that the same state can be restored when the gameWindow is recreated
    public static Tile[] initialTileState = new Tile[16];

    public static void main(String[] args) {
        // This is the play area
        game = new GameWindow("Group E Maze");

        // So the debate here was, do I make the GameWindow object the game
        // or do I make main() the game, manipulating a window?
        // Should GameWindow methods know what they store?
        // Answer is, have the "game" do it.
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.getContentPane().setBackground(Color.cyan);
        game.setUp(defaultPath, true, true);

        game.setVisible(true);

        try {
            // The 4 that installed on Linux here
            // May have to test on Windows boxes to see what is there.
            UIManager.setLookAndFeel(
                    "javax.swing.plaf.nimbus.NimbusLookAndFeel");
            // This is the "Java" or CrossPlatform version and the default
            // UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            // Linux only
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            // really old style Motif
            // UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        } catch (UnsupportedLookAndFeelException e) {
            // handle possible exception
        } catch (ClassNotFoundException e) {
            // handle possible exception
        } catch (InstantiationException e) {
            // handle possible exception
        } catch (IllegalAccessException e) {
            // handle possible exception
        }
    }
};
