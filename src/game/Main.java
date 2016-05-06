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
 * @version May 2, 2016
 * 
 * The version of Main.java we are using now is largely unmodified from
 * the original.
 */

package game;

import java.awt.Color;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Template by:
 * @author Kim Buckner
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
    public static Tile[] initialGridState = new Tile[16];
    public static Tile[] writeTileArray = new Tile[16];

    public static void main(String[] args) {
        // This is the play area
        game = new GameWindow("Group E Maze");
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.getContentPane().setBackground(Color.cyan);
        game.setUp(defaultPath, true, true);
        game.reset();
        game.setVisible(true);
        

        try {
            UIManager.setLookAndFeel(
                    "javax.swing.plaf.nimbus.NimbusLookAndFeel");
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
