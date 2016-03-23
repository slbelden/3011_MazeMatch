/**
 * @author James Scott
 * @author Colin Riley
 * @author Stephen Belden
 * @author Shaya Wolf
 * @author Neil Carrico
 * Date: March 23, 2016
 **/
package game;

import java.awt.Point;

import javax.swing.JLabel;

public class Tile extends JLabel{
    /**
     * because it is a serializable object, need this or javac complains a lot
     */
    private static final long serialVersionUID = 1L;
       
    private int ID;
    private String text;
    private Point location;
    
    public Tile(int x, String i, Point p){        
        ID = x;
        text = i;
        location = p;
    };
    
    public Point getLoc(){
        return location;
    }
    
    public void setLoc(int x, int y){
        
    }
    
    public int getID(){
        return ID;
    }
    
    public void setID(int x){
        
    }
    
    public String getText(){
        return text;
    }
    
    public void setText(String s){
        
    }
    
}
