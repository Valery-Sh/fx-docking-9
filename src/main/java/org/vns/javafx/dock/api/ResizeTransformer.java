package org.vns.javafx.dock.api;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 *
 * @author Valery
 */
public class ResizeTransformer {

    private final DoubleProperty mouseX = new SimpleDoubleProperty();
    private final DoubleProperty mouseY = new SimpleDoubleProperty();

    private Cursor cursor;
    private Stage stage;
    
    private Set<Cursor> cursorTypes = new HashSet<>();
    
            
    public ResizeTransformer() {
        Collections.addAll(cursorTypes,
        Cursor.S_RESIZE,Cursor.E_RESIZE,Cursor.N_RESIZE,Cursor.W_RESIZE,
        Cursor.SE_RESIZE,Cursor.NE_RESIZE,Cursor.SW_RESIZE,Cursor.NW_RESIZE);

    }
    
    private void setCursorTypes(Cursor... cursors) {
        cursorTypes.clear();
        Collections.addAll(this.cursorTypes, cursors);
    }
    
    public void resize(double x, double y) {
        if ( ! cursorTypes.contains(cursor) ) {
            //return;
        }
        double xDelta = 0, yDelta = 0, wDelta = 0, hDelta = 0;

        double curX = mouseX.get();
        double curY = mouseY.get();
        if (cursor == Cursor.S_RESIZE) {
            hDelta = y - this.mouseY.get();
            curY = y;
        } else if (cursor == Cursor.E_RESIZE) {
            wDelta = x - this.mouseX.get();
            curX = x;
        } else if (cursor == Cursor.N_RESIZE) {
            hDelta = this.mouseY.get() - y;
            yDelta = -hDelta;
            curY = y;
        } else if (cursor == Cursor.W_RESIZE) {
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            curX = x;
        } else if (cursor == Cursor.SE_RESIZE) {
            hDelta = y - this.mouseY.get();
            curY = y;
            wDelta = x - this.mouseX.get();
            curX = x;

        } else if (cursor == Cursor.NE_RESIZE) {
            hDelta = this.mouseY.get() - y;
            wDelta = x - this.mouseX.get();
            yDelta = -hDelta;
            curX = x;
            curY = y;
        } else if (cursor == Cursor.SW_RESIZE) {
            hDelta = y - this.mouseY.get();
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            curX = x;
            curY = y;
        } else if (cursor == Cursor.NW_RESIZE) {
            hDelta = this.mouseY.get() - y;
            wDelta = this.mouseX.get() - x;
            xDelta = -wDelta;
            yDelta = -hDelta;
            curX = x;
            curY = y;
        }

        if (wDelta + stage.getWidth() >= stage.getMinWidth()) {
            stage.setX(xDelta + stage.getX());
            stage.setWidth(wDelta + stage.getWidth());
            mouseX.set(curX);
        }
        if (hDelta + stage.getHeight() >= stage.getMinHeight()) {
            stage.setY(yDelta + stage.getY());
            stage.setHeight(hDelta + stage.getHeight());
            mouseY.set(curY);
        }
    }

    public void resize(MouseEvent ev) {
        resize(ev.getScreenX(), ev.getScreenY());
    }

    public Stage getStage() {
        return stage;
    }

    public void start(MouseEvent ev, Stage stage, Cursor cursor, Cursor... supportedCursors) {
        setCursorTypes(supportedCursors);
        this.mouseX.set(ev.getScreenX());
        this.mouseY.set(ev.getScreenY());

        this.cursor = cursor;
        this.stage = stage;
    }

    public static Cursor cursorBy(double nodeX, double nodeY, double width, double height, double left, double right, double top, double bottom, Cursor... supported) {
        boolean e, w, n, s;
        Cursor cursor = Cursor.DEFAULT;
        w = nodeX < left;
        e = nodeX > width - right;
        n = nodeY < top;
        s = nodeY > height - bottom;

/*        System.err.println("!!! cursorBy: ins.getLeft()=" + left);        
        System.err.println("!!! cursorBy: ins.getRight()=" + right);        
        System.err.println("!!! cursorBy: ins.getTop()=" + top);        
        System.err.println("!!! cursorBy: ins.getBotton()=" + bottom);        
        System.err.println("!!! cursorBy: r.getWidth() =" + width);        
        System.err.println("!!! cursorBy: r.getHeight()=" + height);                
        System.err.println("!!! cursorBy: nodeX =" + nodeX);        
        System.err.println("!!! cursorBy: nodeY =" + nodeY);        
        
        System.err.println("-------------------------------------------------");                
*/        
        if (w) {
            if (n) {
                cursor = Cursor.NW_RESIZE;
            } else if (s) {
                cursor = Cursor.SW_RESIZE;
            } else {
                cursor = Cursor.W_RESIZE;
            }
        } else if (e) {
            if (n) {
                cursor = Cursor.NE_RESIZE;
            } else if (s) {
                cursor = Cursor.SE_RESIZE;
            } else {
                cursor = Cursor.E_RESIZE;
            }
        } else if (n) {
            cursor = Cursor.N_RESIZE;
        } else if (s) {
            cursor = Cursor.S_RESIZE;
        }
        Cursor retval = Cursor.DEFAULT;
        for ( Cursor c : supported) {
            if ( c.equals(cursor) ) {
                retval = cursor;
                break;
            }
        }
        return cursor;
    }

    public static Cursor cursorBy(MouseEvent ev, double width, double height, double left, double right, double top, double bottom) {
        return cursorBy(ev.getX(), ev.getY(), width, height, left, right, top, bottom);
    }
    /**
     * !!!!!!!!!
     * @param ev
     * @param r
     * @return 
     */
    public static Cursor cursorBy(MouseEvent ev, Region r) {
        double x, y, w, h;
        
        Insets ins = r.getPadding();
        if (ins == Insets.EMPTY) {
            //return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft() + 5, 15, 15, 15);
            return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft() + 5, 5, 5, 5);            
        }
/*        System.err.println("cursorBy: ins.getLeft()=" + ins.getLeft());        
        System.err.println("cursorBy: ins.getRight()=" + ins.getRight());        
        System.err.println("cursorBy: ins.getTop()=" + ins.getTop());        
        System.err.println("cursorBy: ins.getBotton()=" + ins.getBottom());        
        System.err.println("cursorBy: r.getWidth() =" + r.getWidth());        
        System.err.println("cursorBy: r.getHeight()=" + r.getHeight());                
        System.err.println("cursorBy: nodeX =" + ev.getX());        
        System.err.println("cursorBy: nodeY =" + ev.getY());        
        System.err.println("cursorBy: r.getHeight()=" + r.getHeight());                
        
        System.err.println("-------------------------------------------------");                
  */      
        
        
        return cursorBy(ev, r.getWidth(), r.getHeight(), ins.getLeft(), ins.getRight(), ins.getTop(), ins.getBottom());
    }
}
