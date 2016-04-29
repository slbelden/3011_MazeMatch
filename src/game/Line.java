/**
 * Class for holding data about a single line on a single tile.
 * Immutable after construction.
 * 
 * @author Stephen Belden
 * @version April 28, 2016
 *
 */

package game;

import java.awt.Point;

public class Line {
    private Point begin;
    private Point end;
    
    public Line(Point begin, Point end){
        this.begin = begin;
        this.end = end;
    }
    
    public Point getBegin(){
        return begin;
    }
    
    public Point getEnd() {
        return end;
    }
    
    public void debugPrint() {
        System.out.println("\tLine from " + begin + " to " + end);
    }
}
